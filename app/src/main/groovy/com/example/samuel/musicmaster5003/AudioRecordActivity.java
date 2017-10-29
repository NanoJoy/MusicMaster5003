package com.example.samuel.musicmaster5003;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Samuel on 10/25/2017.
 */
public class AudioRecordActivity extends AppCompatActivity {
    private final String LOG_TAG = "AudioRecordActivity";

    private ExtAudioRecorder audioRecorder = ExtAudioRecorder.getInstance(false);
    private String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePath = getExternalCacheDir() + "/test.wav";
        setContentView(R.layout.activity_audio_record);
        final RecordButton recordButton = new RecordButton((Button)findViewById(R.id.recordButton));
    }

    public class RecordButton {
        private boolean isRecording = false;
        private Button button;
        public RecordButton(Button but) {
            button = but;
            button.setText(R.string.start_recording);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecordClick(isRecording);
                    button.setText(isRecording ? R.string.start_recording : R.string.stop_recording);
                    isRecording = !isRecording;
                }
            });
        }

    }

    private void onRecordClick(boolean isRecording) {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        audioRecorder.setOutputFile(filePath);
        audioRecorder.prepare();
        audioRecorder.start();
    }

    private void stopRecording() {
        audioRecorder.stop();
        audioRecorder.reset();
        logFile(filePath);
    }

    public void onStop() {
        super.onStop();
        audioRecorder.release();
    }

    private void logFile(String path) {
        FileReader fr = null;
        try {
            fr = new FileReader(path);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "Could not find file");
        }
        BufferedReader br = new BufferedReader(fr);
        String s = null;
        try {
            while ((s = br.readLine()) != null) {
                Log.i(LOG_TAG, s);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
