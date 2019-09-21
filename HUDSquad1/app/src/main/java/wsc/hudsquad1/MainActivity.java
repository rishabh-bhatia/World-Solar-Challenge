package wsc.hudsquad1;

/*
 * Author: Rishabh Bhatia
 * Author email: bhatiari@deakin.edu.au
 * Year: 2019
 * */

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    //SeekBar speedSet;//Seekbar object
    public TextView speed, time, distance, temp;//Textview objects
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
    static boolean active = false;//Setting a boolean to check if an activity is active

   /* @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }*/

    private Socket socket;
    {
        try {
            //localhost, 127.0.0.1 OR [::1] - will not work here. Use your local ip address
            socket = IO.socket("http://192.168.0.20:5000"); //http://YourLocalIPAddress:8000
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //speedSet = findViewById(R.id.seekBar);
        speed = findViewById(R.id.textView);
        temp = findViewById(R.id.ambTemp);//Ambient temparature
        time = findViewById((R.id.textView2));
        distance = findViewById(R.id.textView4);
        left = findViewById(R.id.imageView);
        right = findViewById(R.id.imageView2);
        hazard = findViewById(R.id.imageView10);

        //Calls the socket on function which looks for the update event being emitted from the server and receives the messages
        socket.on("update", onNewMessage); //This occurs each time a message is sent from the server. Calls onNewMessage() method.
        socket.connect();   //Connects to the server

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

        Date curTime = Calendar.getInstance().getTime();
        time.setText(curTime.toString());

        Timer timer = new Timer();//Timer initialization
//
//        //Speed timer
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if(flag == 0)
//                {
//                    s = s + 5;
//                    if (s == 180)
//                        flag = 1;
//                    speed.setText(String.valueOf(s));
//                }
//
//                else if (flag == 1)
//                {
//
//                    s = s - 5;
//                    if (s == 0)
//                        flag = 0;
//                    speed.setText(String.valueOf(s));
//
//                }
//            }
//        } , speedDelay, speedPeriod);
//
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




//       //Indicator timer: Run timer every 500 milliseconds and flash indicators + Hazard Icon
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        if (left.getDrawable() == null)
//                            left.setImageResource(R.drawable.turnleft);
//
//                        else
//                            left.setImageDrawable(null);
//
//                        if (right.getDrawable() == null)
//                            right.setImageResource(R.drawable.turnright);
//
//                        else
//                            right.setImageDrawable(null);
//
//                        if (hazard.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.hazardson).getConstantState())
//                            hazard.setImageResource(R.drawable.hazardsoff);
//
//                        else
//                            hazard.setImageResource(R.drawable.hazardson);
//                    }
//                });
//
//            }
//        } , delay, period);



    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN://When the screen is touched
//                x1 = event.getX();
//                y1 = event.getY();
//                break;
//            case MotionEvent.ACTION_UP://When screen is untouched
//                x2 = event.getX();
//                y2 = event.getY();
//                if (x1>x2)//when swiped right
//                {
//                    Intent i = new Intent(MainActivity.this, BatteryActivity.class);
//                    startActivity(i);
//                    //finish();//Closing current activity
//                }
//
//                else if (x1<x2)
//                {
//                    Intent i = new Intent(MainActivity.this, AnalogActivity.class);
//                    startActivity(i);
//                }
//                break;
//        }
//        return false;
//    }


    //Reads the JSON object that is being sent from the server and changes the TextView value.
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String Sensor;
                    String SensorValue;

                    //Checks the sensor coming in and sets the sensor value to the correct TextView
                    try {

                        JSONObject data = (JSONObject) args[0];

                        Sensor = data.get("tag").toString();
                        SensorValue = data.getString("value");

                        switch (Sensor) {
                            case "speed":
                                speed.setText(SensorValue);
                                s = Integer.parseInt(SensorValue);
                                break;
//                            case "motor_temp":
//                                distance.setText(SensorValue + "°c");
//                                break;
//                            case "batt_charge":
//                                BatteryCharge.setText(SensorValue + "%");
//                                break;
//                            case "batt_usage":
//                                BatteryUsage.setText(SensorValue + "w");
//                                break;
//                            case "batt_temp":
//                                BatteryTemp.setText(SensorValue + "°c");
//                                break;
//                            case "batt_input":
//                                BatteryInput.setText(SensorValue + "w");
//                                break;
                            case "ambient_temp":
                                temp.setText(SensorValue + "°c");
                                break;
//                            case "signal_Left":
//                                LeftIndicator.setText(SensorValue);
//                                break;
//                            case "signal_Right":
//                                RightIndicator.setText(SensorValue);
//                                break;
//                            case "signal_LowBeam":
//                                LowBeam.setText(SensorValue);
//                                break;
//                            case "signal_HighBeam":
//                                HighBeam.setText(SensorValue);
//                                break;
//                            case "signal_Hazard":
//                                Hazards.setText(SensorValue);
//                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}
