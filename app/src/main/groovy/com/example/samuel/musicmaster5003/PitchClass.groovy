package com.example.samuel.musicmaster5003

/**
 * Created by Samuel on 10/17/2017.
 */

public enum PitchClass {
    A(0), Bb(1), B(2), C(3), Db(4), D(5), Eb(6), E(7), F(8), Gb(9), G(10), Ab(11)

    private final int steps

    PitchClass(int steps) {
        this.steps = steps
    }

    public int getSteps() {
        return steps;
    }

    static PitchClass getBySteps(int steps) {
        if (steps >= 0) {
            return values().find { note ->
                note.getSteps() == steps % 12
            }
        }
        if ((steps * -1) % 12 == 0) {
            return A
        }
        return values().find { note ->
            12 - note.getSteps() == (steps * -1) % 12
        }
    }
}
