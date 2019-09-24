package wsc.hudsquad1;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BatteryActivity extends AppCompatActivity {

    //SeekBar speedSet;//Seekbar object
    TextView speed, time, distance, battery1, battery2, battery3, battery4;//Textview objects
    ProgressBar batteryProg1, batteryProg2, batteryProg3, batteryProg4;//Progressbar objects
    //BigDecimal dist = new BigDecimal(0.1);
    double dist = 0;//Distance travelled
    ImageView left, right, hazard;//Imageview objects
    int delay = 0;//Time taken by the timer before the first execution
    int period = 500;//Interval after which the timer repeats
    int s = 0;//Value of realtime speed based on which odometric calculations are conducted
    double ss;//double value of s for distance calculation
    int batteryDelay = 0;//Time taken by timer before first execution
    int batteryPeriod = 200;//Interval after which timer repeats for battery percentage
    int flag = 0;//Flag for speed timer
    int battery1Flag = 0;//Flag for battery1 timer
    int battery2Flag = 0;//Flag for battery2 timer
    int battery3Flag = 0;//Flag for battery3 timer
    int battery4Flag = 0;//Flag for battery4 timer
    int battery1percentage;//Initial Battery percentage of battery number 1
    int battery2percentage;//Initial Battery percentage of battery number 2
    int battery3percentage;//Initial Battery percentage of battery number 3
    int battery4percentage;//Initial battery percentage of battery number 4
    float x1, x2, y1, y2;//Initialising coordinates of Ontouchevent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_battery);

        //speedSet = findViewById(R.id.seekBar);
        speed = findViewById(R.id.textView);
        time = findViewById((R.id.textView2));
        distance = findViewById(R.id.textView4);
        left = findViewById(R.id.imageView);
        right = findViewById(R.id.imageView2);
        hazard = findViewById(R.id.imageView10);
        battery1 = findViewById(R.id.bat1tv);
        battery2 = findViewById(R.id.bat2tv);
        battery3 = findViewById(R.id.bat3tv);
        battery4 = findViewById(R.id.bat4tv);
        batteryProg1 = findViewById(R.id.batteryNo1);
        batteryProg2 = findViewById(R.id.batteryNo2);
        batteryProg3 = findViewById(R.id.batteryNo3);
        batteryProg4 = findViewById(R.id.batteryNo4);

        battery1percentage = batteryProg1.getProgress();
        battery2percentage = batteryProg2.getProgress();
        battery3percentage = batteryProg3.getProgress();
        battery4percentage = batteryProg4.getProgress();

        //Setting static values of batetry1 and battery2's textviews as they are images
//        battery1.setText("75%");
//        battery2.setText("50%");


        /*speedSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                speed.setText(String.valueOf(progress));
                s = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/


//        Date curTime = Calendar.getInstance().getTime();//Accessing system time and saving it in curTime
//        time.setText(curTime.toString());//converting  the system time to a string and displaying it in "time" object TextView

        Timer timer = new Timer();//Timer initialization

        //Updating the time
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                //System.out.println("Current time => "+c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String formattedDate = df.format(c.getTime());
                time.setText(formattedDate);//.toString());
            }
        }, 0, 100);

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
        } , delay, period);

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
                BatteryActivity.this.runOnUiThread(new Runnable() {
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

        //Changing battery percentage dynamically using our custom ProgressBar using a timer


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                if(battery1Flag == 0)
                {
                    battery1percentage = battery1percentage + 1;
                    if (battery1percentage == 100)
                        battery1Flag = 1;
                    batteryProg1.setProgress(battery1percentage);
                    battery1.setText(battery1percentage + "%");
                    //battery3.setText(String.valueOf(battery3percentage));
                }

                else if (battery1Flag == 1)
                {

                    battery1percentage = battery1percentage - 1;
                    if (battery1percentage == 0)
                        battery1Flag = 0;
                    batteryProg1.setProgress(battery1percentage);
                    battery1.setText(battery1percentage + "%");

                }
            }
        } , batteryDelay, batteryPeriod);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                if(battery2Flag == 0)
                {
                    battery2percentage = battery2percentage + 1;
                    if (battery2percentage == 100)
                        battery2Flag = 1;
                    batteryProg2.setProgress(battery2percentage);
                    battery2.setText(battery2percentage + "%");
                    //battery3.setText(String.valueOf(battery3percentage));
                }

                else if (battery2Flag == 1)
                {

                    battery2percentage = battery2percentage - 1;
                    if (battery2percentage == 0)
                        battery2Flag = 0;
                    batteryProg2.setProgress(battery2percentage);
                    battery2.setText(battery2percentage + "%");

                }
            }
        } , batteryDelay, batteryPeriod);


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                if(battery3Flag == 0)
                {
                    battery3percentage = battery3percentage + 1;
                    if (battery3percentage == 100)
                        battery3Flag = 1;
                    batteryProg3.setProgress(battery3percentage);
                    battery3.setText(battery3percentage + "%");
                    //battery3.setText(String.valueOf(battery3percentage));
                }

                else if (battery3Flag == 1)
                {

                    battery3percentage = battery3percentage - 1;
                    if (battery3percentage == 0)
                        battery3Flag = 0;
                    batteryProg3.setProgress(battery3percentage);
                    battery3.setText(battery3percentage + "%");

                }
            }
        } , batteryDelay, batteryPeriod);


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(battery4Flag == 0)
                {
                    battery4percentage = battery4percentage + 1;
                    if (battery4percentage == 100)
                        battery4Flag = 1;
                    batteryProg4.setProgress(battery4percentage);
                    battery4.setText(battery4percentage + "%");
                    //battery3.setText(String.valueOf(battery3percentage));
                }

                else if (battery4Flag == 1)
                {

                    battery4percentage = battery4percentage - 1;
                    if (battery4percentage == 0)
                        battery4Flag = 0;
                    batteryProg4.setProgress(battery4percentage);
                    battery4.setText(battery4percentage + "%");

                }
            }
        } , batteryDelay, batteryPeriod);

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
                    Intent i = new Intent(BatteryActivity.this, AnalogActivity.class);
                    startActivity(i);
                    //finish();//Closing current activity
                }
                else if (x1<x2)
                {
                    Intent i = new Intent(BatteryActivity.this, MainActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }
}
