package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void startAccelerometerTest(View view) {
        Intent intent = new Intent(this,AccelerometerTest.class);
        startActivity(intent);
    }

    public void startSuddenMovementDetector(View view) {
        Intent intent = new Intent(this, SuddenMovementRecognizer.class);
        startActivity(intent);
    }

    public void startPhoneCalibration(View view) {
        Intent intent = new Intent(this, PhoneCalibrator.class);
        startActivity(intent);
    }
}
