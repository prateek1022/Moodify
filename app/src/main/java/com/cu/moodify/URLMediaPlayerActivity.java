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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class URLMediaPlayerActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    ImageView pause;
    TextView now_playing,artist;
    Boolean play_pause=true;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        now_playing=findViewById(R.id.now_playing_text);
        pause=findViewById(R.id.pause);
        artist=findViewById(R.id.artist);


        final String audioFile = "https://firebasestorage.googleapis.com/v0/b/moodify-897f3.appspot.com/o/156%20-%20Imagine%20Dragons%20-%20Believer.mp3?alt=media&token=da1c9019-5889-4cbc-bac7-8be1e0819ad6";




        // create a media player
        mediaPlayer = new MediaPlayer();


        // try to load data and play
        try {

            // give data to mediaPlayer
            mediaPlayer.setDataSource(audioFile);
            // media player asynchronous preparation
            mediaPlayer.prepare();



            // create a progress dialog (waiting media player preparation)
            final ProgressDialog dialog = new ProgressDialog(URLMediaPlayerActivity.this);

            // set message of the dialog
            dialog.setMessage(getString(R.string.loading));

            // prevent dialog to be canceled by back button press
            dialog.setCancelable(false);

            // show dialog at the bottom
            dialog.getWindow().setGravity(Gravity.CENTER);

            // show dialog
            dialog.show();


            // inflate layout


            // display title
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mmr.setDataSource(audioFile, new HashMap<String, String>());
            else
                mmr.setDataSource(audioFile);

            String artist_S = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            artist.setText(artist_S);

            byte[] img_bitmap=mmr.getEmbeddedPicture();

            Bitmap bitmap = BitmapFactory.decodeByteArray(img_bitmap, 0, img_bitmap.length);
            now_playing.setText(title);



            /// Load cover image (we use Picasso Library)

            // Get image view
            ImageView mImageView = (ImageView) findViewById(R.id.coverImage);




            loadBitmapByPicasso(this,bitmap,mImageView);

            ///


            // execute this code at the end of asynchronous media player preparation
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(final MediaPlayer mp) {


                    //start media player
                    mp.start();

                    // link seekbar to bar view
                    seekBar = (SeekBar) findViewById(R.id.seekBar);

                    //update seekbar
                    mRunnable.run();

                    //dismiss dialog
                    dialog.dismiss();
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

                if(mediaPlayer!=null) {

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



    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if(mediaPlayer != null) {

                //set max value
                int mDuration = mediaPlayer.getDuration();
                seekBar.setMax(mDuration);

                //update total time text view
                TextView totalTime = (TextView) findViewById(R.id.totalTime);
                totalTime.setText(getTimeString(mDuration));

                //set progress to current position
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);

                //update current time text view
                TextView currentTime = (TextView) findViewById(R.id.currentTime);
                currentTime.setText(getTimeString(mCurrentPosition));

                //handle drag on seekbar
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mediaPlayer != null && fromUser){
                            mediaPlayer.seekTo(progress);
                        }
                    }
                });


            }

            //repeat above code every second
            mHandler.postDelayed(this, 10);
        }
    };



    public void seekForward(View view){

        //set seek time
        int seekForwardTime = 5000;

        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if(currentPosition + seekForwardTime <= mediaPlayer.getDuration()){
            // forward song
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        }else{
            // forward to end position
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }

    }

    public void seekBackward(View view){

        //set seek time
        int seekBackwardTime = 5000;

        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if(currentPosition - seekBackwardTime >= 0){
            // forward song
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        }else{
            // backward to starting position
            mediaPlayer.seekTo(0);
        }

    }




    public void onBackPressed(){
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

        long hours = millis / (1000*60*60);
        long minutes = ( millis % (1000*60*60) ) / (1000*60);
        long seconds = ( ( millis % (1000*60*60) ) % (1000*60) ) / 1000;

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

