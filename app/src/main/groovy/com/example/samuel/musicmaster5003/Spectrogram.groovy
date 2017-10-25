package com.example.samuel.musicmaster5003;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spectrogram {
    private final double PERCENTILE = 0.98;
    private double[][] data;
    private int lowestFreq;
    private int hightestFreq;
    private Bitmap image;

    public Spectrogram(Bitmap image, double[][] data, int lowestFreq, int highestFreq) {

        if (highestFreq <= lowestFreq) {
            throw new InvalidParameterException("highestFreq must be higher than lowestFreq");
        }
        this.data = data;
        this.image = image;
        this.lowestFreq = lowestFreq;
        this.hightestFreq = highestFreq;
    }

    public List<Note> getFourLoudestNotes(int sliceIndex) {
        def notesSorted = getNotes(sliceIndex).sort { a, b ->
            b.intensity <=> a.intensity
        }
        def uniqueNotes = notesSorted.unique { a, b ->
            a.pitchClass <=> b.pitchClass
        }
        return notesSorted.collate(4)[0]
    }

    public List<Note> getNotes(int sliceIndex) {
        double multiplier = 523.25 / 86;
        List<Integer> brightSpots = getBrightSpots(sliceIndex);
        List<Note> notes = new ArrayList<>();
        brightSpots.each {
            PitchClass pitch = MusicUtil.pitchToNearestNote((int)(it * multiplier));
            notes.add(new Note(pitch, data[sliceIndex][it]));
        }
        return notes;
    }

    @NonNull
    private List<Integer> getBrightSpots(int sliceIndex) {
        List<Integer> spots = new ArrayList<>();
        double[] slice = data[sliceIndex];
        double cutoff = getPercentile(slice);
        boolean inSpot = false;
        int spotMax = 0;
        for (int i = 0; i < slice.length; i++) {
            if (slice[i] > cutoff) {
                if (!inSpot) {
                    inSpot = true;
                    spotMax = i;
                } else {
                    if (slice[i] > slice[spotMax]) {
                        spotMax = i;
                    }
                }
            } else {
                if (inSpot) {
                    if (spotMax > 0) {
                        spots.add(spotMax);
                    }
                    inSpot = false;
                }
            }
        }
        return spots;
    }

    private double getPercentile(double[] arr) {
        double[] copy = arr.clone();
        Arrays.sort(copy);
        return copy[(int)(arr.length * PERCENTILE)];
    }

    public int getLowestFreq() {
        return lowestFreq;
    }

    public int getHightestFreq() {
        return hightestFreq;
    }

    public Bitmap getImage() {
        return image;
    }
}
