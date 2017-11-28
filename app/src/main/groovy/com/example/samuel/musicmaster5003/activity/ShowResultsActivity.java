package com.example.samuel.musicmaster5003.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.samuel.musicmaster5003.R;
import com.example.samuel.musicmaster5003.musicmodel.Chord;
import com.example.samuel.musicmaster5003.musicmodel.Song;

import java.util.List;

/**
 * Created by Samuel on 11/28/2017.
 */

public class ShowResultsActivity extends AppCompatActivity {
    private ListView chordListView = null;
    private Button backButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        chordListView = (ListView)findViewById(R.id.chordList);

        Song song = getIntent().getParcelableExtra("song");
        List<Chord> chords = song.getChords();
        Chord[] chordsArray = new Chord[chords.size()];
        for (int i = 0; i < chords.size(); i++) {
            chordsArray[i] = chords.get(i);
        }
        ArrayAdapter<Chord> adapter = new ArrayAdapter<Chord>(this, R.layout.textview_chord, chordsArray);
        chordListView.setAdapter(adapter);

        backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AudioRecordActivity.class);
                startActivity(intent);
            }
        });
    }
}
