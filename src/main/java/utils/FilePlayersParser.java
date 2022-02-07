package utils;

import computation.TeamsGenerator;
import domain.Composition;
import domain.Player;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilePlayersParser implements PlayersParserInterface {

    private static final int NUMBER_OF_COLUMNS_TO_SKIP = 9;
    private static final int NUMBER_OF_SKILLS = 3;
    private static Iterable<? extends CSVRecord> parser;
    private final int columnToSkip;
    private final int numberOfSkill;

    public FilePlayersParser(FileReader file) throws IOException {
        this(file, NUMBER_OF_COLUMNS_TO_SKIP, NUMBER_OF_SKILLS);
    }

    public FilePlayersParser(FileReader file, int columnToSkip, int numberOfSkill) throws IOException {
        this.columnToSkip = columnToSkip;
        this.numberOfSkill = numberOfSkill;
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
            for (int i = 0; i < this.columnToSkip; i++) {
                iterator.next();
            }
            int skillNumber = 0;
            while (iterator.hasNext() && skillNumber < this.numberOfSkill) {
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
        System.out.println(bestComposition);
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
