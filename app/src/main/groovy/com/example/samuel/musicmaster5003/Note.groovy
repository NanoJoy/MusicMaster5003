package com.example.samuel.musicmaster5003

/**
 * Created by Samuel on 10/17/2017.
 */

public class Note {
    final PitchClass pitchClass
    final double intensity

    Note(PitchClass pitchClass, double intensity) {
        this.pitchClass = pitchClass
        this.intensity = intensity
    }

    public String toString() {
        return pitchClass.name() + " " + intensity;
    }
}
