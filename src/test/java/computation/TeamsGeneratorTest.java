package computation;

import domain.Player;
import domain.Team;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TeamsGeneratorTest {

    private static TeamsGenerator teamGenerator;
    private static Player p1;
    private static Player p2;
    private static Player p3;
    private static Player p4;
    private static Player p5;

    @BeforeClass
    public static void setup() {
        p1 = new Player();
        p1.setSkillsList(Arrays.asList(7.0, 7.0, 7.0));
        p1.setGender(Player.Gender.FEMME);
        p1.setHandler(Player.Handler.YES);
        p1.setClub("c1");
        p1.setNickName("p1");
        p2 = new Player();
        p2.setSkillsList(Arrays.asList(8.0, 8.0, 8.0));
        p2.setGender(Player.Gender.HOMME);
        p2.setHandler(Player.Handler.MAYBE);
        p2.setClub("c2");
        p2.setNickName("p2");
        p3 = new Player();
        p3.setSkillsList(Arrays.asList(7.0, 7.0, 7.0));
        p3.setGender(Player.Gender.HOMME);
        p3.setHandler(Player.Handler.YES);
        p3.setClub("c1");
        p3.setDay(2);
        p3.setNickName("p3");
        p4 = new Player();
        p4.setSkillsList(Arrays.asList(9.0, 9.0, 9.0));
        p4.setGender(Player.Gender.FEMME);
        p4.setHandler(Player.Handler.NO);
        p4.setClub("c1");
        p4.setNickName("p4");
        p5 = new Player();
        p5.setSkillsList(Arrays.asList(8.0, 8.0, 8.0));
        p5.setGender(Player.Gender.HOMME);
        p5.setHandler(Player.Handler.MAYBE);
        p5.setClub("c3");
        p5.setDay(1);
        p5.setNickName("p5");
        teamGenerator = new TeamsGenerator(Arrays.asList(p1, p2, p3, p4, p5));
    }

    @Test
    public void testGetNumberOfPlayerByTeam() {
        Assert.assertEquals(1, teamGenerator.getNumberOfPlayersByTeam(5));
        Assert.assertEquals(5, teamGenerator.getNumberOfPlayersByTeam(1));
        Assert.assertEquals(2, teamGenerator.getNumberOfPlayersByTeam(3));
    }

    @Test
    public void testGetExpectedPlayersByTeam() {
        Assert.assertEquals(2, teamGenerator.getExpectedPlayersByTeam(2));
        Assert.assertEquals(4, teamGenerator.getExpectedPlayersByTeam(1));
    }

    @Test
    public void testInitTeams() {
        int nbOfTeam = 2;
        List<Team> teams = teamGenerator.initTeams(nbOfTeam);
        Assert.assertEquals(3, teamGenerator.getNumberOfPlayersByTeam(nbOfTeam));
        Assert.assertEquals(nbOfTeam, teams.size());
        Team expectedFirstTeam = new Team(Arrays.asList(p1, p3, p5));
        Team expectedSecondTeam = new Team(Arrays.asList(p4, p2));
        Assert.assertEquals(expectedFirstTeam, teams.get(0));
        Assert.assertEquals(expectedSecondTeam, teams.get(1));
        Assert.assertEquals(5, teamGenerator.getNbPlayers());
    }

    @Test
    public void testSportAverage() {
        Assert.assertEquals(7.8,
                teamGenerator.getSkillAverages().stream().mapToDouble(Double::doubleValue).average().orElse(0), 0.0);
    }

    @Test
    public void testStdDev() {
        Assert.assertEquals(0.6, teamGenerator.getSkillsStdDev(), 0.1);
    }
}
