import computation.TeamsGenerator;
import domain.Composition;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import utils.PlayersParser;

import java.io.FileReader;
import java.io.IOException;

public class CalculatorJob {

    public static void main(String[] args) throws IOException, ParseException {
        CommandLineParser commandParser = new DefaultParser();
        Options options = new Options();
        options.addOption("file", true, "file to parse");
        options.addOption("nbTeams", true, "number of team to create");
        options.addOption("nbRuns", true, "number of shuffle to run");
        CommandLine commandline = commandParser.parse(options, args, false);
        String file = commandline.getOptionValue("file", "players.csv");

        PlayersParser playersParser = new PlayersParser(new FileReader(file));
        TeamsGenerator teamsGenerator = playersParser.getTeamsGeneratorFromFile();

        int nbTeams = Integer.valueOf(commandline.getOptionValue("nbTeams", "6"));
        int nbRuns = Integer.valueOf(commandline.getOptionValue("nbRuns", "20"));
        Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns);
        System.out.println(bestComposition);
    }
}
