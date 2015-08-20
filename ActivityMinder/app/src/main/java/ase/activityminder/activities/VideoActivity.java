package ase.activityminder.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import ase.activityminder.R;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoActivity extends Activity {

    private String videoURL = //"rtmp://204.107.26.252:8086/live/796.high.stream";
//        "http://10.200.85.187/how.mp4";
            "http://videocdn.bodybuilding.com/video/mp4/58000/59932m.mp4";
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (!LibsChecker.checkVitamioLibs(this))
            return;

        setContentView(R.layout.activity_video);
        mVideoView = (VideoView) findViewById(R.id.surface_view);

        Intent intent = getIntent(); // get the video's url to be played from the intent from ExerciseDetailsActivity
        if (intent != null) {
            videoURL = intent.getStringExtra("VIDEO_URL");

        }

        if (videoURL.equals("")) {
            Toast.makeText(this, "Please set the video path for your media file", Toast.LENGTH_LONG).show();
        } else {
            mVideoView.setVideoPath(videoURL);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(mVideoView);
            mVideoView.setMediaController(mediaController);

            mVideoView.requestFocus();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }

    }

    public void startPlay(View view) {
        if (!TextUtils.isEmpty(videoURL)) {
            mVideoView.setVideoPath(videoURL);
        }
    }

    public void openVideo(View View) {
        mVideoView.setVideoPath(videoURL);
    }

}
