package wsc.hudsquad1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    ProgressBar batteryProg3, batteryProg4;//Progressbar objects
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
    int battery3Flag = 0;//Flag for battery3 timer
    int battery4Flag = 0;//Flag for battery4 timer
    int battery3percentage;//Initial Battery percentage of battery number 3
    int battery4percentage;//Initial battery percentage of battery number 4

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
        batteryProg3 = findViewById(R.id.batteryNo3);
        batteryProg4 = findViewById(R.id.batteryNo4);

        battery3percentage = batteryProg3.getProgress();
        battery4percentage = batteryProg4.getProgress();

        //Setting static values of batetry1 and battery2's textviews as they are images
        battery1.setText("75%");
        battery2.setText("50%");


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


        Date curTime = Calendar.getInstance().getTime();//Accessing system time and saving it in curTime
        time.setText(curTime.toString());//converting  the system time to a string and displaying it in "time" object TextView

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
}
