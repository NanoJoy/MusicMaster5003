package com.example.samuel.musicmaster5003.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.samuel.musicmaster5003.musicmodel.Song;

/**
 * Created by Samuel on 11/28/2017.
 */

public class ShowResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Song song = getIntent().getParcelableExtra("song");
    }
}
