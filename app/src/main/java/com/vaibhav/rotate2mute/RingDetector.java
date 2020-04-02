package com.vaibhav.rotate2mute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class RingDetector extends BroadcastReceiver {

    SensorManager sensorManagerGravity;
    MyGravityListener myGravityListener;
    Sensor sensorGravity;
    Context context_global;
    AudioManager audioManager;
    static boolean once_up = false;
    static boolean stop = false;
    static boolean muted = false;

    public void audioSettings(boolean mute_status){
        Log.d("mute_status: ", String.valueOf(mute_status));
        try {
            audioManager = (AudioManager) context_global.getSystemService(Context.AUDIO_SERVICE);
            int adjust_mute;
            if (mute_status) {
                adjust_mute = AudioManager.ADJUST_MUTE;
            } else {
                adjust_mute = AudioManager.ADJUST_UNMUTE;
            }

            assert audioManager != null;
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, adjust_mute, 0);
        }catch (Exception e){
            e.printStackTrace();
            sensorManagerGravity.unregisterListener(myGravityListener);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context_global = context;
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();
        assert intent.getAction() != null;
        Log.d("RingDetectorLog: ", intent.getAction());

        //Initialize gravity sensor
        sensorManagerGravity = (SensorManager) context_global.getSystemService(Context.SENSOR_SERVICE);
        assert sensorManagerGravity != null;
        sensorGravity = sensorManagerGravity.getDefaultSensor(Sensor.TYPE_GRAVITY);
        myGravityListener = new MyGravityListener();

        if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
            String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String state_ringing = TelephonyManager.EXTRA_STATE_RINGING;
            assert phone_state != null;
            if(phone_state.equals(state_ringing)){
                Toast.makeText(context, "Phone is ringing", Toast.LENGTH_LONG).show();
                Log.d("RingDetectorLog: ", "Phone is ringing");
                once_up = false;
                stop = false;
                muted = false;
                audioSettings(false);   //UnMuting
                sensorManagerGravity.registerListener(myGravityListener, sensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else{
                sensorManagerGravity.unregisterListener(myGravityListener, sensorGravity);
                stop = true;
                audioSettings(false);
            }
        }
    }

    class MyGravityListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event){
            float sensor_val = event.values[2];
            if(stop){
                sensorManagerGravity.unregisterListener(myGravityListener, sensorGravity);
                audioSettings(false);
                Log.d("Sensor stop ", String.valueOf(stop));
            }

            if(!once_up && sensor_val > 6){
                once_up = true;
                Log.d("Sensor value if: ", String.valueOf(sensor_val) + " " + once_up);
            }
            else if(sensor_val < -6 && once_up && !muted){
                audioSettings(true);
                muted = true;
                Log.d("Sensor value else if: ", String.valueOf(sensor_val) + " " + once_up);
                sensorManagerGravity.unregisterListener(myGravityListener, sensorGravity);
            }
            else{
                Log.d("Sensor value else: ", String.valueOf(sensor_val) + " " + once_up);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy){

        }
    }

}