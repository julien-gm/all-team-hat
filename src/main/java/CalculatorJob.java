import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import computation.TeamsGenerator;
import domain.Composition;
import domain.Player;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CalculatorJob {

    public static void main(String[] args) throws IOException, ParseException {
        CommandLineParser commandParser = new DefaultParser();
        Options options = new Options();
        options.addOption("file", true, "file to parse");
        options.addOption("nbTeams", true, "number of team to create");
        options.addOption("nbRuns", true, "number of shuffle to run");
        CommandLine commandline = commandParser.parse(options, args, false);

        String file = commandline.getOptionValue("file", "players.csv");
        File csvFile = new File("src/main/resources/" + file);
        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        List<Player> allPlayers = Arrays.asList(mapper.readerFor(Player[].class).with(bootstrapSchema).readValue(csvFile));

        TeamsGenerator teamsGenerator = new TeamsGenerator(allPlayers);
        Integer nbTeams = Integer.valueOf(commandline.getOptionValue("nbTeams", "6"));
        Integer nbRuns = Integer.valueOf(commandline.getOptionValue("nbRuns", "20"));
        Composition bestComposition = teamsGenerator.computeBestComposition(nbTeams, nbRuns);
        System.out.println(bestComposition);
    }
}
