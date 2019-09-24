package wsc.hudsquad1;

/*
 * Author: Rishabh Bhatia
 * Author email: bhatiari@deakin.edu.au
 * Year: 2019
 * */

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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
    int touchCount = 0;
    EditText link;
    //BigDecimal dist = new BigDecimal(0.1);
    double dist = 0;//Distance travelled
    ImageView left, right, hazard, lowBeam, highBeam, handBrake, seatBelt, airBag, doorOpen, abs, malfunction;//Imageview objects
    public String leftFlag = "OFF", rightFlag = "OFF";//Flags for indicators
    String url = "0.0.0.0:5000";//url for simulator network
    int delay = 0;//Time taken by the timer before the first execution
    int period = 500;//Interval after which the timer repeats
    int s = 0;//Value of ProgressBar's realtime position based on which odometric calculations are conducted
    double ss;//double value of s for distance calculation
    int speedDelay = 0;//Timer delay. Can be removed after simulator is connected
    int speedPeriod = 500;//Timer period. Can be removed after simulator is connected
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
            socket = IO.socket("http://" + url); //http://YourLocalIPAddress:8000
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
        lowBeam = findViewById(R.id.lowBeam);//Low beam icon
        highBeam = findViewById(R.id.highBeam);//High beam icon
        handBrake = findViewById(R.id.handbrake);//Handbrake icon
        seatBelt = findViewById(R.id.seatBelt);//Seatbelt icon
        airBag = findViewById(R.id.airBag);//Seatbelt icon
        doorOpen = findViewById(R.id.doorOpen);//Door open icon
        abs = findViewById(R.id.abs);//abs icon
        malfunction = findViewById(R.id.malfunction);//Malfunction for battery or motor
        link = findViewById(R.id.editText);

        speed.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(link.getVisibility() == View.VISIBLE)
                {
                    link.setVisibility(view.GONE);
                    url = link.getText().toString();
                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                    try {
                        //localhost, 127.0.0.1 OR [::1] - will not work here. Use your local ip address
                        socket = IO.socket("http://" + url); //http://YourLocalIPAddress:8000
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                    //Calls the socket on function which looks for the update event being emitted from the server and receives the messages
                    socket.on("update", onNewMessage); //This occurs each time a message is sent from the server. Calls onNewMessage() method.
                    socket.connect();   //Connects to the server
                }
                else
                    link.setVisibility(View.VISIBLE);
                return false;
            }
        });





        //Initializing timer
        Timer timer = new Timer();//Timer initialization
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


//        Date curTime = Calendar.getInstance().getTime();

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


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (leftFlag.equals("ON")) {
                            if (left.getDrawable() == null)
                                left.setImageResource(R.drawable.turnleft);

                            else
                                left.setImageDrawable(null);
                        }

                        if (leftFlag.equals("OFF")) {
                                left.setImageDrawable(null);
                        }


                        if(rightFlag.equals("ON")) {
                            if (right.getDrawable() == null)
                                right.setImageResource(R.drawable.turnright);

                            else
                                right.setImageDrawable(null);
                        }

                        if (rightFlag.equals("OFF")) {
                                right.setImageDrawable(null);
                        }

                    }
                });

            }
        } , delay, period);

    }

    //Setting the status of HUD Icons using simulator data
    private void lightState(String status, String itemName)
    {
        if (itemName == "LowBeam")
        {
            if (status.equals("ON"))
            {
                lowBeam.setImageResource(R.drawable.lowbeams);
            }
            else
                lowBeam.setImageDrawable(null);
        }

        if (itemName == "HighBeam")
        {
            if (status.equals("ON"))
            {
                highBeam.setImageResource(R.drawable.highbeams);
            }
            else
                highBeam.setImageDrawable(null);
        }

        if (itemName == "HandBrake")
        {
            if (status.equals("ON"))
            {
                handBrake.setImageResource(R.drawable.brakesystemwarning);
            }
            else
                handBrake.setImageDrawable(null);
        }

        if (itemName == "SeatBelt")
        {
            if (status.equals("ON"))
            {
                seatBelt.setImageResource(R.drawable.seatbelt);
            }
            else
                seatBelt.setImageDrawable(null);
        }

        if (itemName == "AirBag")
        {
            if (status.equals("ON"))
            {
                airBag.setImageResource(R.drawable.airbag);
            }
            else
                airBag.setImageDrawable(null);
        }

        if (itemName == "DoorOpen")
        {
            if (status.equals("ON"))
            {
                doorOpen.setImageResource(R.drawable.dooropen);
            }
            else
                doorOpen.setImageDrawable(null);
        }

        if (itemName == "ABS")
        {
            if (status.equals("ON"))
            {
                abs.setImageResource(R.drawable.abs);
            }
            else
                abs.setImageDrawable(null);
        }

        if (itemName == "Malfunction")
        {
            if (status.equals("ON"))
            {
                malfunction.setImageResource(R.drawable.engine);
            }
            else
                malfunction.setImageDrawable(null);
        }
    }

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
                    Intent i = new Intent(MainActivity.this, BatteryActivity.class);
                    startActivity(i);
                    //finish();//Closing current activity
                }

                else if (x1<x2)
                {
                    Intent i = new Intent(MainActivity.this, AnalogActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }


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
                            case "signal_Left":
                                leftFlag = SensorValue;
                                break;
                            case "signal_Right":
                                rightFlag = SensorValue;
                                break;
                            case "signal_LowBeam":
                                lightState(SensorValue, "LowBeam");
                                Toast.makeText(getApplicationContext(), "Low beam: " + SensorValue, Toast.LENGTH_LONG).show();
                                break;
                            case "signal_HighBeam":
                                lightState(SensorValue, "HighBeam");
                                Toast.makeText(getApplicationContext(), "High beam: " + SensorValue, Toast.LENGTH_LONG).show();
                                break;
                            case "signal_Hazard":
                                leftFlag = SensorValue;
                                rightFlag = SensorValue;
                                break;
                            case "warning_Handbrake":
                                lightState(SensorValue, "HandBrake");
                                break;
                            case "warning_Seatbelt":
                                lightState(SensorValue, "SeatBelt");
                                break;
                            case "warning_Airbag":
                                lightState(SensorValue, "AirBag");
                                Toast.makeText(getApplicationContext(), "Airbag: " + SensorValue, Toast.LENGTH_LONG).show();
                                break;
                            case "warning_Door":
                                lightState(SensorValue, "DoorOpen");
                                break;
                            case "warning_ABS":
                                lightState(SensorValue, "ABS");
                                break;
                            case "warning_Engine":
                                lightState(SensorValue, "Malfunction");
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}
