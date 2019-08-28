package wsc.hudsquad1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    SeekBar speedSet;
    TextView speed, time, distance;
    //BigDecimal dist = new BigDecimal(0.1);
    double dist = 0;
    ImageView left, right;
    int delay = 0;
    int period = 1000;
    double s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        speedSet = findViewById(R.id.seekBar);
        speed = findViewById(R.id.textView);
        time = findViewById((R.id.textView2));
        distance = findViewById(R.id.textView4);
        left = findViewById(R.id.imageView);
        right = findViewById(R.id.imageView2);

        speedSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        });

        Date curTime = Calendar.getInstance().getTime();
        time.setText(curTime.toString());

        Timer timer = new Timer();


       timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                /*dist = dist.add(BigDecimal.valueOf(0.1));
                distance.setText((String.valueOf(dist)));*/
                dist = dist + s/3600;
                distance.setText(String.format("%.1f", dist));


            }
        } ,delay, period);



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (left.getDrawable() == null)
                            left.setImageResource(R.drawable.turnleft);

                        else
                            left.setImageDrawable(null);

                        if (right.getDrawable() == null)
                            right.setImageResource(R.drawable.turnright);

                        else
                            right.setImageDrawable(null);

                    }
                });

            }
        } ,0, 500);



    }
}
