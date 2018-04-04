package com.example.myfirstapp;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PhoneCalibrator extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private Button startButton, saveButton, resetButton;
    private TextView countdown, accelerationText, savedText, resetText;
    private Sensor sensor;
    private SensorManager manager;
    private boolean calibrationInProgress = false;
    private double maxAcceleration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_calibrator);

        startButton = (Button)findViewById(R.id.button2);
        saveButton = (Button)findViewById(R.id.button4);
        resetButton = (Button)findViewById(R.id.button8);

        startButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        countdown = (TextView)findViewById(R.id.textView12);
        accelerationText = (TextView)findViewById(R.id.textView13);
        savedText = (TextView)findViewById(R.id.textView14);
        resetText = (TextView)findViewById(R.id.textView17);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onClick(View v)
    {
        if (v.getId() == R.id.button2) {

            startButton.setAlpha(0.5f);
            startButton.setClickable(false);

            countdown.setVisibility(View.VISIBLE);
            calibrationInProgress = true;
            maxAcceleration = 0;

            accelerationText.setText("");
            accelerationText.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            savedText.setVisibility(View.INVISIBLE);
            resetText.setVisibility(View.INVISIBLE);

            new CountDownTimer(5000, 1) {

                public void onTick(long millisUntilFinished) {
                    countdown.setText("" + millisUntilFinished / 1000 + ":" + millisUntilFinished % 1000);
                }

                public void onFinish() {
                    countdown.setText("");
                    countdown.setVisibility(View.INVISIBLE);
                    calibrationInProgress = false;
                    startButton.setAlpha(1f);
                    startButton.setClickable(true);

                    accelerationText.setText("Maximum measured acceleration: " + maxAcceleration);
                    accelerationText.setVisibility(View.VISIBLE);

                    saveButton.setVisibility(View.VISIBLE);
                }
            }.start();

        } else if (v.getId() == R.id.button4) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

            double savedAcceleration = preferences.getFloat("accelerator_range", (float) 0.0);
            if (savedAcceleration < maxAcceleration) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("accelerator_range", (float) maxAcceleration);
                editor.commit();
                savedText.setText("Calibration saved!");
                savedText.setVisibility(View.VISIBLE);
            } else {

                savedText.setText("Higher value already saved!");
                savedText.setVisibility(View.VISIBLE);
            }
        } else if (v.getId() == R.id.button8) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat("accelerator_range", (float) 40.0);
            editor.commit();

            resetText.setText("Value reset to 40.");
            resetText.setVisibility(View.VISIBLE);

        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double acceleration = Math.sqrt(event.values[0]*event.values[0] +
                event.values[1]*event.values[1] + event.values[2]*event.values[2]);

        //Log.d("STABILITY","delta: " + Double.toString(acceleration));

        if (acceleration > maxAcceleration) {
            maxAcceleration = acceleration;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }
}
