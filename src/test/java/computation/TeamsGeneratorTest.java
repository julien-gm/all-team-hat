package computation;

import computation.TeamsGenerator;
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
        p1.setSpeed(7);
        p1.setEndurance(7);
        p1.setTech(7);
        p1.setGender(Player.Gender.F);
        p1.setHandler(Player.Handler.YES);
        p1.setClub("c1");
        p1.setNickName("p1");
        p2 = new Player();
        p2.setSpeed(8);
        p2.setEndurance(8);
        p2.setTech(8);
        p2.setGender(Player.Gender.M);
        p2.setHandler(Player.Handler.MAYBE);
        p2.setClub("c2");
        p2.setNickName("p2");
        p3 = new Player();
        p3.setSpeed(7);
        p3.setEndurance(7);
        p3.setTech(7);
        p3.setGender(Player.Gender.M);
        p3.setHandler(Player.Handler.YES);
        p3.setClub("c1");
        p3.setNickName("p3");
        p4 = new Player();
        p4.setSpeed(9);
        p4.setEndurance(9);
        p4.setTech(9);
        p4.setGender(Player.Gender.F);
        p4.setHandler(Player.Handler.NO);
        p4.setClub("c1");
        p4.setNickName("p4");
        p5 = new Player();
        p5.setSpeed(8);
        p5.setEndurance(8);
        p5.setTech(8);
        p5.setGender(Player.Gender.M);
        p5.setHandler(Player.Handler.MAYBE);
        p5.setClub("c3");
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
    public void testInitTeams() {
        int nbOfTeam = 2;
        List<Team> teams = teamGenerator.initTeams(nbOfTeam);
        Assert.assertEquals(3, teamGenerator.getNumberOfPlayersByTeam(nbOfTeam));
        Assert.assertEquals(nbOfTeam, teams.size());
        Team expectedFirstTeam = new Team(Arrays.asList(p1, p5, p2));
        Team expectedSecondTeam = new Team(Arrays.asList(p4, p3, Team.fakePlayer));
        Assert.assertEquals(expectedFirstTeam, teams.get(0));
        Assert.assertEquals(expectedSecondTeam, teams.get(1));
    }

    @Test
    public void testEnduranceAverage() {
        Assert.assertEquals(7.8, teamGenerator.getEnduranceAverage(), 0.0);
    }

    @Test
    public void testSpeedAverage() {
        Assert.assertEquals(7.8, teamGenerator.getSpeedAverage(), 0.0);
    }

    @Test
    public void testTechAverage() {
        Assert.assertEquals(7.8, teamGenerator.getTechAverage(), 0.0);
    }
}
