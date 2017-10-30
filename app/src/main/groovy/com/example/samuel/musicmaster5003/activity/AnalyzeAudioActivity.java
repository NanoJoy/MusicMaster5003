package com.example.samuel.musicmaster5003.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.samuel.musicmaster5003.R;
import com.example.samuel.musicmaster5003.Spectrogram;
import com.example.samuel.musicmaster5003.SpectrogramMaker;
import com.example.samuel.musicmaster5003.musicmodel.Chord;
import com.example.samuel.musicmaster5003.musicmodel.MusicUtil;
import com.example.samuel.musicmaster5003.musicmodel.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnalyzeAudioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_spectrogram);

        try {
            final Spectrogram spectrogram = SpectrogramMaker.makeSpectrogram(this, "ukeprog", false);
            ImageView image = (ImageView) findViewById(R.id.img);
            image.setImageBitmap(spectrogram.getImage());
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
            for (int i = 0; i < chords.size(); i++) {
                System.out.println("Chord " + i + ": " + chords.get(i).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
