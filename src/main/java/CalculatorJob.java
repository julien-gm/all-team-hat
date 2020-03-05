import computation.TeamsGenerator;
import domain.Composition;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import utils.FilePlayersParser;
import utils.PlayersParserInterface;
import utils.SheetsPlayersParser;

import java.io.FileReader;
import java.io.IOException;

public class CalculatorJob {

    public static void main(String[] args) throws IOException, ParseException {
        CommandLineParser commandParser = new DefaultParser();
        Options options = new Options();
        options.addOption("file", true, "file to parse");
        options.addOption("sheet", true, "Google Spreadsheet id to parse");
        options.addOption("range", true, "Sheet name and range to lookup for player");
        options.addOption("day", true, "Day to generate team for");
        options.addOption("nbTeams", true, "number of team to create");
        options.addOption("nbRuns", true, "number of shuffle to run");
        CommandLine commandline = commandParser.parse(options, args, false);
        PlayersParserInterface playersParser;
        if (commandline.hasOption("sheet")) {
            String sheetId = commandline.getOptionValue("sheet", "1oRkdNy4vHwiSSEo7nTlfZxN4aQepo0Eex0fgaLna0FQ");
            String range = commandline.getOptionValue("range", "Inscriptions!A10:M84");
            int day = Integer.parseInt(commandline.getOptionValue("day", "1"));
            playersParser = new SheetsPlayersParser(sheetId, range, day);
        } else {
            String file = commandline.getOptionValue("file", "players.csv");
            playersParser = new FilePlayersParser(new FileReader(file));
        }
        TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();

        int nbTeams = Integer.valueOf(commandline.getOptionValue("nbTeams", "6"));
        int nbRuns = Integer.valueOf(commandline.getOptionValue("nbRuns", "20"));
        Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns);
        playersParser.write(bestComposition);
    }
}
