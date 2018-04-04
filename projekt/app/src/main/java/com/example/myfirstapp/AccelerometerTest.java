package com.example.myfirstapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;


public class AccelerometerTest extends AppCompatActivity implements SensorEventListener {

    private TextView xCoord, yCoord, zCoord, totalAcceleration;
    private Sensor sensor;
    private SensorManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer_test);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        xCoord = (TextView)findViewById(R.id.textView5);
        yCoord = (TextView)findViewById(R.id.textView6);
        zCoord = (TextView)findViewById(R.id.textView7);
        totalAcceleration = (TextView)findViewById(R.id.textView15);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xCoord.setText("X: "+event.values[0]);
        yCoord.setText("Y: "+event.values[1]);
        zCoord.setText("Z: "+event.values[2]);

        double accSum = Math.sqrt(event.values[0]*event.values[0]
                + event.values[1]*event.values[1] + event.values[2]*event.values[2]);

        Log.d("WTFFF","accsum: "+accSum);

        totalAcceleration.setText("Total acceleration: "+accSum);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //unused
    }
}
