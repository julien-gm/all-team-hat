package computation;

import computation.TeamsCalculator;
import domain.Player;
import domain.Team;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamsCalculatorTest {

    private static TeamsCalculator teamCalculator;
    private static List<Team> teams;
    private static Team team1;
    private static Team team2;

    @BeforeClass
    public static void setup() {
        Player p1 = new Player();
        p1.setSpeed(7);
        p1.setEndurance(7);
        p1.setTech(7);
        p1.setGender(Player.Gender.F);
        p1.setHandler(Player.Handler.YES);
        p1.setAge(29);
        p1.setClub("c1");
        Player p2 = new Player();
        p2.setSpeed(8);
        p2.setEndurance(8);
        p2.setTech(8);
        p2.setGender(Player.Gender.M);
        p2.setHandler(Player.Handler.MAYBE);
        p2.setAge(30);
        p2.setClub("c2");
        Player p3 = new Player();
        p3.setTech(5);
        p3.setEndurance(7);
        p3.setSpeed(6);
        p3.setGender(Player.Gender.F);
        p3.setHandler(Player.Handler.YES);
        p3.setAge(27);
        p3.setClub("c1");
        Player p4 = new Player();
        p4.setTech(10);
        p4.setEndurance(9);
        p4.setSpeed(8);
        p4.setGender(Player.Gender.M);
        p4.setHandler(Player.Handler.NO);
        p4.setAge(26);
        p4.setClub("c2");

        team1 = new Team(Arrays.asList(p1, p2));
        // Girl 1 : 0.2
        // Endurance 7.5 : 0.2
        // Tech 7.5 : 0.1
        // Speed 7.5 : 0.4
        // domain.Handler 1 : 0.2
        // No handler 0 : 0.4
        // TOTAL : 1.5
        team2 = new Team(Arrays.asList(p3, p4));
        // Girl 1 : 0.2
        // Tech 7.5 : 0.2
        // Endurance 8 : 0.4
        // Speed 7 : 0.9
        // domain.Handler 1 : 0.2
        // No handler 1 : 0.6
        // TOTAL : 2.5
        teams = Arrays.asList(team1, team2);
        Map<String, Double> expectedClubScore = new HashMap<>();
        expectedClubScore.put("c1", 1.0);
        expectedClubScore.put("c2", 1.0);
        teamCalculator = new TeamsCalculator(
            7.7,
            7.6,
            7.9,
            0.4,
            1.2,
            28,
            expectedClubScore);
    }

    @Test
    public void testTeamScore() {
        Assert.assertEquals(7.15, teamCalculator.getTeamScore(team1), 0.0001);
        Assert.assertEquals(8.55, teamCalculator.getTeamScore(team2), 0.0001);
    }

    @Test
    public void testCompute() {
        Assert.assertEquals(15.7, teamCalculator.compute(teams), 0.0001);
    }
}
