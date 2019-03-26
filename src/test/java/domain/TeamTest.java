package domain;

import domain.Player;
import domain.Team;
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
        p1.setSpeed(7);
        p1.setEndurance(7);
        p1.setTech(7);
        p1.setGender(Player.Gender.F);
        p1.setHandler(Player.Handler.YES);
        p1.setClub("test");
        Player p2 = new Player();
        p2.setSpeed(8);
        p2.setEndurance(8);
        p2.setTech(8);
        p2.setGender(Player.Gender.M);
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
    public void testEnduranceScore() {
        Assert.assertEquals(0.3, team.getEnduranceScore(7.2), 0.0001);
    }

    @Test
    public void testSpeedScore() {
        Assert.assertEquals(0.2, team.getSpeedScore(7.7), 0.0001);
    }

    @Test
    public void testTechScore() {
        Assert.assertEquals(0.0, team.getTechScore(7.5), 0.0001);
    }

    @Test
    public void testHandlerScore() {
        Assert.assertEquals(0.7, team.getHandlerScore(0.3), 0.0001);
    }

    @Test
    public void testNoHandlerScore() {
        Assert.assertEquals(3.6, team.getNoHandlerScore(1.2), 0.0001);
    }

    @Test
    public void testClubScore() {
        Map<String, Double> expectedClubScore = new HashMap<>();
        expectedClubScore.put("", 3.0);
        Assert.assertEquals(1.0, team.getClubScore(expectedClubScore), 0.0001);
    }
}