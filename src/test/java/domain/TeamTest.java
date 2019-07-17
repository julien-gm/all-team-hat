package domain;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TeamTest {

    private static Team team;

    @BeforeClass
    public static void setup() {
        Player p1 = new Player();
        p1.setSkillsList(Arrays.asList(7.0, 7.0, 7.0));
        p1.setGender(Player.Gender.FEMME);
        p1.setHandler(Player.Handler.YES);
        p1.setClub("test");
        Player p2 = new Player();
        p2.setSkillsList(Arrays.asList(8.0, 8.0, 8.0));
        p2.setGender(Player.Gender.HOMME);
        p2.setHandler(Player.Handler.MAYBE);
        p2.setClub("test");
        Player p3 = new Player(false);
        team = new Team(Arrays.asList(p1, p2, p3));
    }

    @Test
    public void testGirlScore() {
        Assert.assertEquals(0.2, team.getGirlScore(1.2), 0.0001);
    }

    @Test
    public void testSportScore() {
        // TODO le 0.5 ?
        Assert.assertEquals(0.5, team.getSportScore(Arrays.asList(7.2, 7.7, 7.5)), 0.0001);
    }

    @Test
    public void testHandlerScore() {
        Assert.assertEquals(0.7, team.getHandlerScore(0.3), 0.0001);
    }

    @Test
    public void testNoHandlerScore() {
        Assert.assertEquals(6.0, team.getNoHandlerScore(1.2), 0.0001);
    }

    @Test
    public void testStdDev() {
        Assert.assertEquals(8.1, team.getStandardDeviation(Arrays.asList(7.2, 7.7, 7.5)), 0.1);
    }

    @Test
    public void testClubScore() {
        Map<String, Double> expectedClubScore = new HashMap<>();
        expectedClubScore.put("", 3.0);
        Assert.assertEquals(1.0, team.getClubScore(expectedClubScore), 0.0001);
    }
}
