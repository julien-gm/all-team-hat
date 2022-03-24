package computation;

import domain.Player;
import domain.Team;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TeamsCalculatorTest {

    private static TeamsCalculator teamCalculator;
    private static Team team1;
    private static Team team2;

    @BeforeClass
    public static void setup() {
        Player p1 = new Player();
        p1.setSkillsList(Arrays.asList(7.0, 7.0, 7.0));
        p1.setGender(Player.Gender.FEMME);
        p1.setHandler(Player.Handler.YES);
        p1.setClub("c1");
        Player p2 = new Player();
        p2.setSkillsList(Arrays.asList(8.0, 8.0, 8.0));
        p2.setGender(Player.Gender.HOMME);
        p2.setHandler(Player.Handler.MAYBE);
        p2.setClub("c2");
        Player p3 = new Player();
        p3.setSkillsList(Arrays.asList(6.0, 7.0, 5.0));
        p3.setGender(Player.Gender.FEMME);
        p3.setHandler(Player.Handler.YES);
        p3.setClub("c1");
        Player p4 = new Player();
        p4.setSkillsList(Arrays.asList(8.0, 9.0, 10.0));
        p4.setGender(Player.Gender.HOMME);
        p4.setHandler(Player.Handler.NO);
        p4.setClub("c2");

        team1 = new Team(Arrays.asList(p1, p2));
        team2 = new Team(Arrays.asList(p3, p4));
        Map<String, Double> expectedClubScore = new HashMap<>();
        expectedClubScore.put("c1", 1.0);
        expectedClubScore.put("c2", 1.0);
        teamCalculator = new TeamsCalculator(Arrays.asList(7.7, 7.6, 7.9), 0.4, 1.2, 0, expectedClubScore, 2, 1, 10);
    }

    @Test
    public void testTeamScore() {
        Assert.assertEquals(187.12, teamCalculator.getTeamScore(team1), 0.01);
        Assert.assertEquals(698.19, teamCalculator.getTeamScore(team2), 0.01);
    }

    private Player createDefaultPlayer() {
        Player player = new Player();
        player.setClub("toto");
        player.setGender(Player.Gender.HOMME);
        player.setHandler(Player.Handler.NO);
        player.setSkillsList(Arrays.asList(Math.random() * 10, Math.random() * 10, Math.random() * 10));
        return player;
    }

    @Test
    public void testTeamScoreWithDays() {
        Player p1 = createDefaultPlayer();
        p1.setDay(2);
        Player p2 = createDefaultPlayer();
        p2.setDay(1);
        Player p3 = createDefaultPlayer();
        Team t1 = new Team(Arrays.asList(p1, p2, p3));
        Assert.assertTrue(teamCalculator.getTeamScore(t1) > 0);
    }

    @Test
    public void testTeamScoreWithDaysKO() {
        Player p1 = createDefaultPlayer();
        p1.setDay(2);
        Player p2 = createDefaultPlayer();
        p2.setDay(2);
        Player p3 = createDefaultPlayer();
        Team t1 = new Team(Arrays.asList(p1, p2, p3));
        t1.initSkills();
        Assert.assertFalse(teamCalculator.getTeamScore(t1) < 200);
    }
}
