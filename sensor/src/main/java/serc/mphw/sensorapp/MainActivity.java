package serc.mphw.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textview_acc;
    private TextView textview_light;
    private TextView textview_info;
    private TextView textview_context;

    private SensorManager sensorManager;
    private Sensor mSensor = null;
    private Sensor lSensor = null;

    private double accelerationCurrentVal;
    private double accelerationPrevVal;

    private boolean light = true;
    private boolean movement = false;
    private int state = 0;
    private int prevState = 0;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String txt_;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                accelerationCurrentVal = Math.sqrt(x * x + y * y + z * z);
                double changeInAcceleration = Math.abs(accelerationCurrentVal - accelerationPrevVal);
                textview_acc.setText("Acc: " + String.valueOf(changeInAcceleration));
                if (changeInAcceleration < 0.1)
                    movement = false;
                else
                    movement = true;

                accelerationPrevVal = accelerationCurrentVal;
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
                float x = sensorEvent.values[0];
                textview_light.setText("Light: " + String.valueOf(x));
                if (x < 2)
                    light = false;
                else
                    light = true;
            }


            if (light && movement){ // state = 1
                txt_ = "Context: telefon elde ve hareketli";
                state = 1;
            }

            else if (light && !movement) { // state = 0
                txt_ = "Context: telefon masada ve hareketsiz";
                state = 0;
            }
            else if (!light && movement) { // state = 2
                state = 2;
                txt_ = "Context: telefon cepte ve hareketli";
            }

            else { // state = 3
                state = 3;
                txt_ = "Context: telefon cepte ve hareketsiz";
            }

            if (prevState != state){
                Intent intent = new Intent();
                intent.setAction("serc.mphw.BroadcastMessage");
                intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendBroadcast(intent);
            }
            
            prevState = state;

            textview_context.setText(txt_);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview_acc = findViewById(R.id.textview_acc);
        textview_light = findViewById(R.id.textview_light);
        textview_info = findViewById(R.id.textview_info);
        textview_context = findViewById(R.id.textview_context);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (mSensor == null) {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                textview_info.setText(textview_info.getText() + mSensor.getName() + " found\n");
            } else {
                textview_info.setText(textview_info.getText() + "!! There are no accelerometers on your device !!\n");
            }
        }
        if (lSensor == null){
            if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
                lSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                textview_info.setText(textview_info.getText() + lSensor.getName() + " found\n");
            } else {
                textview_info.setText(textview_info.getText() + "!! There are no light sensors on your device !!\n");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, lSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}