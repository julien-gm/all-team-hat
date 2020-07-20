package domain;

import org.junit.Assert;
import org.junit.Test;

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
}
