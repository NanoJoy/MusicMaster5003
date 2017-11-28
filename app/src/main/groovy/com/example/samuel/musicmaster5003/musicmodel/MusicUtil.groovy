package com.example.samuel.musicmaster5003.musicmodel
/**
 * Created by Samuel on 10/17/2017.
 */

public class MusicUtil {
    final static double A4_PITCH = 440
    final static double TWELFTH_ROOT_2 = 2.0 ** (1.0 / 12)

    public static PitchClass pitchToNearestNote(int pitch) {
        if (pitch == A4_PITCH) {
            return PitchClass.A
        }
        pitch < A4_PITCH ? findNoteLower(pitch, 1, A4_PITCH) : findNoteHigher(pitch, 1, A4_PITCH)
    }

    private static PitchClass findNoteHigher(int pitch, int numSteps, double lastPitch) {
        double thisPitch = A4_PITCH * (TWELFTH_ROOT_2 ** numSteps)
        if (A4_PITCH * (TWELFTH_ROOT_2 ** numSteps) > pitch) {
            return PitchClass.getBySteps((thisPitch - pitch).abs() <= (lastPitch - pitch).abs() ? numSteps : numSteps - 1)
        }
        return findNoteHigher(pitch, numSteps + 1, thisPitch)
    }

    private static PitchClass findNoteLower(int pitch, int numSteps, double lastPitch) {
        double thisPitch = A4_PITCH * (TWELFTH_ROOT_2 ** numSteps)
        if (A4_PITCH * (TWELFTH_ROOT_2 ** numSteps) < pitch) {
            return PitchClass.getBySteps((thisPitch - pitch).abs() <= (lastPitch - pitch).abs() ? numSteps : numSteps + 1)
        }
        return findNoteLower(pitch, numSteps - 1, thisPitch)
    }

    public static List<PitchClass> findMostProminentPitchesForWindow(List<List<Note>> window, int startSlice, int windowSize = 3) {
        final double REQUIRED_SHARE = 0.15
        def intensities = new HashMap<PitchClass, Double>()
        PitchClass.values().each { intensities.put(it, 0.0) }
        def totalIntensity = 0.0
        window.eachWithIndex { List<Note> it, int i ->
            if (i >= startSlice && i < startSlice + windowSize) {
                it.each { note ->
                    intensities.put(note.pitchClass, intensities.get(note.pitchClass) + note.intensity)
                    totalIntensity += note.intensity
                }
            }
        }
        def pitches = new ArrayList<PitchClass>()
        PitchClass.values().each {
            if (intensities.get(it) >= totalIntensity * REQUIRED_SHARE) {
                pitches.add(it)
                intensities.remove(it)
            }
        }
        if (pitches.size() < 3) {
            pitches.add(intensities.max { it.value }.key)
        }
        pitches
    }

    static List<ChordWithLength> getChords(List<ChordWithLength> chordSlices) {
        def currentChord = new Chord(null, Chord.Quality.UNKNOWN, [])
        def chords = new ArrayList<ChordWithLength>()
        0.upto(chordSlices.size() - 1) {
            def slice = chordSlices[it].chord
            def length = chordSlices[it].length
            if (slice == null) {
                return true
            }
            def fifthOfCurrent = slice.quality == Chord.Quality.OPEN_FIFTH && slice.rootNote == currentChord.rootNote
            if (slice.equals(currentChord) ||  fifthOfCurrent) {
                chords[chords.size() - 1] = new ChordWithLength(currentChord, chords[chords.size() - 1].length + length)
                return true
            }
            def currentOfFifth = [Chord.Quality.MAJOR, Chord.Quality.MINOR].contains(slice.quality) && slice.rootNote == currentChord.rootNote
            if (currentOfFifth) {
                chords[chords.size() - 1] = new ChordWithLength(slice, chords[chords.size() - 1].length + length)
                currentChord = slice
                return true
            }
            if (slice.quality != Chord.Quality.UNKNOWN) {
                currentChord = slice
                chords.add(chordSlices[it])
                return true
            }
        }
        chords.findAll { it.length > 1 }
    }
}
