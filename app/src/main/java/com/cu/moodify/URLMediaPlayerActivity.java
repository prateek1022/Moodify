package com.cu.moodify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
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

public class URLMediaPlayerActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    ImageView pause;
    ProgressBar loading;
    TextView now_playing, artist;
    Boolean play_pause = true;
    private Handler mHandler = new Handler();
    ImageView mImageView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                main_run();
            }
        },500);

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
            if (Build.VERSION.SDK_INT >= 14)
                mmr.setDataSource(audioFile, new HashMap<String, String>());
            else
                mmr.setDataSource(audioFile);

            String artist_S = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            artist.setText(artist_S);

            byte[] img_bitmap = mmr.getEmbeddedPicture();

            final Bitmap bitmap = BitmapFactory.decodeByteArray(img_bitmap, 0, img_bitmap.length);
            now_playing.setText(title);

            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(final MediaPlayer mp) {
                    mp.start();

                    mRunnable.run();
                    seekBar.setClickable(true);
                    seekBar.setEnabled(true);
                    loadBitmapByPicasso(URLMediaPlayerActivity.this, bitmap, mImageView);
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
        if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        } else {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    public void seekBackward(View view) {

        int seekBackwardTime = 5000;
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition - seekBackwardTime >= 0) {
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        } else {
            mediaPlayer.seekTo(0);
        }
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
        StringBuffer buf = new StringBuffer();

        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        buf
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    private void loadBitmapByPicasso(Context pContext, Bitmap pBitmap, ImageView pImageView) {
        try {
            Uri uri = Uri.fromFile(File.createTempFile("temp_file_name", ".jpg", pContext.getCacheDir()));
            OutputStream outputStream = pContext.getContentResolver().openOutputStream(uri);
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            Picasso.get().load(uri).into(pImageView);
        } catch (Exception e) {
            Log.e("LoadBitmapByPicasso", e.getMessage());
        }
    }
}

