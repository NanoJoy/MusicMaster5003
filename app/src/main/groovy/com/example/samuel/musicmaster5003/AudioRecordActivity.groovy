package com.example.samuel.musicmaster5003

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

/**
 * Created by Samuel on 10/25/2017.
 */
public class AudioRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final button = findViewById(R.id.recordButton) as Button
    }

    class PlayButton extends Button {
        private isRecording = false
        PlayButton(Context context) {
            super(context)

        }
    }
}
