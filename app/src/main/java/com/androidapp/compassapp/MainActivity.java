package com.androidapp.compassapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    CompassView compassView;

    SensorManager sensorManager;
    SensorEventListener listener;
    Sensor magneticSensor, accelSensor;

    float [] accelValues, magneticValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassView = (CompassView) findViewById(R.id.compassView);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch(event.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER :
                        accelValues = event.values.clone(); break;

                    case Sensor.TYPE_MAGNETIC_FIELD :
                        magneticValues = event.values.clone(); break;

                    default: break;
                }
                if(magneticValues != null && accelValues != null){
                    float[] R = new float[16];
                    float[] I = new float[16];
                    SensorManager.getRotationMatrix(R, I, accelValues, magneticValues);
                    float[] values = new float[3];
                    SensorManager.getOrientation(R,values);
                    //센서메니저를 사용해 겟 오리엔테이션[나침반의 방향]] 호출 R 행렬만 사용해 넣어준다.

                    compassView.azimuth = (int) radian2degree(values[0]);   //2개의 센서로부터 값을 받았다.
                    compassView.invalidate();   //compassView에 있는 그림을 무효화 시켜라=다시그려라
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {            }
        }  ;

        sensorManager.registerListener(listener, magneticSensor, sensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener, accelSensor, sensorManager.SENSOR_DELAY_UI);

    }

    private float radian2degree(float radian) {return radian * 180 /  (float)Math.PI;    }
}