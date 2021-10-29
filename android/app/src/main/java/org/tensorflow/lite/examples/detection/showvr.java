package org.tensorflow.lite.examples.detection;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;

public class showvr extends AppCompatActivity {
    protected VrVideoView _VRvideo;
    VideoLoaderTask LoadVideoLoaderThread;
    VrVideoView.Options _Options =new VrVideoView.Options();
    private SeekBar seekBar;
    boolean isPaused = true;
    String fileUri = "lolvr.mp4"; // bears.mp4 => TYPE_MONO
    Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/lol-fypproject.appspot.com/o/lolvr.mp4?alt=media&token=5ace1a18-52e5-49cc-a52f-8362393b8e2d");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showvr);

        _VRvideo = (VrVideoView) findViewById(R.id.video_view);
        _VRvideo.setInfoButtonEnabled(false);
        _VRvideo.setEventListener(new ActivityEventListener());

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());

        _Options.inputType = VrVideoView.Options.TYPE_MONO;
        LoadVideoLoaderThread = new VideoLoaderTask();
       LoadVideoLoaderThread.execute(Pair.create(fileUri, _Options));

        //Task-3
        MagnetSensor magnetSensor = new MagnetSensor(getApplicationContext());
        MagnetSensor.OnCardboardTriggerListener magnetTriggerListener = new MagnetSensor.OnCardboardTriggerListener() {
            @Override
            public void onCardboardTrigger() {
                if (isPaused) {
                    _VRvideo.playVideo();
                } else {
                    _VRvideo.pauseVideo();
                }
                isPaused = !isPaused;
            }
        };
        magnetSensor.setOnCardboardTriggerListener(magnetTriggerListener);
        magnetSensor.start();

    }

    class VideoLoaderTask extends AsyncTask<Pair<String, VrVideoView.Options>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Pair<String, VrVideoView.Options>... fileInformation) {
           showvr.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        _VRvideo.loadVideoFromAsset(fileUri, _Options);
                    }catch (IOException e){
                    }
                }});
            return true;
        }
    }

    private class ActivityEventListener extends VrVideoEventListener {

        @Override
        public void onLoadSuccess() {
            //Once loaded the video, set the seekBar to the video duration.
            //Task-1 Part-a
            seekBar.setMax((int) _VRvideo.getDuration());
        }

        @Override
        public void onLoadError(String errorMessage) { }

        @Override
        public void onClick() {
            // To pause or to resume
            //Task-1 Part-b
            if (isPaused) {
                _VRvideo.playVideo();
            } else {
                _VRvideo.pauseVideo();
            }
            isPaused = !isPaused;

        }

        @Override
        public void onNewFrame() {
            // Change the seekBar when frame keep changing
            //Task-1 Part-c
            seekBar.setProgress((int) _VRvideo.getCurrentPosition());
        }

        @Override
        public void onCompletion() {
            // when video finish, replay again
            //Task-1 Part-d
            _VRvideo.seekTo(0);
        }
    }

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                // when seek bar change, change the video to THAT part.
                //Task-2 Part-a
                _VRvideo.seekTo(progress);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    }

}