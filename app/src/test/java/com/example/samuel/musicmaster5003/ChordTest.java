package com.example.samuel.musicmaster5003;

import com.example.samuel.musicmaster5003.musicmodel.Chord;
import com.example.samuel.musicmaster5003.musicmodel.PitchClass;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;


/**
 * Created by Samuel on 10/19/2017.
 */
public class ChordTest {
    @Test
    public void wrongNumberOfNotesReturnsEmpty() throws Exception {
        List<PitchClass> twoNotes = new ArrayList<>();
        twoNotes.add(PitchClass.A);
        twoNotes.add(PitchClass.E);
        assertFalse(Chord.fromPitchClasses(twoNotes) != null);
        List<PitchClass> fiveNotes = new ArrayList<>();
        fiveNotes.add(PitchClass.A);
        fiveNotes.add(PitchClass.E);
        fiveNotes.add(PitchClass.C);
        fiveNotes.add(PitchClass.G);
        fiveNotes.add(PitchClass.Bb);
        assertFalse(Chord.fromPitchClasses(fiveNotes) != null);
    }

    @Test
    public void nonThirdBasedChordReturnsUnknown() {
        List<PitchClass> notes = Arrays.asList(PitchClass.A, PitchClass.B, PitchClass.E);
        checkChord(notes, Chord.Quality.UNKNOWN, null);
    }

    @Test
    public void majorChordTest() {
        List<PitchClass> amaj = Arrays.asList(PitchClass.A, PitchClass.Db, PitchClass.E);
        checkChord(amaj, Chord.Quality.MAJOR, PitchClass.A);
        List<PitchClass> dmaj = Arrays.asList(PitchClass.A, PitchClass.D, PitchClass.Gb);
        checkChord(dmaj, Chord.Quality.MAJOR, PitchClass.D);
        List<PitchClass> fmaj = Arrays.asList(PitchClass.A, PitchClass.C, PitchClass.F);
        checkChord(fmaj, Chord.Quality.MAJOR, PitchClass.F);
    }

    @Test
    public void minorChordTest() {
        List<PitchClass> amin = Arrays.asList(PitchClass.A, PitchClass.C, PitchClass.E);
        checkChord(amin, Chord.Quality.MINOR, PitchClass.A);
        List<PitchClass> dmin = Arrays.asList(PitchClass.A, PitchClass.D, PitchClass.F);
        checkChord(dmin, Chord.Quality.MINOR, PitchClass.D);
        List<PitchClass> fsmin = Arrays.asList(PitchClass.A, PitchClass.Db, PitchClass.Gb);
        checkChord(fsmin, Chord.Quality.MINOR, PitchClass.Gb);
    }

    private void checkChord(List<PitchClass> notes, Chord.Quality quality, PitchClass root) {
        Chord chord = Chord.fromPitchClasses(notes);
        assertTrue(chord != null);
        assertEquals(chord.getQuality(), quality);
        assertEquals(chord.getRootNote(), root);
    }
}
