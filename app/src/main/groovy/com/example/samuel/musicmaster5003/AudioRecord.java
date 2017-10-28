package com.example.samuel.musicmaster5003;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;

import groovy.transform.CompileStatic;

/**
 * Created by Samuel on 10/25/2017.
 */
public class AudioRecord extends AppCompatActivity {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }

    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }

    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }


        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        setContentView(ll);
        Log.d(LOG_TAG, "hi");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }


        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

    }

    private static final String LOG_TAG = "AudioRecord";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;
    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;
    private PlayButton mPlayButton = null;
    private MediaPlayer mPlayer = null;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};

    public class RecordButton extends AppCompatButton {
        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    onRecord(getmStartRecording());
                    if (getmStartRecording()) {
                        setText("Stop recording");
                    } else {
                        setText("Start recording");
                    }

                    mStartRecording = !getmStartRecording();
                }

            });
        }

        public boolean getmStartRecording() {
            return mStartRecording;
        }

        public boolean ismStartRecording() {
            return mStartRecording;
        }

        public void setmStartRecording(boolean mStartRecording) {
            this.mStartRecording = mStartRecording;
        }

        private boolean mStartRecording = true;
    }

    public class PlayButton extends AppCompatButton {
        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    onPlay(getmStartPlaying());
                    if (getmStartPlaying()) {
                        setText("Stop playing");
                    } else {
                        setText("Start playing");
                    }

                    mStartPlaying = !getmStartPlaying();
                }

            });
        }

        public boolean getmStartPlaying() {
            return mStartPlaying;
        }

        public boolean ismStartPlaying() {
            return mStartPlaying;
        }

        public void setmStartPlaying(boolean mStartPlaying) {
            this.mStartPlaying = mStartPlaying;
        }

        private boolean mStartPlaying = true;
    }
}
