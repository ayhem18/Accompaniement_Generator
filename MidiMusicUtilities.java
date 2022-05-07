import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
class MusicUtilities {
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

    static final String MINOR_CHORD_NAME = "MIN";
    static final String MAJOR_CHORD_NAME = "MAJ";
    static final String MAJOR_7_CHORD_NAME = "MAJ7";
    static final String MINOR_7_CHORD_NAME = "MIN7";

    static final Intervals MINOR_CHORD = new Intervals("1 b3 5");
    static final Intervals MAJOR_CHORD = new Intervals("1 3 5");
    static final Intervals MAJOR_7_CHORD = new Intervals("1 3 5 7");
    static final Intervals MINOR_7_CHORD = new Intervals("1 b3 5 b7");

    static final List<Chord> MAJOR_CHORDS = new ArrayList<>();
    static final List<Chord> MINOR_CHORDS = new ArrayList<>();
    static final List<Chord> MAJOR_7_CHORDS = new ArrayList<>();
    static final List<Chord> MINOR_7_CHORDS = new ArrayList<>();

    static final List<Chord> ALL_CHORDS = new ArrayList<>();
    static {
        for (int i =  C_LEFT_HAND_VALUE; i < C_LEFT_HAND_VALUE + Note.OCTAVE; i++) {
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

    static Chord getRandomChordWithDuration(double duration) {
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

    static Chord getChord(int positionInOctave, Intervals chord, double duration, int inversion) {
        return new Chord(new Note(C_LEFT_HAND_VALUE + positionInOctave, duration), chord).setInversion(inversion);
    }

    static Chord getChord(int positionInOctave, String chordType, double duration, int inversion) {
        return getChord(positionInOctave, Chord.getIntervals(chordType), duration, inversion);
    }

    /**
     * @param note any Note object
     * @return a note object with the default parameters to avoid issues with comparisons between Notes
     */
    static Note commonNoteVersion(Note note) {
        return new Note(C_RIGHT_HAND_VALUE + note.getPositionInOctave(), DEFAULT_DURATION);
    }
}

class KeyParserListener extends ParserListenerAdapter {
    static final List<Intervals> MAJOR_SCALES = new ArrayList<>();
    static final List<Intervals> MINOR_SCALES = new ArrayList<>();

    // static final int DEFAULT_OCTAVE = 5;

    static {
        for (int i = MusicUtilities.C_RIGHT_HAND_VALUE; i < MusicUtilities.C_RIGHT_HAND_VALUE + Note.OCTAVE; i++) {
            MINOR_SCALES.add(new Intervals(MusicUtilities.MINOR_INTERVAL.toString()).setRoot(new Note(i)));
            MAJOR_SCALES.add(new Intervals(MusicUtilities.MAJOR_INTERVAL.toString()).setRoot(new Note(i)));
        }
    }

    final List<Note> notes;

    public KeyParserListener() {
        notes = new ArrayList<>();
    }

    @Override
    public void onNoteParsed(Note note) {
        notes.add(new Note(MusicUtilities.C_RIGHT_HAND_VALUE + note.getPositionInOctave()));
    }

    public Intervals getKey() {

        int bestFit = 0;
        // taking into consideration that a minimum of 2 scales will have the maximum fit indicator
        // (one major indicator and its equivalent in the minor scale)
        List<Intervals> bestFitScales = new ArrayList<>();

        // find the best fit indicator from the major scales
        for (Intervals majorScale : MAJOR_SCALES) {
            bestFit = Math.max(bestFit, findFitCounter(majorScale));
        }

        // find the best fit indicator from the minor scales
        for (Intervals minorScale : MINOR_SCALES) {
            bestFit = Math.max(bestFit, findFitCounter(minorScale));
        }

        // find the most fit major scale
        for (Intervals majorScale : MAJOR_SCALES) {
            if (findFitCounter(majorScale) == bestFit)
                bestFitScales.add(majorScale);
        }

        // find the most fit minor scale
        for (Intervals minorScale : MINOR_SCALES) {
            if (findFitCounter(minorScale) == bestFit)
                bestFitScales.add(minorScale);
        }

        // determine the final key scale out of the best key scales
        // 1: if the music sheets ends or starts with a scale's root
        // this scale is chosen as the key scale
        // 2: in neither of these cases, we choose the scale whose root
        // more present in the music sheet

        // check the last note in the music sheet
        for (Intervals scale : bestFitScales) {
            if (scale.getNotes().get(0).getPositionInOctave() == notes.get(notes.size() - 1).getPositionInOctave()) {
                return scale;
            }
        }

        // the first note in the music sheet
        for (Intervals scale : bestFitScales) {
            if (scale.getNotes().get(0).getPositionInOctave() == notes.get(0).getPositionInOctave()) {
                return scale;
            }
        }

        // choose the scale whose root is more frequent in the music she
        int maxFrequency = 0;
        Intervals bestScale = bestFitScales.get(0);

        for (Intervals scale : bestFitScales) {
            int f ;
            if ((f =findNoteFrequency(scale.getNotes().get(0))) > maxFrequency) {
                maxFrequency = f;
                bestScale = scale;
            }
        }
        return bestScale;
    }
    private int findFitCounter(Intervals scale) {
        List<Note> notesDistinct = notes.stream()
                        .map(Note::getPositionInOctave).distinct()
                .map(position -> new Note(MusicUtilities.C_RIGHT_HAND_VALUE + position)).toList();

        List<Note> scaleNotes = new ArrayList<>(scale.getNotes().stream()
                .map(Note::getPositionInOctave)
                .map(position -> new Note(MusicUtilities.C_RIGHT_HAND_VALUE + position)).toList());

        scaleNotes.retainAll(notesDistinct);
        return scaleNotes.size();
    }

    private int findNoteFrequency(Note note) {
        int counter = 0;
        for (Note n : notes) {
            if (n.getPositionInOctave() == note.getPositionInOctave()) {
                counter ++;
            }
        }
        return counter;
    }

}

class TimeParserListener extends ParserListenerAdapter {
    byte numerator;
    byte denominator;

    @Override
    public void onTimeSignatureParsed (byte numerator, byte denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public void getTheTimeSignature () {
        for (byte d = 1; d <= MusicUtilities.DENOMINATOR_LIMIT; d++) {
            for (byte n = 1; n <= Math.pow(2, d); n++) {
                if (this.numerator != 0 && this.denominator != 0) break;

                onTimeSignatureParsed(d, n);
            }
        }
    }
}

class MeasuresParserListener extends ParserListenerAdapter {
    byte numerator;
    byte denominator;
    // timePerMeasure can be induced from numerator and denominator
    double timePerMeasure;
    private double currentTime;
    List<List<Note>> measures;
    public MeasuresParserListener(byte numerator, byte denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.timePerMeasure = numerator/ (Math.pow(2, denominator));
        this.currentTime = 0.0;
        measures = new ArrayList<>();
    }

    @Override
    public void onNoteParsed(Note note) {
        if (measures.isEmpty() || currentTime >= timePerMeasure) {
            measures.add(new ArrayList<>());
            currentTime = 0;
        }
        double noteDuration = MusicUtilities.fitDuration(note.getDuration());
        currentTime += noteDuration;
        measures.get(measures.size() - 1).add(
                new Note(MusicUtilities.C_RIGHT_HAND_VALUE + note.getPositionInOctave(), noteDuration));
    }

}
class ChordsGenerator {
    Intervals keyScale;
    List<List<Note>> measures;
    double timePerMeasure;
    double timePerChord;

    public ChordsGenerator(Intervals keyScale, List<List<Note>> measures, double timePerMeasure) {
        this.keyScale = keyScale;
        this.measures = measures;
        this.timePerMeasure = timePerMeasure;

        double interMediateTime = this.timePerMeasure * MusicUtilities.QUARTER_TO_WHOLE;
        // if the intermediate timePerMeasure is an even integer
        if (Math.floor(interMediateTime) == interMediateTime && (int) interMediateTime % 2 == 0) {
            timePerChord = timePerMeasure / 2;
        }
        else {
            timePerChord = timePerMeasure;
        }
    }

    public List<List<Note>> generateNotesPerChord() {
        // flatten measures list
        List<Note> notes = measures.stream().flatMap(List::stream).toList();

        double currentTime = 0;
        List<List<Note>> notesPerChord = new ArrayList<>();

        for (Note n: notes) {
            if (currentTime == 0) {
                notesPerChord.add(new ArrayList<>());
            }
            double noteDuration = MusicUtilities.fitDuration(n.getDuration());

            notesPerChord.get(notesPerChord.size() - 1).add(
                    new Note(n).setDuration(Math.min(timePerChord - currentTime, noteDuration)));


            currentTime += noteDuration;

            if (currentTime > timePerChord) {
                currentTime -= timePerChord;
                notesPerChord.add(new ArrayList<>());
                notesPerChord.get(notesPerChord.size() - 1).add(
                        new Note(n).setDuration(currentTime));
            }
            else if (currentTime == timePerChord){
                currentTime = 0;
            }
        }
        return notesPerChord;
    }

    public List<ChordObject> generateRandomChords(Intervals keyScale) {
        List<List<Note>> notesPerChord = generateNotesPerChord();
        List<ChordObject> chords = new ArrayList<>();
        int count = 0;
        // add the first chord explicitly as it does not have a previousNotes attributes
        chords.add(new ChordObject
                (keyScale, null, notesPerChord.get(0), notesPerChord.get(1), timePerChord, ++count));
        int i;

        for (i = 1; i < notesPerChord.size() - 1 ; i++) {
            chords.add(new ChordObject(keyScale,
                            notesPerChord.get(i - 1), notesPerChord.get(i), notesPerChord.get(i + 1),
                    timePerChord, ++count));
        }
        // i at this point is equal to notesPerChord.size() - 1
        // add the last chord explicitly as it does not have a nextNotes attribute
        chords.add(
                new ChordObject(
                        keyScale, notesPerChord.get(i - 1), notesPerChord.get(i), null, timePerChord, ++count));

        return chords;
    }
}