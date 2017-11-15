package com.example.samuel.musicmaster5003.musicmodel;

/**
 * Created by Samuel on 11/8/2017.
 */

public class ChordWithLength {
    public final Chord chord;
    public final int length;

    public ChordWithLength(Chord chord, int length) {
        this.chord = chord;
        this.length = length;
    }

    public String toString() {
        return chord.toString() + " " + length;
    }
}
