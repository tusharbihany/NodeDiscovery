package scheduler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class BackgroundScheduler {

    // Scheduled every 1 sec
    private static final long SCHEDULER_FREQUENCY = 3000;

    /*
       Schedules a callable every SCHEDULER_FREQUENCY milliseconds
     */
    public static void scheduleTTLCleanUp(Timer timer, final Callable<Void> callable) {
        timer.scheduleAtFixedRate(new TimerTask() {

                                      @Override
                                      public void run() {
                                          try {
                                              callable.call();
                                          } catch (Exception e) {
                                              //log the error
                                          }
                                      }
                                  },
                //Delay
                0,
                //Time between each execution
                SCHEDULER_FREQUENCY);
    }
}
