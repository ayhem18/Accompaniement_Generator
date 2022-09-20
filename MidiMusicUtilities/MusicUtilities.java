package MidiMusicUtilities;

import org.jfugue.theory.Chord;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicUtilities {
    static final Intervals MINOR_INTERVAL = new Intervals("1 2 b3 4 5 b6 b7");
    static final Intervals MAJOR_INTERVAL = new Intervals("1 2 3 4 5 6 7");

    static final int C_RIGHT_HAND_VALUE = 60;
    static final byte DENOMINATOR_LIMIT = 6;
    static final int C_LEFT_HAND_VALUE = 48;
    static final List<Double> VALID_DURATIONS ;
    static int QUARTER_TO_WHOLE = 4;
    static double DEFAULT_DURATION = 0.25;
    static {
        VALID_DURATIONS = new ArrayList<>();
        for (int i = 0; i <= MusicUtilities.DENOMINATOR_LIMIT; i++) {
            VALID_DURATIONS.add(1.0 / Math.pow(2, i));
        }
    }

    static double fitDuration(double noteDuration) {
        double difference = Integer.MAX_VALUE;
        double bestDuration = 0;
        for (double duration : VALID_DURATIONS) {
            double d;
            if (difference > (d = Math.abs(duration - noteDuration)) ) {
                difference = d;
                bestDuration = duration;
            }
        }
        return bestDuration;
    }

    public static final String MINOR_CHORD_NAME = "MIN";
    public static final String MAJOR_CHORD_NAME = "MAJ";
    public static final String MAJOR_7_CHORD_NAME = "MAJ7";
    public static final String MINOR_7_CHORD_NAME = "MIN7";

    public    static final Intervals MINOR_CHORD = new Intervals("1 b3 5");
    public static final Intervals MAJOR_CHORD = new Intervals("1 3 5");
    public static final Intervals MAJOR_7_CHORD = new Intervals("1 3 5 7");
    public static final Intervals MINOR_7_CHORD = new Intervals("1 b3 5 b7");

    static final List<Chord> MAJOR_CHORDS = new ArrayList<>();
    static final List<Chord> MINOR_CHORDS = new ArrayList<>();
    static final List<Chord> MAJOR_7_CHORDS = new ArrayList<>();
    static final List<Chord> MINOR_7_CHORDS = new ArrayList<>();

    static final List<Chord> ALL_CHORDS = new ArrayList<>();
    static {
        for (int i = C_LEFT_HAND_VALUE; i < C_LEFT_HAND_VALUE + Note.OCTAVE; i++) {
            MAJOR_CHORDS.add(new Chord(new Note(i), MAJOR_CHORD));
            MINOR_CHORDS.add(new Chord(new Note(i), MINOR_CHORD));
            MAJOR_7_CHORDS.add(new Chord(new Note(i), MAJOR_7_CHORD));
            MINOR_7_CHORDS.add(new Chord(new Note(i), MINOR_7_CHORD));
        }

        ALL_CHORDS.addAll(MAJOR_7_CHORDS);
        ALL_CHORDS.addAll(MINOR_7_CHORDS);
        ALL_CHORDS.addAll(MAJOR_CHORDS);
        ALL_CHORDS.addAll(MINOR_CHORDS);
    }

    public static Chord getRandomChordWithDuration(double duration) {
        Random generator = new Random();
        int choice = generator.nextInt(2);
        // if the choice value is equal to 1 then return a minor chord
        if (choice == 1) {
            return new Chord(
                    new Note(generator.nextInt(Note.OCTAVE) + C_LEFT_HAND_VALUE, duration), MINOR_CHORD)
                    .setInversion(generator.nextInt(3));
        }

        return new Chord(
                new Note(generator.nextInt(Note.OCTAVE) + C_LEFT_HAND_VALUE, duration), MAJOR_CHORD)
                .setInversion(generator.nextInt(3));
    }

    public static Chord getChord(int positionInOctave, Intervals chord, double duration, int inversion) {
        return new Chord(new Note(C_LEFT_HAND_VALUE + positionInOctave, duration), chord).setInversion(inversion);
    }

    public static Chord getChord(int positionInOctave, String chordType, double duration, int inversion) {
        return getChord(positionInOctave, Chord.getIntervals(chordType), duration, inversion);
    }

    /**
     * @param note any Note object
     * @return a note object with the default parameters to avoid issues with comparisons between Notes
     */
    public static Note commonNoteVersion(Note note) {
        return new Note(C_RIGHT_HAND_VALUE + note.getPositionInOctave(), DEFAULT_DURATION);
    }
}
