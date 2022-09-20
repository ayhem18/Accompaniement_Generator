package Chords;

import MidiMusicUtilities.MusicUtilities;
import org.jfugue.theory.Chord;

import java.util.Random;

public class ChordsReproduction {

    static ChordObject crossOver(ChordObject chord1, ChordObject chord2) {

        assert chord1.chordDuration == chord2.chordDuration;

        Random random = new Random();
        // an array of chordObject to facilitate the randomization
        ChordObject[] chords = new ChordObject[] {chord1, chord2};
        Chord newChord =
                MusicUtilities.getChord(chords[random.nextInt(2)].actualChord.getRoot().getPositionInOctave(),
                        chords[random.nextInt(2)].actualChord.getChordType(),
                        chord2.chordDuration,
                        chords[random.nextInt(2)].actualChord.getInversion());
        ChordObject worseChord = chord1.fitnessValue > chord2.fitnessValue ? chord2 : chord1;
        return new ChordObject(worseChord.keyScale, worseChord.notesPrevious, worseChord.notesCurrent, worseChord.notesNext,
                worseChord.chordDuration, newChord);
    }

    static Chord crossOver(Chord chord1, Chord chord2, double duration) {
        Random random = new Random();
        // an array of chordObject to facilitate the randomization
        Chord[] chords = new Chord[] {chord1, chord2};
        return MusicUtilities.getChord(chords[random.nextInt(2)].getRoot().getPositionInOctave(),
                        chords[random.nextInt(2)].getChordType(),
                        duration,
                        chords[random.nextInt(2)].getInversion());
    }

    /**
     * This method mutates the type of the passed Chord
     * The mutation operation mainly sets Minor to Minor-7 and vise versa
     * and sets Major to Major-7 and vise versa
     * without modifying the rest of the chord's aspects
     * @param chord the chord object to mutate
     */
    static void mutateChordType(ChordObject chord) {

        if (chord.actualChord.isMinor()) {
            chord.actualChord = MusicUtilities.getChord(chord.actualChord.getRoot().getPositionInOctave(),
                    // sets the type to minor-7
                    MusicUtilities.MINOR_7_CHORD,
                    chord.chordDuration,
                    chord.actualChord.getInversion());
        }
        else if (chord.actualChord.isMajor()) {
            chord.actualChord = MusicUtilities.getChord(chord.actualChord.getRoot().getPositionInOctave(),
                    // sets the chord to major-7
                    MusicUtilities.MAJOR_7_CHORD,
                    chord.chordDuration,
                    chord.actualChord.getInversion());
        }
        else if (chord.actualChord.getChordType().equalsIgnoreCase(MusicUtilities.MAJOR_7_CHORD_NAME)) {
            chord.actualChord = MusicUtilities.getChord(chord.actualChord.getRoot().getPositionInOctave(),
                    // sets the chord to the basic major chord
                    MusicUtilities.MAJOR_CHORD,
                    chord.chordDuration,
                    chord.actualChord.getInversion());
        }
        else {
            chord.actualChord = MusicUtilities.getChord(chord.actualChord.getRoot().getPositionInOctave(),
                    // sets the chord to the basic minor chord
                    MusicUtilities.MINOR_CHORD,
                    chord.chordDuration,
                    chord.actualChord.getInversion());
        }
    }

    static void mutateChordInversion(ChordObject chord) {
        int inversion = chord.actualChord.getInversion();
        int newInversion  = chord.actualChord.getInversion() + (int) Math.ceil(Math.random() * 2);
        chord.actualChord.setInversion(newInversion);

    }
}
