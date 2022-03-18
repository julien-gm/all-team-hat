package domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class PlayerTest {

    @Test
    public void testPlayerPlayTheSameDay() {
        Player p1 = new Player();
        Player p2 = new Player();
        Player p3 = new Player();
        p3.setDay(1);
        Player p4 = new Player();
        p4.setDay(1);
        Player p5 = new Player();
        p5.setDay(2);

        Assert.assertTrue(p1.playsTheSameDay(p2));
        Assert.assertTrue(p2.playsTheSameDay(p1));

        Assert.assertTrue(p3.playsTheSameDay(p4));
        Assert.assertTrue(p4.playsTheSameDay(p3));

        Assert.assertFalse(p3.playsTheSameDay(p5));
        Assert.assertFalse(p5.playsTheSameDay(p3));

        Assert.assertTrue(p1.playsTheSameDay(p3));
        Assert.assertTrue(p1.playsTheSameDay(p5));
    }

    @Test
    public void testToString() {
        Player p = new Player();
        p.setFirstName("Julien");
        p.setLastName("GM");
        p.setNickName("Jouj");
        p.setHandler(Player.Handler.YES);
        p.setClub("Shamrock");
        p.setGender(Player.Gender.HOMME);
        p.setSkillsList(Arrays.asList(6.0, 7.0, 8.0));
        Assert.assertEquals("H Julien GM (Jouj) [H] score 7,00 - Shamrock", p.toString());

        p.setHandler(Player.Handler.MAYBE);
        p.setNickName("");
        Assert.assertEquals("(H) Julien GM [H] score 7,00 - Shamrock", p.toString());
    }
}
