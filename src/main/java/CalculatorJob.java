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
        options.addOption("nbSkills", true, "Number of skills");
        options.addOption("skillFirstCol", true, "First column number for skills");
        options.addOption("firstNameCol", true, "Column firstname name");
        options.addOption("lastNameCol", true, "Column lastname name");
        options.addOption("nicknameCol", true, "Column nickname name");
        options.addOption("clubCol", true, "Column club name");
        options.addOption("ageCol", true, "Column age name");
        options.addOption("genderCol", true, "Column gender name");
        options.addOption("emailCol", true, "Column email name");
        options.addOption("handlingCol", true, "Column handling name");
        options.addOption("handlerValue", true, "Value when handler");
        options.addOption("middleValue", true, "Value when middle");
        options.addOption("teamMateColName", true, "Column teammate name");
        CommandLine commandline = commandParser.parse(options, args, false);
        PlayersParserInterface playersParser;

        int nbTeams = Integer.parseInt(commandline.getOptionValue("nbTeams", "6"));
        int nbRuns = Integer.parseInt(commandline.getOptionValue("nbRuns", "10"));
        int nbSkills = Integer.parseInt(commandline.getOptionValue("nbSkills", "3"));
        int skillFirstCol = Integer.parseInt(commandline.getOptionValue("skillFirstCol", "9"));
        int invalidTeamPenalty = Integer.parseInt(commandline.getOptionValue("invalidTeamPenalty", "200"));
        int teammatePenalty = Integer.parseInt(commandline.getOptionValue("teammatePenalty", "50"));
        String firstnameColName = commandline.getOptionValue("firstNameCol", PlayersParserInterface.FIRST_NAME);
        String lastnameColName = commandline.getOptionValue("lastNameCol", PlayersParserInterface.LAST_NAME);
        String nicknameColName = commandline.getOptionValue("nicknameCol", PlayersParserInterface.NICKNAME);
        String clubColName = commandline.getOptionValue("clubCol", PlayersParserInterface.CLUB);
        String ageColName = commandline.getOptionValue("ageCol", PlayersParserInterface.AGE);
        String genderColName = commandline.getOptionValue("genderCol", PlayersParserInterface.GENDER);
        String emailColName = commandline.getOptionValue("emailCol", PlayersParserInterface.EMAIL);
        String handlingColName = commandline.getOptionValue("handlingCol", PlayersParserInterface.HANDLING);
        String handler = commandline.getOptionValue("handlerValue", PlayersParserInterface.HANDLER);
        String middle = commandline.getOptionValue("middleValue", PlayersParserInterface.MIDDLE);
        String teamMateColName = commandline.getOptionValue("teamMateColName", PlayersParserInterface.TEAMMATE);

        if (commandline.hasOption("sheet")) {
            String sheetId = commandline.getOptionValue("sheet", "18JPdGOZwmIk9NYdi6KdcYTxEkDaOv3771VaR678jw2E");
            String range = commandline.getOptionValue("range", "Inscriptions!A8:Q88");
            playersParser = new SheetsPlayersParser(sheetId, range, skillFirstCol, nbSkills, teamMateColName);
        } else {
            String file = commandline.getOptionValue("file", "players.csv");
            playersParser = new FilePlayersParser(new FileReader(file), skillFirstCol, nbSkills, teamMateColName,
                    firstnameColName, lastnameColName, nicknameColName, clubColName, ageColName, emailColName,
                    genderColName, handlingColName, handler, middle);
        }
        TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();
        Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns, invalidTeamPenalty,
                teammatePenalty);
        playersParser.write(bestComposition);
        System.out.println(bestComposition.getScoreForDay(1));
        System.out.println(bestComposition.getScoreForDay(2));
    }
}
