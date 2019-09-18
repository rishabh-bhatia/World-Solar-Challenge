package wsc.hudsquad1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AnalogActivity extends AppCompatActivity {

    //SeekBar speedSet;//Seekbar object
    TextView speed, time, distance;//Textview objects
    //BigDecimal dist = new BigDecimal(0.1);
    double dist = 0;//Distance travelled
    ImageView left, right, hazard;//Imageview objects
    int delay = 0;//Time taken by the timer before the first execution
    int period = 500;//Interval after which the timer repeats
    int s = 0;//Value of ProgressBar's realtime position based on which odometric calculations are conducted
    double ss;//double value of s for distance calculation
    int speedDelay = 0;
    int speedPeriod = 500;
    int flag = 0;//Flag for speed timer
    float x1, x2, y1, y2;//Initialising coordinates of Ontouchevent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_analog);

        speed = findViewById(R.id.textView);
        time = findViewById((R.id.textView2));
        distance = findViewById(R.id.textView4);
        left = findViewById(R.id.imageView);
        right = findViewById(R.id.imageView2);
        hazard = findViewById(R.id.imageView10);

        Date curTime = Calendar.getInstance().getTime();
        time.setText(curTime.toString());

        Timer timer = new Timer();//Timer initialization

        //Speed timer
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(flag == 0)
                {
                    s = s + 5;
                    if (s == 180)
                        flag = 1;
                    speed.setText(String.valueOf(s));
                }

                else if (flag == 1)
                {

                    s = s - 5;
                    if (s == 0)
                        flag = 0;
                    speed.setText(String.valueOf(s));

                }
            }
        } , speedDelay, speedPeriod);

        //Odometer timer
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                /*dist = dist.add(BigDecimal.valueOf(0.1));
                distance.setText((String.valueOf(dist)));*/
                /*Odometric calculation by using Distance = Speed * time
                where time  = 1hr = [1/(60*60)]secs*/
                ss = s;
                dist = dist + ss/3600;
                distance.setText(String.format("%.1f", dist) + "km");//Show distance upto 1 decimal place in km


            }
        } , delay, period);




        //Indicator timer: Run timer every 500 milliseconds and flash indicators + Hazard Icon
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AnalogActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (left.getDrawable() == null)
                            left.setImageResource(R.drawable.turnleft);

                        else
                            left.setImageDrawable(null);

                        if (right.getDrawable() == null)
                            right.setImageResource(R.drawable.turnright);

                        else
                            right.setImageDrawable(null);

                        if (hazard.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.hazardson).getConstantState())
                            hazard.setImageResource(R.drawable.hazardsoff);

                        else
                            hazard.setImageResource(R.drawable.hazardson);
                    }
                });

            }
        } , delay, period);
    }

    //Setting up a touch event listener which will detect right swipe and left swipe and then open a new activity.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN://When the screen is touched
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP://When screen is untouched
                x2 = event.getX();
                y2 = event.getY();
                if (x1>x2)//when swiped right
                {
                    Intent i = new Intent(AnalogActivity.this, MainActivity.class);
                    startActivity(i);
                    //finish();//Closing current activity
                }
                else if (x1<x2)
                {
                    Intent i = new Intent(AnalogActivity.this, BatteryActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }
}
