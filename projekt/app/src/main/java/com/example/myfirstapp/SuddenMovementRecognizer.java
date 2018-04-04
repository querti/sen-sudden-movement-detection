package com.example.myfirstapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SuddenMovementRecognizer extends AppCompatActivity implements SensorEventListener,View.OnClickListener {

    private TextView suddenMovementMessage, deltaPrint, xMsg, yMsg, zMsg, powerMessage;
    private Sensor sensor;
    private SensorManager manager;
    private int threshold = 0;
    private static SeekBar seekBar;
    private boolean stopMeasuring = false;
    private boolean showPowerMessage = false;
    private static final double maxVariance = 3;
    private static final int stabilityLength = 8;
    private Switch easterEgg;
    private boolean isButtonChecked =false;
    int maxDiscrete = 40;
    private Button printButton;

    private PreviousValueSaver saver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudden_movement_recognizer);

       printButton = (Button)findViewById(R.id.button6);
       printButton.setOnClickListener(this);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        suddenMovementMessage = (TextView)findViewById(R.id.textView2);
        xMsg = (TextView)findViewById(R.id.textView8);
        yMsg = (TextView)findViewById(R.id.textView4);
        zMsg = (TextView)findViewById(R.id.textView9);
        powerMessage = (TextView)findViewById(R.id.textView3);
        easterEgg = (Switch)findViewById(R.id.switch1);
        easterEgg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isButtonChecked = isChecked;
            }
        });

        saver = new PreviousValueSaver();
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        double savedAcceleration = preferences.getFloat("accelerator_range", (float) 38.0);
        maxDiscrete = ((int) savedAcceleration) + 4;
        Log.d("TESTEKEK", "discrete: "+ maxDiscrete + "saved: "+ savedAcceleration);


        seekBar.setMax(maxDiscrete);
        seekBar.setProgress(maxDiscrete/2);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d("ENDING","ENINGGGG");
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Log.d("ENDING","ENINGGGG222");
            isButtonChecked = false;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick(View v)
    {
        if (v.getId() == R.id.button6) {
            saver.printData();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        saver.saveNewValues(event.values[0], event.values[1], event.values[2], false);
        double deltaForce = saver.getAccelerationDeltaForce();
        double delta = saver.getAccelerationDelta();
        boolean movementDetected =false;

        if (deltaForce > 1000) {
            return;
        }

        threshold = seekBar.getProgress();

        if (delta>threshold) {

            suddenMovementMessage.setVisibility(View.VISIBLE);
            stopMeasuring = true;
            movementDetected = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    suddenMovementMessage.setVisibility(View.INVISIBLE);
                    stopMeasuring = false;
                }
            }, 2000);
        }

        if (deltaForce>threshold && saver.isAccelerationStable(maxVariance, stabilityLength) && !showPowerMessage && movementDetected) {

            String force = (String) String.format("%.2f",saver.getMax() * 0.160);
            powerMessage.setText("Your power is "+ force + " Newtons" );
            powerMessage.setVisibility(View.VISIBLE);
            showPowerMessage = true;
            movementDetected = false;

            if (isButtonChecked) {
                if (delta < maxDiscrete/2 +2) {
                    MediaPlayer mp1 = MediaPlayer.create(this, R.raw.whoa1);
                    mp1.start();
                } else if (delta < (3*maxDiscrete)/(4) + 1) {
                    MediaPlayer mp2 = MediaPlayer.create(this, R.raw.whoa2);
                    mp2.start();
                } else {
                    MediaPlayer mp3 = MediaPlayer.create(this, R.raw.whoa3);
                    mp3.start();
                }
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    powerMessage.setVisibility(View.INVISIBLE);
                    showPowerMessage = false;
                }
            }, 2000);

        }

       /* if (stopMeasuring == true) {
            suddenMovementMessage.setVisibility(View.VISIBLE);
            return;
        } else {
            suddenMovementMessage.setVisibility(View.INVISIBLE);
        }*/

        TextView thresholdMessage;
        thresholdMessage = (TextView)findViewById(R.id.textView);
        thresholdMessage.setText("threshold: "+threshold);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //unused
    }
}
