import org.junit.Test;

public class TimeTest {

    @Test
    public void test(){
        Timer timerLauncher = new TimerLauncher();

        TimerTask timerTask = new TimerTask("测试任务",1);
        timerLauncher.add(timerTask);
        timerLauncher.advanceClock(100);
        System.out.println(timerLauncher.size());
    }
}
