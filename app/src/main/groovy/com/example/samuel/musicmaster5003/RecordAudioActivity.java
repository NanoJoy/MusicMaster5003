package com.example.samuel.musicmaster5003;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecordAudioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);

        try {
            final Spectrogram spectrogram = SpectrogramMaker.makeSpectrogram(this, "ukeprog");
            ImageView image = (ImageView) findViewById(R.id.img);
            image.setImageBitmap(spectrogram.getImage());
            List<List<Note>> allNotes = new ArrayList<>();
            for (int i = 0; i < spectrogram.getImage().getWidth(); i++) {
                allNotes.add(i, spectrogram.getNotes(i));
            }
            List<Chord> chordSlices = new ArrayList<>();
            for (int i = 0; i < allNotes.size() - 3; i++) {
                Optional<Chord> chordOp = Chord.fromPitchClasses(MusicUtil.findMostProminentPitchesForWindow(allNotes, i));
                if (chordOp.isPresent()) {
                    chordSlices.add(chordOp.get());
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
