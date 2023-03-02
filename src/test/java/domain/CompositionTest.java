package domain;

import computation.TeamsCalculator;
import computation.TeamsGenerator;
import org.junit.Assert;
import org.junit.Test;
import utils.FilePlayersParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CompositionTest {

    @Test
    public void testShuffle() throws IOException {
        File csvFile = new File("src/test/resources/players.csv");
        FilePlayersParser playersParser = new FilePlayersParser(new FileReader(csvFile), 9, 3);

        TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();
        int nbTeams = 3;
        TeamsCalculator teamsCalculator = teamsGenerator.getTeamsCalculator(nbTeams);
        Composition compo1 = new Composition(teamsGenerator.initTeams(nbTeams), teamsCalculator, 50, 200);
        Composition compo2 = compo1.shuffle();

        Assert.assertNotEquals(compo1.getTeams().stream().mapToDouble(t -> t.getGirlScore(1.0)),
                compo2.getTeams().stream().mapToDouble(t -> t.getGirlScore(1.0)));
    }

    @Test
    public void testScore() throws IOException {
        File csvFile = new File("src/test/resources/players.csv");
        FilePlayersParser playersParser = new FilePlayersParser(new FileReader(csvFile), 9, 3);

        TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();
        Composition composition = teamsGenerator.computeBestComposition(2, 20, 0, 0);
        List<Team> teams = composition.getTeams();
        Assert.assertEquals(2, teams.size());
        Team t1 = teams.get(0);
        Team t2 = teams.get(1);
        Assert.assertEquals(560.16, composition.getScore(), 0.1);
        Assert.assertEquals(t1.getPlayers().size(), t2.getPlayers().size());
        List<Double> skills = teamsGenerator.getSkillAverages();
        double s1 = t1.getSkillsScore(skills);
        double s2 = t2.getSkillsScore(skills);
        Assert.assertEquals(s1, s2, 35);
        double sa1 = t1.getSkillsAverage();
        double sa2 = t2.getSkillsAverage();
        Assert.assertEquals(sa1, sa2, 0.8);
        double expectedHandlerScore = teamsGenerator.getNbHandlers();
        Assert.assertEquals(t1.getHandlerScore(expectedHandlerScore), t2.getHandlerScore(expectedHandlerScore), 0.1);
    }

    @Test
    public void testScoreWithSize() throws IOException {
        File csvFile = new File("src/test/resources/players_withsize.csv");
        FilePlayersParser playersParser = new FilePlayersParser(new FileReader(csvFile), 7, 4);

        TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();
        Composition composition = teamsGenerator.computeBestComposition(2, 20, 0, 0);
        List<Team> teams = composition.getTeams();
        Assert.assertEquals(2, teams.size());
        Team t1 = teams.get(0);
        Team t2 = teams.get(1);
        Assert.assertEquals(2331.1, composition.getScore(), 0.1);
        Assert.assertEquals(t1.getPlayers().size(), t2.getPlayers().size());
        double ageExpected = teamsGenerator.getAgeAverage();
        Assert.assertEquals(30.4, ageExpected, 0.1);
        List<Double> skills = teamsGenerator.getSkillAverages();
        double s1 = t1.getSkillsScore(skills);
        double s2 = t2.getSkillsScore(skills);
        Assert.assertEquals(s1, s2, 500);
        double sa1 = t1.getSkillsAverage();
        double sa2 = t2.getSkillsAverage();
        Assert.assertEquals(sa1, sa2, 1.0);
        double expectedHandlerScore = teamsGenerator.getNbHandlers();
        Assert.assertEquals(t1.getHandlerScore(expectedHandlerScore), t2.getHandlerScore(expectedHandlerScore), 0.1);
    }

    @Test
    public void testScoreWithDay() throws IOException {
        File csvFile = new File("src/test/resources/players_with_day.csv");
        FilePlayersParser playersParser = new FilePlayersParser(new FileReader(csvFile), 8, 3);

        TeamsGenerator teamsGenerator = playersParser.getTeamsGenerator();
        Composition composition = teamsGenerator.computeBestComposition(2, 50, 20000, 0);
        List<Team> teams = composition.getTeams();
        Assert.assertEquals(2, teams.size());
        Team t1 = teams.get(0);
        Team t2 = teams.get(1);

        Assert.assertEquals(5, t1.getPlayersForDay(1).size());
        Assert.assertEquals(5, t1.getPlayersForDay(2).size());
        Assert.assertEquals(5, t2.getPlayersForDay(1).size());
        Assert.assertEquals(5, t2.getPlayersForDay(2).size());
    }
}
