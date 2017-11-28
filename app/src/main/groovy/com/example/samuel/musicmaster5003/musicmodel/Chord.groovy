package com.example.samuel.musicmaster5003.musicmodel
/**
 * Created by Samuel on 10/18/2017.
 */

public class Chord {
    static enum Quality {
        OPEN_FIFTH("Open fifth", 7, null, null),
        MAJOR("Major", 4, 3, null),
        MINOR("Minor", 3, 4, null),
        DIMINISHED("Dim", 3, 3, null),
        AUGMENTED("Aug", 4, 4, null),
        MAJOR_7("Maj7", 4, 3, 4),
        DOM_7("Dom7", 4, 3, 3),
        MINOR_7("Min7", 3, 4, 3),
        HALF_DIM_7("HalfDim7", 3, 3, 4),
        DIM_7("Dim7", 3, 3, 3),
        UNKNOWN("Unknown", null, null, null)

        final String displayName
        final Integer firstInterval
        final Integer secondInterval
        final Integer thirdInterval

        Quality(String displayName, Integer firstInterval, Integer secondInterval, Integer thirdInterval) {
            this.displayName = displayName
            this.firstInterval = firstInterval
            this.secondInterval = secondInterval
            this.thirdInterval = thirdInterval
        }
    }

    private final PitchClass rootNote
    private final Quality quality
    private final List<PitchClass> pitches

    Chord(rootNote, quality, pitches) {
        this.rootNote = rootNote
        this.quality = quality
        this.pitches = pitches
    }

    static Chord fromString(String description) {
        def parts = description.split(":");
        def root = PitchClass.values().find { it.name() == parts[0] }
        def qual = Quality.values().find { it.displayName == parts[1] }
        return new Chord(root, qual, null)
    }

    static Chord fromPitchClasses(List<PitchClass> pitchClasses) {
        def numPitches = pitchClasses.size()
        if (numPitches < 2 || numPitches > 4) {
            return null
        }
        def sortedPitches = pitchClasses.sort { a, b -> a.steps <=> b.steps }
        if (numPitches == 2) {
            switch (sortedPitches[1].steps - sortedPitches[0].steps) {
                case 7:
                    return new Chord(sortedPitches[0], Quality.OPEN_FIFTH, sortedPitches)
                    break
                case 5:
                    return new Chord(sortedPitches[1], Quality.OPEN_FIFTH, sortedPitches)
                    break
                default:
                    //println("Slice " + pitchClasses[0].name() + " " + pitchClasses[1].name())
                    return new Chord(null, Quality.UNKNOWN, sortedPitches)
                    break
            }
        }

        def steps = sortedPitches.collect { it.steps }
        def inRootForm = checkIfRootForm(steps)
        (numPitches - 1).times {
            if (inRootForm) { return true }
            frontToBack(sortedPitches)
            steps[0] = steps[0] + 12
            frontToBack(steps)
            inRootForm = checkIfRootForm(steps)
        }
        if (!inRootForm) {
            return new Chord(null, Quality.UNKNOWN, sortedPitches)
        }
        def qualities = new ArrayList<Quality>()
        qualities.addAll(Quality.values())
        qualities = qualities.findAll {
            it.firstInterval == steps[1] - steps[0]
        }
        qualities = qualities.findAll {
            it.secondInterval == steps[2] - steps[1]
        }
        if (numPitches == 4) {
            qualities = qualities.findAll {
                it.thirdInterval == steps[2] - steps[1]
            }
        }
        if (qualities.size() == 0) {
            return new Chord(null, Quality.UNKNOWN, sortedPitches)
        }
        new Chord(sortedPitches[0], qualities[0], sortedPitches)
    }

    private static void frontToBack(List list) {
        def front = list[0]
        0.upto(list.size() - 2) { list[it] = list[it + 1] }
        list[list.size() - 1] = front
    }

    private static boolean checkIfRootForm(List<Integer> steps) {
        def inRootForm = true
        1.upto(steps.size() - 1) {
            def diff = steps[it] - steps[it - 1]
            if (diff != 3 && diff != 4) {
                inRootForm = false
            }
        }
        inRootForm
    }

    PitchClass getRootNote() {
        rootNote
    }

    Quality getQuality() {
        quality
    }

    List<PitchClass> getPitches() {
        pitches
    }

    boolean equals(Chord other) {
        if (quality == Quality.UNKNOWN) {
            return false
        }
        rootNote == other.rootNote && quality == other.quality
    }

    boolean noteBelongsTo(PitchClass pitch) {
        pitches.contains(pitch)
    }

    String toString() {
        if (quality == Quality.UNKNOWN) {
            def sb = new StringBuilder()
            pitches.each { sb.append(it.name()) }
            return sb.toString()
        }
        rootNote.name() + " " + quality.displayName
    }
}
