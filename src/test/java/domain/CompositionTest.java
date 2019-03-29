package domain;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import computation.TeamsCalculator;
import computation.TeamsGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CompositionTest {

    @Test
    public void testShuffle() throws IOException {
        File csvFile = new File("src/test/resources/players.csv");
        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        List<Player> allPlayers = Arrays.asList(mapper.readerFor(Player[].class).with(bootstrapSchema).readValue(csvFile));

        TeamsGenerator teamsGenerator = new TeamsGenerator(allPlayers);
        int nbTeams = 3;
        TeamsCalculator teamsCalculator = teamsGenerator.getTeamsCalculator(nbTeams);
        Composition compo1 = new Composition(teamsGenerator.initTeams(nbTeams), teamsCalculator);
        Composition compo2 = compo1.shuffle();

        Assert.assertNotEquals(
            compo1.getTeams().stream().mapToDouble(t -> t.getGirlScore(1.0)),
            compo2.getTeams().stream().mapToDouble(t -> t.getGirlScore(1.0))
        );
    }
}
