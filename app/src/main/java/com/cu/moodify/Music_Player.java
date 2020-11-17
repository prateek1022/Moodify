package com.cu.moodify;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

public class Music_Player extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        final String audioFile = "https://firebasestorage.googleapis.com/v0/b/moodify-897f3.appspot.com/o/156%20-%20Imagine%20Dragons%20-%20Believer.mp3?alt=media&token=da1c9019-5889-4cbc-bac7-8be1e0819ad6";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final MediaPlayer mediaplayer =new MediaPlayer();
                mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaplayer.setDataSource(audioFile);
                    mediaplayer.prepare();
                    mediaplayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },500);


    }
}