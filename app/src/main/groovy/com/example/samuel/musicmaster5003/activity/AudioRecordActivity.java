package com.example.samuel.musicmaster5003.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.samuel.musicmaster5003.ExtAudioRecorder;
import com.example.samuel.musicmaster5003.R;
import com.example.samuel.musicmaster5003.Spectrogram;
import com.example.samuel.musicmaster5003.SpectrogramMaker;
import com.example.samuel.musicmaster5003.musicmodel.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 10/25/2017.
 */
public class AudioRecordActivity extends AppCompatActivity {
    private final String LOG_TAG = "AudioRecordActivity";

    private ExtAudioRecorder audioRecorder = ExtAudioRecorder.getInstance(false);
    private String filePath = null;
    private RecordButton recordButton;
    private TextView infoView;

    private enum State { WAITING, RECORDING, PROCESSING, ERROR }
    private State state = State.WAITING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePath = getExternalCacheDir() + "/testtest.wav";
        setContentView(R.layout.activity_audio_record);
        recordButton = new RecordButton((Button)findViewById(R.id.recordButton));
        infoView = (TextView)findViewById(R.id.infoView);
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
                            button.setText(R.string.start_recording);
                            button.setEnabled(true);
                            state = State.WAITING;
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void startRecording() {
        audioRecorder.setOutputFile(filePath);
        audioRecorder.prepare();
        audioRecorder.start();
    }

    private void stopRecording() {
        try {
            audioRecorder.stop();
            audioRecorder.reset();
            final Spectrogram spectrogram = SpectrogramMaker.makeSpectrogram(this, filePath, true);
            List<List<Note>> allNotes = new ArrayList<>();
            for (int i = 0; i < spectrogram.getImage().getWidth(); i++) {
                allNotes.add(i, spectrogram.getNotes(i));
            }
            List<Chord> chordSlices = new ArrayList<>();
            for (int i = 0; i < allNotes.size() - 3; i++) {
                Chord chord = Chord.fromPitchClasses(MusicUtil.findMostProminentPitchesForWindow(allNotes, i));
                if (chord != null) {
                    chordSlices.add(chord);
                }
            }
            List<Chord> chords = MusicUtil.getChords(chordSlices);
            StringBuilder sb = new StringBuilder("Detected chords:\n");
            for (int i = 0; i < chords.size(); i++) {
                sb.append(chords.get(i).toString()).append("\n");
            }
            infoView.setText(sb.toString());
            state = State.WAITING;
        } catch (IOException e) {
            showAndLogError(e);
        } finally {
            audioRecorder.release();
        }
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

    private void showAndLogError(Exception e) {
        state = State.ERROR;
        infoView.setText("There has been an error, check logs");
        Log.e(LOG_TAG, e.getMessage());
        e.printStackTrace();
    }
}
