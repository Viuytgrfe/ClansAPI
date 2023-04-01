import me.vineer.clansapi.ClansAPI;
import org.junit.Assert;
import org.junit.Test;

public class TimeFromTickTest {
    @Test
    public void first() {
        Assert.assertEquals("2 секунды", ClansAPI.getTextFromTick(40));
    }
    @Test
    public void second() {
        Assert.assertEquals("1 секунда", ClansAPI.getTextFromTick(39));
    }
    @Test
    public void third() {
        Assert.assertEquals("0 секунд", ClansAPI.getTextFromTick(19));
    }
    @Test
    public void fourth() {
        Assert.assertEquals("1 минута 1 секунда", ClansAPI.getTextFromTick(20*60+20));
    }
    @Test
    public void fifth() {
        Assert.assertEquals("2 минуты 24 секунды", ClansAPI.getTextFromTick(2*20*60+20*24));
    }
    @Test
    public void sixth() {
        Assert.assertEquals("2 минуты 48 секунд", ClansAPI.getTextFromTick(2*20*60+20*48));
    }
    @Test
    public void seventh() {
        Assert.assertEquals("2 минуты", ClansAPI.getTextFromTick(2*20*60));
    }
    @Test
    public void eighth() {
        Assert.assertEquals("28 минут 30 секунд", ClansAPI.getTextFromTick(28*20*60+20*30));
    }
    @Test
    public void ninth() {
        Assert.assertEquals("55 минут 31 секунда", ClansAPI.getTextFromTick(55*20*60+20*31));
    }
    @Test
    public void tenth() {
        Assert.assertEquals("4 секунды", ClansAPI.getTextFromTick(20*4));
    }

    @Test
    public void eleventh() {
        Assert.assertEquals("5 секунд", ClansAPI.getTextFromTick(20*5));
    }
    @Test
    public void twelveth() {
        Assert.assertEquals("5 минут 5 секунд", ClansAPI.getTextFromTick(5*20*60+20*5));
    }
    @Test
    public void test13() {
        Assert.assertEquals("5 минут 14 секунд", ClansAPI.getTextFromTick(5*20*60+20*14));
    }
    @Test
    public void test14() {
        Assert.assertEquals("13 секунд", ClansAPI.getTextFromTick(20*13));
    }
    @Test
    public void test15() {
        Assert.assertEquals("10 секунд", ClansAPI.getTextFromTick(20*10));
    }
}
