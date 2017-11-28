package com.example.samuel.musicmaster5003.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.samuel.musicmaster5003.*;
import com.example.samuel.musicmaster5003.musicmodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 10/25/2017.
 */
public class AudioRecordActivity extends AppCompatActivity {
    private final String LOG_TAG = "AudioRecordActivity";

    private ExtAudioRecorder audioRecorder = null;
    private String fileName = null;
    private String filePath = null;
    private SeekBar pickinessBar = null;
    private RecordButton recordButton;

    private enum State { WAITING, RECORDING, PROCESSING, ERROR }
    private State state = State.WAITING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileName = "/testtest.wav";
        filePath = getExternalCacheDir() + fileName;
        setContentView(R.layout.activity_audio_record);
        recordButton = new RecordButton((Button)findViewById(R.id.recordButton));
        pickinessBar = (SeekBar)findViewById(R.id.pickinessBar);
    }

    private class ProcessingRunnable implements Runnable {
        private double[] data;
        private int sampleRate;

        public ProcessingRunnable(double[] data, int sampleRate) {
            this.data = data;
            this.sampleRate = sampleRate;
        }

        @Override
        public void run() {
            final Spectrogram spectrogram = SpectrogramMaker.makeSpectrogram(data, sampleRate);
            int sliderPos = pickinessBar.getProgress();
            double pickiness = sliderPos == 0 ? 0.01 : sliderPos / 100.0;
            List<ChordWithLength> chords = spectrogram.findChords(pickiness);
            List<Chord> justChords = new ArrayList<>();
            for (int i = 0; i < chords.size(); i++) {
                justChords.add(i, chords.get(i).chord);
            }
            Intent intent = new Intent(getBaseContext(), ShowResultsActivity.class);
            intent.putExtra("song", new Song(filePath, justChords));
            startActivity(intent);
        }
    }

    private class RecordButton {
        private Button button;
        RecordButton(Button but) {
            button = but;
            button.setText(R.string.start_recording);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (state) {
                        case WAITING:
                            startRecording();
                            button.setText(R.string.stop_recording);
                            state = State.RECORDING;
                            break;
                        case RECORDING:
                            state = State.PROCESSING;
                            button.setEnabled(false);
                            button.setText(R.string.processing);
                            stopRecording();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        void setState(State state) {
            if (state == State.WAITING) {
                button.setEnabled(true);
                button.setText(R.string.start_recording);
            }
        }
    }

    private void startRecording() {
        audioRecorder = ExtAudioRecorder.getInstance(false);
        audioRecorder.setOutputFile(filePath);
        audioRecorder.prepare();
        audioRecorder.start();
    }

    private void stopRecording() {
        try {
            double[] data = audioRecorder.getData();
            int sampleRate = audioRecorder.getSamplingRate();
            audioRecorder.stop();
            audioRecorder.reset();
            Thread processingThread = new Thread(new ProcessingRunnable(data, sampleRate));
            processingThread.start();
        } catch (Exception e) {
            showAndLogError(e);
        } finally {
            audioRecorder.release();
            audioRecorder = null;
        }
    }

    public void onStop() {
        super.onStop();
        if (audioRecorder != null) {
            audioRecorder.release();
        }
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

    private void showAndLogError(Exception e) {
        state = State.ERROR;
        Log.e(LOG_TAG, e.getMessage());
        e.printStackTrace();
    }
}
