package com.example.samuel.musicmaster5003.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.samuel.musicmaster5003.R;
import com.example.samuel.musicmaster5003.Spectrogram;
import com.example.samuel.musicmaster5003.SpectrogramMaker;
import com.example.samuel.musicmaster5003.musicmodel.ChordWithLength;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Samuel on 11/8/2017.
 */

public class TestFromWav extends AppCompatActivity {
    private static final String LOG_TAG = "TestFromWav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_from_wav);
        TextView testText = (TextView)findViewById(R.id.testText);
        InputStream is = getResources().openRawResource(getResources().getIdentifier("cgaf2", "raw", getPackageName()));
        try {
            byte[] data = IOUtils.toByteArray(is);
            ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(data, 24, 28));
            int sampleRate = wrapped.order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
            byte[] soundData = Arrays.copyOfRange(data, 44, data.length);
            double[] temp = new double[soundData.length / 2];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = (short) ((soundData[i * 2 + 1] & 0xff) << 8) | (soundData[i * 2] & 0xff);
            }
            final Spectrogram spectrogram = SpectrogramMaker.makeSpectrogram(temp, sampleRate);
            List<ChordWithLength> chords = spectrogram.findChords();
            final StringBuilder sb = new StringBuilder("Detected chords:\n");
            for (int i = 0; i < chords.size(); i++) {
                sb.append(chords.get(i).toString()).append("\n");
            }
            testText.setText(sb.toString());
        } catch (IOException e) {
            testText.setText("Could not open file");
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
