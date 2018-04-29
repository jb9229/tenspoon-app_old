package host.exp.exponent.tools;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by test on 2016-12-08.
 */
public class TimerHandler {
    public static final int UP_A_DAY   =   2000;

    Thread    tTask;
    TextView textView;
    String dateFormat;
    int interval;


    public TimerHandler(TextView textView, String dateFormat, int interval) {
        this.textView       =   textView;
        this.dateFormat     =   dateFormat;
        this.interval       =   interval;
    }

    public void start(){
        tTask   =   new Thread(new Runnable() {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted())
                {
                    Date rightNow               = new Date();
                    SimpleDateFormat formatter  = new SimpleDateFormat(dateFormat);

                    String dateString = formatter.format(rightNow);
                    textView.setText(dateString);


                    try {

                        Thread.sleep(interval);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        tTask.start();
    }

    public void stop(){
        tTask.interrupt();
    }
}
