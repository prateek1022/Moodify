package com.cu.moodify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;

public class Music_player extends Activity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    ImageView pause;
    ProgressBar loading;
    TextView now_playing, artist;
    Boolean play_pause = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    ImageView mImageView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        now_playing = findViewById(R.id.now_playing_text);
        pause = findViewById(R.id.pause);
        artist = findViewById(R.id.artist);
        loading=findViewById(R.id.pause_loading);
        seekBar =  findViewById(R.id.seekBar);
        pause.setVisibility(View.GONE);
        seekBar.setClickable(false);
        seekBar.setEnabled(false);

        mImageView = findViewById(R.id.coverImage);
        Picasso.get().load(R.drawable.music).into(mImageView);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                main_run();
            }
        }, 500);

    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (mediaPlayer != null) {

                int mDuration = mediaPlayer.getDuration();
                seekBar.setMax(mDuration);

                TextView totalTime = (TextView) findViewById(R.id.totalTime);
                totalTime.setText(getTimeString(mDuration));

                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);

                TextView currentTime = (TextView) findViewById(R.id.currentTime);
                currentTime.setText(getTimeString(mCurrentPosition));

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                });
            }
            mHandler.postDelayed(this, 10);
        }
    };

    public void main_run(){


        final String audioFile = "https://media1.vocaroo.com/mp3/1LMZAFg74SUd";

        mediaPlayer = new MediaPlayer();

        try {

            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.prepare();
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(audioFile, new HashMap<String, String>());

            String artist_S = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            artist.setText(artist_S);

            byte[] img_bitmap = mmr.getEmbeddedPicture();

            //noinspection ConstantConditions
            final Bitmap bitmap = BitmapFactory.decodeByteArray(img_bitmap, 0, img_bitmap.length);
            now_playing.setText(title);

            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(final MediaPlayer mp) {
                    mp.start();

                    mRunnable.run();
                    seekBar.setClickable(true);
                    seekBar.setEnabled(true);
                    loadBitmapByPicasso(Music_player.this, bitmap, mImageView);
                    pause.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                }
            });

        } catch (IOException e) {
            Activity a = this;
            a.finish();
            Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
        }

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer != null) {

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        play_pause = false;
                        pause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    } else {
                        mediaPlayer.start();
                        play_pause = true;
                        pause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                    }
                }
            }
        });
    }

    public void seekForward(View view) {

        int seekForwardTime = 5000;
        int currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(Math.min(currentPosition + seekForwardTime, mediaPlayer.getDuration()));
    }

    public void seekBackward(View view) {

        int seekBackwardTime = 5000;
        int currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(Math.max(currentPosition - seekBackwardTime, 0));
    }

    public void onBackPressed() {
        super.onBackPressed();

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        finish();
    }

    private String getTimeString(long millis) {

        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

         @SuppressLint("DefaultLocale") String buf = String.format("%02d", minutes) +
                ":" +
                String.format("%02d", seconds);
        return buf;
    }

    private void loadBitmapByPicasso(Context pContext, Bitmap pBitmap, ImageView pImageView) {
        try {
            Uri uri = Uri.fromFile(File.createTempFile("temp_file_name", ".jpg", pContext.getCacheDir()));
            OutputStream outputStream = pContext.getContentResolver().openOutputStream(uri);
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //noinspection ConstantConditions
            outputStream.close();
            Picasso.get().load(uri).into(pImageView);
        } catch (Exception e) {
            Log.e("LoadBitmapByPicasso", Objects.requireNonNull(e.getMessage()));
        }
    }
}

