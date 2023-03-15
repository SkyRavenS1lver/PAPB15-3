package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;
    private TextView textViewAzimuth;
    private TextView textViewPitch;
    private TextView textViewRoll;

    private float[] accelerometerData = new float[3];
    private float[] magnetometerData = new float[3];
    private ImageView spotTop;
    private ImageView spotBtm;
    private ImageView spotLeft;
    private ImageView spotRight;
    public static final float VALUE_DRIFT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textViewAzimuth = findViewById(R.id.value_azimuth);
        textViewPitch = findViewById(R.id.value_pitch);
        textViewRoll = findViewById(R.id.value_roll);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        spotBtm = findViewById(R.id.sopt_btm);
        spotTop = findViewById(R.id.sopt_top);
        spotLeft = findViewById(R.id.sopt_left);
        spotRight = findViewById(R.id.sopt_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensorAccelerometer != null){
            sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorMagnetometer != null){
            sensorManager.registerListener(this, sensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }
        float[] rotationMartix = new float[9];
        float[] orientationValue = new float[3];
        boolean rotation0k = SensorManager.getRotationMatrix(rotationMartix,null,accelerometerData,magnetometerData);
        if (rotation0k){SensorManager.getOrientation(rotationMartix, orientationValue);}
        float azimuth = orientationValue[0];
        float pitch = orientationValue[1];
        float roll = orientationValue[2];
        textViewAzimuth.setText(getResources().getString(R.string.value_format, azimuth));
        textViewPitch.setText(getResources().getString(R.string.value_format, pitch));
        textViewRoll.setText(getResources().getString(R.string.value_format, roll));
        if (Math.abs(pitch) < VALUE_DRIFT){pitch = 0;}
        if (Math.abs(roll) < VALUE_DRIFT){roll = 0;}
        spotRight.setAlpha(0.0f);
        spotTop.setAlpha(0.0f);
        spotLeft.setAlpha(0.0f);
        spotBtm.setAlpha(0.0f);
        if (pitch>0){spotBtm.setAlpha(pitch);}
        else {spotTop.setAlpha(Math.abs(pitch));}
        if (roll>0){spotLeft.setAlpha(roll);}
        else {spotRight.setAlpha(Math.abs(roll));}

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}