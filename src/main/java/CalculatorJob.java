import static utils.Parameters.*;

import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import computation.TeamsGenerator;
import domain.Composition;
import utils.FilePlayersParser;
import utils.PlayersParserInterface;
import utils.SheetsPlayersParser;

public class CalculatorJob {
    public static void main(String[] args) throws IOException, ParseException {
        CommandLineParser commandParser = new DefaultParser();
        Options options = new Options();
        options.addOption("file", true, "file to parse");
        options.addOption("sheet", true, "Google Spreadsheet id to parse");
        options.addOption("range", true, "Sheet name and range to lookup for player");
        options.addOption("nbTeams", true, "number of team to create");
        options.addOption("nbRuns", true, "number of shuffle to run");
        CommandLine commandline = commandParser.parse(options, args, false);
        PlayersParserInterface playersParser;

        if (Boolean.valueOf(sheet.getStringValue())) {
            String sheetId = inputSheetId.getStringValue();
            String range = inputSheetRange.getStringValue();
            playersParser = new SheetsPlayersParser(sheetId, range);
        } else {
            String file = commandline.getOptionValue("file", "players.csv");
            playersParser = new FilePlayersParser(new FileReader(file));
        }
        TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();

        int nbTeams = Integer.valueOf(teams.getStringValue());
        int nbRuns = Integer.valueOf(runs.getStringValue());
        Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns);
        playersParser.write(bestComposition);
    }
}
