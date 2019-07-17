package domain;

import computation.TeamsCalculator;
import computation.TeamsGenerator;
import org.junit.Assert;
import org.junit.Test;
import utils.PlayersParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CompositionTest {

    @Test
    public void testShuffle() throws IOException {
        File csvFile = new File("src/test/resources/players.csv");
        PlayersParser playersParser = new PlayersParser(new FileReader(csvFile));

        TeamsGenerator teamsGenerator = playersParser.getTeamsGeneratorFromFile();
        int nbTeams = 3;
        TeamsCalculator teamsCalculator =
                teamsGenerator.getTeamsCalculator(nbTeams);
        Composition compo1 = new
                Composition(teamsGenerator.initTeams(nbTeams), teamsCalculator);
        Composition compo2 = compo1.shuffle();

        Assert.assertNotEquals(
                compo1.getTeams().stream().mapToDouble(t -> t.getGirlScore(1.0)),
                compo2.getTeams().stream().mapToDouble(t -> t.getGirlScore(1.0))
        );
    }
}
