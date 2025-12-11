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
        p1.setSkillsList(Arrays.asList(7.0, 6.0, 8.0));
        p1.setGender(Player.Gender.FEMME);
        p1.setHandler(Player.Handler.YES);
        p1.setAge(20);
        p1.setFirstName("Pré");
        p1.setLastName("Nom");
        p1.setNickName("nickie");
        p1.setDay(1);
        p1.setClub("test");
        Player p2 = new Player();
        p2.setAge(30);
        p2.setFirstName("first");
        p2.setLastName("last");
        p2.setNickName("nick");
        p2.setSkillsList(Arrays.asList(8.0, 9.0, 7.0));
        p2.setGender(Player.Gender.HOMME);
        p2.setHandler(Player.Handler.MAYBE);
        p2.setClub("test");
        Player p3 = new Player(false);
        team = new Team(Arrays.asList(p1, p2, p3));
        team.initSkills();
    }

    @Test
    public void testGirlScore() {
        Assert.assertEquals(0.2, team.getGirlScore(1.2), 0.0001);
    }

    @Test
    public void testSkillsAvg() {
        Assert.assertEquals(7.5, team.getSkillsAverage(), 0.01);
    }

    @Test
    public void testSkillsScore() {
        Assert.assertEquals(86.63, team.getSkillsScore(Arrays.asList(7.2, 7.7, 7.5)), 0.01);
    }

    @Test
    public void testHandlerScore() {
        Assert.assertEquals(7, team.getHandlerScore(0.3), 0.0001);
    }

    @Test
    public void testNoHandlerScore() {
        Assert.assertEquals(12, team.getNoHandlerScore(1.2), 0.0001);
    }

    @Test
    public void testGetPlayersForDay() {
        Assert.assertEquals(2, team.getPlayersForDay(1).size(), 0.0001);
        Assert.assertEquals(1, team.getPlayersForDay(2).size(), 0.0001);
    }

    @Test
    public void testStdDev() {
        Assert.assertEquals(0.5, team.getStandardDeviation(), 0.01);
    }

    @Test
    public void testClubScore() {
        Map<String, Double> expectedClubScore = new HashMap<>();
        expectedClubScore.put("", 1.0);
        expectedClubScore.put("TEST", 1.0);
        Assert.assertEquals(20.0, team.getClubScore(expectedClubScore), 0.0001);
    }

    @Test
    public void testToString() {
        Assert.assertEquals(
                "Day #1 - 1 Women / 1 Men, 1 Handlers / 0 Middles, Skills: 7,50 (0,50) [25,00]\nTEST: 2 \n"
                        + "Day #2 - 0 Women / 1 Men, 0 Handlers / 0 Middles, Skills: 8,00 (0,00) [30,00]\nTEST: 2 \n",
                team.toString());
    }

    @Test
    public void testToCSV() {
        Assert.assertEquals(
                "Team #1,Poste,Genre,Prenom,Nom,Pseudo,Age,skill_1,skill_2,skill_3,Moyenne compétence,Club\n"
                        + "1,H,F,Pré,Nom,nickie,20,\"7,00\",\"6,00\",\"8,00\",\"7,00\",TEST\n"
                        + "2,(h),H,first,last,nick,30,\"8,00\",\"9,00\",\"7,00\",\"8,00\",TEST\n"
                        + "Stats:,1+1,1/2,,,,\"25,00\",\"7,50\",\"7,50\",\"7,50\",\"7,50\",2\n\n",
                team.toCSV(Arrays.asList(team), 1, false));
    }

    @Test
    public void testToCSVWithDay() {
        Assert.assertEquals(
                "Team #1,Poste,Genre,Prenom,Nom,Pseudo,Age,skill_1,skill_2,skill_3,Moyenne compétence,Club,Jour\n"
                        + "1,H,F,Pré,Nom,nickie,20,\"7,00\",\"6,00\",\"8,00\",\"7,00\",TEST,1\n"
                        + "2,(h),H,first,last,nick,30,\"8,00\",\"9,00\",\"7,00\",\"8,00\",TEST,0\n"
                        + "=COUNTIF(B3:B4;\"H\"),=A5+COUNTIF(B3:B4;\"(h)\"),=COUNTIF(C3:C4;\"F\"),"
                        + ",,,=ROUND(AVERAGE(G6:G7); 2),=ROUND(AVERAGE(H6:H7); 2),=ROUND(AVERAGE(I6:I7); 2),=ROUND(AVERAGE(J6:J7); 2),=ROUND(AVERAGE(K6:K7); 2),"
                        + "=ARRAYFORMULA(MAX(COUNTIF(L3:L4;L3:L4))),2\nStats J1:,1+1,1/2,,,,\"25,00\",\"7,50\",\"7,50\",\"7,50\",\"7,50\",2,2\n"
                        + "Stats J2:,0+1,0/1,,,,\"30,00\",\"8,00\",\"9,00\",\"7,00\",\"8,00\",1,1\n\n",
                team.toCSV(Arrays.asList(team), 1, true));
    }
}
