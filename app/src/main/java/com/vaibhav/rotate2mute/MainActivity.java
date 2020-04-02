package com.vaibhav.rotate2mute;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioStream;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void audioSettings(boolean mute_status){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int adjust_mute;
        if(mute_status){
            adjust_mute = AudioManager.ADJUST_MUTE;
        }
        else{
            adjust_mute = AudioManager.ADJUST_UNMUTE;
        }

        assert audioManager != null;
        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, adjust_mute, 0);
    }

    boolean toggle = false; //Initially UnMuted

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mute_unmute = findViewById(R.id.buttonMute);

        mute_unmute.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                if(!toggle){
                    Toast.makeText(getApplicationContext(), "Mute", Toast.LENGTH_SHORT).show();
                    audioSettings(true);
                    toggle = true;
                }
                else{
                    Toast.makeText(getApplicationContext(), "UnMute", Toast.LENGTH_SHORT).show();
                    audioSettings(false);
                    toggle = false;
                }
            }
        });
    }

}
