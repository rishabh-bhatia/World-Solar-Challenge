package wsc.hudsquad1;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class BatteryActivity extends AppCompatActivity {

    //SeekBar speedSet;//Seekbar object
    TextView speed, time, distance, battery1, battery2, battery3, battery4, temp, temp2, avgSpeed, range, ambPressure, motorTemp, batteryTemp, rangeLeft;//Textview objects
    ProgressBar batteryProg1, batteryProg2, batteryProg3, batteryProg4;//Progressbar objects
    //BigDecimal dist = new BigDecimal(0.1);
    EditText link;//Hidden edittext
    public String leftFlag = "OFF", rightFlag = "OFF";//Flags for indicators
    String url = "0.0.0.0:5000";
    double dist = 0;//Distance travelled
    ImageView left, right, hazard, lowBeam, highBeam, handBrake, seatBelt, airBag, doorOpen, abs, malfunction;//Imageview objects
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
    float avgBat, bat1, bat2, bat3, bat4;//Battery variable


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
        setContentView(R.layout.activity_battery);

        //speedSet = findViewById(R.id.seekBar);
        speed = findViewById(R.id.textView);
        temp = findViewById(R.id.ambTemp);
        time = findViewById((R.id.textView2));
        distance = findViewById(R.id.textView4);
        left = findViewById(R.id.imageView);
        right = findViewById(R.id.imageView2);
        hazard = findViewById(R.id.hazardSwitch);
        battery1 = findViewById(R.id.bat1tv);
        battery2 = findViewById(R.id.bat2tv);
        battery3 = findViewById(R.id.bat3tv);
        battery4 = findViewById(R.id.bat4tv);
        batteryProg1 = findViewById(R.id.batteryNo1);
        batteryProg2 = findViewById(R.id.batteryNo2);
        batteryProg3 = findViewById(R.id.batteryNo3);
        batteryProg4 = findViewById(R.id.batteryNo4);
        lowBeam = findViewById(R.id.lowBeam);//Low beam icon
        highBeam = findViewById(R.id.highBeam);//High beam icon
        handBrake = findViewById(R.id.handbrake);//Handbrake icon
        seatBelt = findViewById(R.id.seatBelt);//Seatbelt icon
        airBag = findViewById(R.id.airBag);//Seatbelt icon
        doorOpen = findViewById(R.id.doorOpen);//Door open icon
        abs = findViewById(R.id.abs);//abs icon
        malfunction = findViewById(R.id.malfunction);//Malfunction for battery or motor
        temp2 = findViewById(R.id.ambientTemp);
        avgSpeed = findViewById(R.id.avgSpeed);
        range =findViewById(R.id.range);
        ambPressure = findViewById(R.id.ambientPressure);
        motorTemp = findViewById(R.id.motorTemp);
        batteryTemp = findViewById(R.id.batteryTemp);
        link = findViewById(R.id.editText2);
        rangeLeft = findViewById(R.id.range);

        battery1percentage = batteryProg1.getProgress();
        battery2percentage = batteryProg2.getProgress();
        battery3percentage = batteryProg3.getProgress();
        battery4percentage = batteryProg4.getProgress();

        temp.setOnLongClickListener(new View.OnLongClickListener() {
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

        Timer timer = new Timer();//Timer initialization

        //Updating the time
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                //System.out.println("Current time => "+c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                String formattedDate = df.format(c.getTime());
                time.setText(formattedDate);//.toString());
            }
        }, 0, 100);



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                BatteryActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (leftFlag.equals("ON")) {
                            if ((left.getDrawable().getConstantState()).equals(getResources().getDrawable(R.drawable.turnleftoff).getConstantState()))
                                left.setImageResource(R.drawable.turnleft);

                            else
                                left.setImageResource(R.drawable.turnleftoff);
                        }

                        if (leftFlag.equals("OFF")) {
                            left.setImageResource(R.drawable.turnleftoff);
                        }


                        if(rightFlag.equals("ON")) {
                            if ((right.getDrawable().getConstantState()).equals(getResources().getDrawable(R.drawable.turnrightoff).getConstantState()))
                                right.setImageResource(R.drawable.turnright);

                            else
                                right.setImageResource(R.drawable.turnrightoff);
                        }

                        if (rightFlag.equals("OFF")) {
                            right.setImageResource(R.drawable.turnrightoff);
                        }

                    }
                });

            }
        } , delay, period);

    }

    private  void avgBattery(float batt1, float batt2, float batt3, float batt4)
    {
        avgBat = (batt1 + batt2 + batt3 + batt4)/4;
        batteryProg1.setProgress((int) batt1);//setting battery to sensor's battery value using formula
        batteryProg2.setProgress((int) batt2);//setting battery to sensor's battery value using formula
        batteryProg3.setProgress((int) batt3);//setting battery to sensor's battery value using formula
        batteryProg4.setProgress((int) batt4);//setting battery to sensor's battery value using formula

        battery1.setText(((int) batt1) + "%");
        battery2.setText(((int) batt2) + "%");
        battery3.setText(((int) batt3) + "%");
        battery4.setText(((int) batt4) + "%");
        rangeLeft.setText("Range: " + String.format("%.1f", (400 * (avgBat/100))) + "km");

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
                lowBeam.setImageResource(R.drawable.lowbeamsoff);
        }

        if (itemName == "HighBeam")
        {
            if (status.equals("ON"))
            {
                highBeam.setImageResource(R.drawable.highbeams);
            }
            else
                highBeam.setImageResource(R.drawable.highbeamsoff);
        }

        if (itemName == "HandBrake")
        {
            if (status.equals("ON"))
            {
                handBrake.setImageResource(R.drawable.brakesystemwarning);
            }
            else
                handBrake.setImageResource(R.drawable.brakesystemwarningoff);
        }

        if (itemName == "SeatBelt")
        {
            if (status.equals("ON"))
            {
                seatBelt.setImageResource(R.drawable.seatbelt);
            }
            else
                seatBelt.setImageResource(R.drawable.seatbeltoff);
        }

        if (itemName == "AirBag")
        {
            if (status.equals("ON"))
            {
                airBag.setImageResource(R.drawable.airbag);
            }
            else
                airBag.setImageResource(R.drawable.airbagoff);
        }

        if (itemName == "DoorOpen")
        {
            if (status.equals("ON"))
            {
                doorOpen.setImageResource(R.drawable.dooropen);
            }
            else
                doorOpen.setImageResource(R.drawable.dooropenoff);
        }

        if (itemName == "ABS")
        {
            if (status.equals("ON"))
            {
                abs.setImageResource(R.drawable.abs);
            }
            else
                abs.setImageResource(R.drawable.absoff);
        }

        if (itemName == "Malfunction")
        {
            if (status.equals("ON"))
            {
                malfunction.setImageResource(R.drawable.engine);
            }
            else
                malfunction.setImageResource(R.drawable.engineoff);
        }
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
                if (x1>x2)//when swiped left
                {
                    Intent i = new Intent(BatteryActivity.this, AnalogActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    //finish();//Closing current activity
                }
                else if (x1<x2)//right swipe
                {
                    Intent i = new Intent(BatteryActivity.this, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
//                            case "speed":
//                                speed.setText(SensorValue);
//                                s = Integer.parseInt(SensorValue);
//                                break;
                            case "motor_temp":
                                motorTemp.setText("Motor Temp: " + SensorValue + "°c");
                                break;
//                            case "batt_charge":
//                                try{
//                                    int i = (int) Double.parseDouble(SensorValue);//Converting the string like 100.0 to integer to prevent errors
//                                    batteryProg1.setProgress(i);
//                                    batteryProg2.setProgress(i);
//                                    batteryProg3.setProgress(i);
//                                    batteryProg4.setProgress(i);
//                                    battery1percentage = batteryProg1.getProgress();
//                                    battery2percentage = batteryProg2.getProgress();
//                                    battery3percentage = batteryProg3.getProgress();
//                                    battery4percentage = batteryProg4.getProgress();
//                                    battery1.setText(battery1percentage + "%");
//                                    battery2.setText(battery2percentage + "%");
//                                    battery3.setText(battery3percentage + "%");
//                                    battery4.setText(battery4percentage + "%");
//                                } catch(NumberFormatException ex) {
//                                    Toast.makeText(getApplicationContext(), "Parsing error in battery progress!", Toast.LENGTH_LONG).show();
//                                }
//
//                                break;
//                            case "batt_usage":
//                                BatteryUsage.setText(SensorValue + "w");
//                                break;
                            case "batt_temp":
                                batteryTemp.setText("Battery Temp: " + SensorValue + "°c");
                                break;
//                            case "batt_input":
//                                BatteryInput.setText(SensorValue + "w");
//                                break;
                            case "ambient_temp":
                                temp.setText(SensorValue + "°c");
                                temp2.setText("Ambient Temp: " + SensorValue + "°c");
                                break;
                            case "ambient_pressure":
                                ambPressure.setText("Ambient Pressure: " + SensorValue + "kPa");
                                break;
                            case "signal_Left":
                                leftFlag = SensorValue;
                                break;
                            case "signal_Right":
                                rightFlag = SensorValue;
                                break;
                            case "signal_LowBeam":
                                lightState(SensorValue, "LowBeam");
//                                Toast.makeText(getApplicationContext(), "Low beam: " + SensorValue, Toast.LENGTH_LONG).show();
                                break;
                            case "signal_HighBeam":
                                lightState(SensorValue, "HighBeam");
//                                Toast.makeText(getApplicationContext(), "High beam: " + SensorValue, Toast.LENGTH_LONG).show();
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
                            case "batt_cellA":
                                bat1 = Float.parseFloat(SensorValue);
                                avgBattery(bat1, bat2, bat3, bat4);
                                break;
                            case "batt_cellB":
                                bat2 = Float.parseFloat(SensorValue);
                                avgBattery(bat1, bat2, bat3, bat4);
                                break;
                            case "batt_cellC":
                                bat3 = Float.parseFloat(SensorValue);
                                avgBattery(bat1, bat2, bat3, bat4);
                                break;
                            case "batt_cellD":
                                bat4 = Float.parseFloat(SensorValue);
                                avgBattery(bat1, bat2, bat3, bat4);
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
