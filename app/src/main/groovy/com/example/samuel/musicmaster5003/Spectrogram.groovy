package com.example.samuel.musicmaster5003

import android.graphics.Bitmap
import android.support.annotation.NonNull
import com.example.samuel.musicmaster5003.musicmodel.Chord
import com.example.samuel.musicmaster5003.musicmodel.ChordWithLength
import com.example.samuel.musicmaster5003.musicmodel.MusicUtil
import com.example.samuel.musicmaster5003.musicmodel.Note
import com.example.samuel.musicmaster5003.musicmodel.PitchClass

import java.security.InvalidParameterException

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

    List<Note> getNotes(int sliceIndex) {
        double multiplier = 523.25 / 86;
        List<Integer> brightSpots = getBrightSpots(sliceIndex);
        List<Note> notes = new ArrayList<>();
        brightSpots.each {
            PitchClass pitch = MusicUtil.pitchToNearestNote((int)(it * multiplier));
            notes.add(new Note(pitch, data[sliceIndex][it]));
        }
        return notes;
    }

    private List<Integer> findChordStarts() {
        double lastAvg = 0
        int curMax = -1
        List<Integer> starts = []
        for (int i = 0; i < data.length; i++) {
            def avg = data[i].sum() / data[i].size()
            if (avg <= lastAvg) {
                if (curMax == i - 1) {
                    starts.add(i - 1)
                    curMax = i
                }
            } else {
                curMax = i
            }
            lastAvg = avg
        }
        starts
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

    List<ChordWithLength> findChords(double pickiness) {
        def chords = []
        def allNotes = []
        0.upto(data.length - 1, {
            allNotes.add(it, getNotes(it))
        })
        def starts = findChordStarts()
        starts.add(data.length)
        0.upto(starts.size() - 2, {
            def chunkSize = starts[it + 1] - starts[it]
            def pitches = MusicUtil.findMostProminentPitchesForWindow(allNotes, starts[it], chunkSize, pickiness)
            chords.add(new ChordWithLength(Chord.fromPitchClasses(pitches), chunkSize))
        })
        MusicUtil.getChords(chords)
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
