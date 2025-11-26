package utils;

import computation.TeamsGenerator;
import domain.Composition;
import domain.Player;
import domain.Team;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilePlayersParser implements PlayersParserInterface {

    private final int firstSkillCol;
    private final int nbSkills;
    private static Iterable<? extends CSVRecord> parser;
    private final String teamMateColName;
    private final String firstnameColName;
    private final String lastnameColName;
    private final String nicknameColName;
    private final String clubColName;
    private final String ageColName;
    private final String emailColName;
    private final String genderColName;
    private final String handlingColName;
    private final String handler;
    private final String middle;
    private boolean use_day = false;

    public FilePlayersParser(FileReader file, int columnToSkip, int numberOfSkill, String teamMateColName,
            String firstnameColName, String lastnameColName, String nicknameColName, String clubColName,
            String ageColName, String emailColName, String genderColName, String handlingColName, String handler,
            String middle) throws IOException {
        this.firstSkillCol = columnToSkip;
        this.nbSkills = numberOfSkill;
        this.teamMateColName = teamMateColName;
        this.firstnameColName = firstnameColName;
        this.lastnameColName = lastnameColName;
        this.nicknameColName = nicknameColName;
        this.clubColName = clubColName;
        this.ageColName = ageColName;
        this.emailColName = emailColName;
        this.genderColName = genderColName;
        this.handlingColName = handlingColName;
        this.handler = handler;
        this.middle = middle;
        parser = CSVParser.parse(file, CSVFormat.RFC4180.withHeader());
    }

    public FilePlayersParser(FileReader file, int columnToSkip, int numberOfSkill) throws IOException {
        this(file, columnToSkip, numberOfSkill, TEAMMATE, FIRST_NAME, LAST_NAME, NICKNAME, CLUB, AGE, EMAIL, GENDER,
                HANDLING, HANDLER, MIDDLE);
    }

    public TeamsGenerator getTeamsGenerator() {
        List<Player> allPlayers = new ArrayList<>();

        for (CSVRecord record : parser) {
            // Reading with headers so we can get the values via the name
            // and they can be in any order (as long as the skills are last)
            Player player = new Player(record.get(this.nicknameColName));
            player.setLastName(record.get(this.lastnameColName));
            player.setFirstName(record.get(this.firstnameColName));
            try {
                player.setEmail(record.get(this.emailColName));
            } catch (IllegalArgumentException e) {
                player.setEmail("");
            }
            player.setClub(record.get(this.clubColName));
            player.setAge(Integer.parseInt(record.get(this.ageColName)));
            player.setGender(
                    record.get(this.genderColName).startsWith("F") ? Player.Gender.FEMME : Player.Gender.HOMME);
            String handler = record.get(this.handlingColName);
            player.setHandler(handler.equals(this.handler) ? Player.Handler.YES
                    : handler.equals(this.middle) ? Player.Handler.NO : Player.Handler.MAYBE);

            // Getting skills
            // Skipping the first 8 columns that we just read
            Iterator<String> iterator = record.iterator();
            for (int i = 0; i < firstSkillCol; i++) {
                iterator.next();
            }
            int skillNumber = 0;
            while (iterator.hasNext() && skillNumber < nbSkills) {
                String value = iterator.next();
                player.getSkillsList().add(Double.parseDouble(value));
                skillNumber++;
            }
            setTeamMate(allPlayers, record, player);
            if (record.isSet(DAY)) {
                this.use_day = true;
                String day = record.get(DAY);
                if (!day.equals("")) {
                    player.setDay(Integer.parseInt(day));
                }
            }
            if (allPlayers.stream().anyMatch(p -> p.getEmail().equals(player.getEmail()))) {
                System.err.println("Warning, you have several players with the same email: " + player.getEmail());
            }
            if (allPlayers.stream()
                    .anyMatch(p -> (p.getFirstName().equals(player.getFirstName())
                            && p.getLastName().equals(player.getLastName()))
                            || p.getNickName().equals(player.getNickName()))) {
                System.err.println(
                        String.format("Warning, you have several players with the same nickname : \"%s\" (%s %s)",
                                player.getNickName(), player.getFirstName(), player.getLastName()));
            }
            allPlayers.add(player);
        }
        return new TeamsGenerator(allPlayers);
    }

    @Override
    public void write(Composition bestComposition) {
        try {
            int teamIndex = 1;
            String runtime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            runtime = runtime.replace(":", ".");
            String teams = "";
            for (Team t : bestComposition.getTeams()) {
                String fileName = String.format("./run_%s_team_%d.csv", runtime, teamIndex);
                FileWriter fw = new FileWriter(fileName);
                String teamCSV = t.toCSV(bestComposition.getTeams(), teamIndex, this.use_day);
                teams += teamCSV;
                fw.write(teamCSV);
                teamIndex++;
                fw.close();
            }
            FileWriter fw = new FileWriter(String.format("./run_%s_teams.csv", runtime));
            fw.write(teams);
            fw.close();
            fw = new FileWriter(String.format("./run_%s_info.txt", runtime));
            fw.write(toString());
            fw.close();
            // copy team file with a generic name
            FileUtils.copyFile(new File(String.format("./run_%s_teams.csv", runtime)), new File("./last_run.csv"));
        } catch (IOException e) {
            System.err.println("Unable to write composition");
            System.err.println(e.getMessage());
            System.out.println(bestComposition);
        }
    }

    private void setTeamMate(List<Player> allPlayers, CSVRecord record, Player player) {
        try {
            String nickName = record.get(teamMateColName);
            if (nickName != null && !nickName.equals("")) {
                allPlayers.stream().filter(p -> p.getNickName().equalsIgnoreCase(nickName)).findFirst()
                        .ifPresent(player::setTeamMate);
            }
            if (player.getTeamMate() != null) {
                System.out.println(player.getNickName() + " teammate is " + player.getTeamMate().getNickName());
            }
        } catch (Exception ignored) {
        }
    }
}
