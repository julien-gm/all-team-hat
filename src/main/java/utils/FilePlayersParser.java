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

    private static final int NUMBER_OF_COLUMNS_TO_SKIP = 7;
    private static Iterable<? extends CSVRecord> parser;

    public FilePlayersParser(FileReader file) throws IOException {
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
            player.setEmail(record.get("Email"));
            player.setClub(record.get("Club"));
            // player.setAge(Integer.parseInt(record.get("Age")));
            player.setGender(record.get("Sexe").startsWith("F") ? Player.Gender.FEMME : Player.Gender.HOMME);
            String handler = record.get("Handler");
            player.setHandler(handler.equals("oui") ? Player.Handler.YES
                    : handler.equals("non") ? Player.Handler.NO : Player.Handler.MAYBE);

            // Getting skills
            // Skipping the first 8 columns that we just read
            Iterator<String> iterator = record.iterator();
            for (int i = 0; i < NUMBER_OF_COLUMNS_TO_SKIP; i++) {
                iterator.next();
            }
            while (iterator.hasNext()) {
                player.getSkillsList().add(Double.parseDouble(iterator.next()));
            }
            allPlayers.add(player);
        }
        return new TeamsGenerator(allPlayers);
    }

    @Override
    public void write(Composition bestComposition) {
        System.out.println(bestComposition);
    }
}
