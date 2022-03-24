package utils;

import computation.TeamsGenerator;
import domain.Composition;
import domain.Player;
import domain.Team;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

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

    public FilePlayersParser(FileReader file, int columnToSkip, int numberOfSkill) throws IOException {
        this.firstSkillCol = columnToSkip;
        this.nbSkills = numberOfSkill;
        parser = CSVParser.parse(file, CSVFormat.RFC4180.withHeader());
    }

    public TeamsGenerator getTeamsGenerator() {
        List<Player> allPlayers = new ArrayList<>();

        for (CSVRecord record : parser) {
            // Reading with headers so we can get the values via the name
            // and they can be in any order (as long as the skills are last)
            Player player = new Player(record.get("Pseudo"));
            player.setLastName(record.get("Nom"));
            player.setFirstName(record.get("Prénom"));
            player.setEmail(record.get("Adresse e-mail"));
            player.setClub(record.get("Club"));
            player.setAge(Integer.parseInt(record.get("Age")));
            player.setGender(record.get("Sexe").startsWith("F") ? Player.Gender.FEMME : Player.Gender.HOMME);
            String handler = record.get("Handler?");
            player.setHandler(handler.equals("Oui") ? Player.Handler.YES
                    : handler.equals("Non") ? Player.Handler.NO : Player.Handler.MAYBE);

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
            if (record.isSet("Jour")) {
                String day = record.get("Jour");
                if (!day.equals("")) {
                    player.setDay(Integer.parseInt(day));
                }
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
            for (Team t : bestComposition.getTeams()) {
                String fileName = String.format("run_%s_team_%d.csv", runtime, teamIndex);
                FileWriter fw = new FileWriter(fileName);
                teamIndex++;
                fw.write(t.toCSV());
                fw.close();
            }
            FileWriter fw = new FileWriter(String.format("run_%s_info.txt", runtime));
            fw.write(toString());
        } catch (IOException e) {
            System.err.println("Unable to write composition");
            System.err.println(e.getMessage());
            System.out.println(bestComposition);

        }
    }

    private void setTeamMate(List<Player> allPlayers, CSVRecord record, Player player) {
        try {
            String nickName = record.get("Quels sont le nom et prénom de ton binôme?");
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
