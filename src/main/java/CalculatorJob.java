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
        options.addOption("nbTeams", true, "number of team to create");
        options.addOption("nbRuns", true, "number of shuffle to run");
        options.addOption("invalidTeamPenalty", true, "penalty added when a generated team is invalid");
        options.addOption("teammatePenalty", true, "penalty added when a player doesn't plays with its teammate");
        CommandLine commandline = commandParser.parse(options, args, false);
        PlayersParserInterface playersParser;
        if (commandline.hasOption("sheet")) {
            String sheetId = commandline.getOptionValue("sheet", "18JPdGOZwmIk9NYdi6KdcYTxEkDaOv3771VaR678jw2E");
            String range = commandline.getOptionValue("range", "Inscriptions!A8:Q88");
            playersParser = new SheetsPlayersParser(sheetId, range);
        } else {
            String file = commandline.getOptionValue("file", "players.csv");
            playersParser = new FilePlayersParser(new FileReader(file));
        }
        TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();

        int nbTeams = Integer.parseInt(commandline.getOptionValue("nbTeams", "6"));
        int nbRuns = Integer.parseInt(commandline.getOptionValue("nbRuns", "20"));
        int invalidTeamPenalty = Integer.parseInt(commandline.getOptionValue("invalidTeamPenalty", "200"));
        int teammatePenalty = Integer.parseInt(commandline.getOptionValue("teammatePenalty", "50"));
        Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns, invalidTeamPenalty,
                teammatePenalty);
        playersParser.write(bestComposition);
        System.out.println(bestComposition.getScoreForDay(1));
        System.out.println(bestComposition.getScoreForDay(2));
    }
}
