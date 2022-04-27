import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;

class KeyParserDemo extends ParserListenerAdapter {
    private final List<String> notes;
    private final List<Note> n;
    int counter;
    public KeyParserDemo() {
        notes = new ArrayList<>();
        n = new ArrayList<>();
        counter = 0;
    }

    @Override
    public void onNoteParsed(Note note) {
        notes.add(note.getToneString());
        n.add(note);
    }

    public void displayNotes() {
        System.out.println(notes);
        System.out.println(notes.size());
        System.out.println("####################");
        System.out.println(n.size());
    }
}

class KeyParser extends ParserListenerAdapter {
    static final Intervals MINOR_INTERVAL = new Intervals("1 2 b3 4 5 b6 b7");
    static final Intervals MAJOR_INTERVAL = new Intervals("1 2 3 4 5 6 7");
    static final List<Intervals> MAJOR_SCALES = new ArrayList<>();
    static final List<Intervals> MINOR_SCALES = new ArrayList<>();

    static final int C_INTEGER = 60;
    static final int DEFAULT_OCTAVE = 5;
    static {
        for (int i = C_INTEGER; i < C_INTEGER + Note.OCTAVE; i++) {
            MINOR_SCALES.add(new Intervals(MINOR_INTERVAL.toString()).setRoot(new Note(i)));
            MAJOR_SCALES.add(new Intervals(MAJOR_INTERVAL.toString()).setRoot(new Note(i)));
        }
    }

    private final List<Note> notes;

    public KeyParser() {
        notes = new ArrayList<>();
    }

    @Override
    public void onNoteParsed(Note note) {
        notes.add(new Note(note.getPositionInOctave() % Note.OCTAVE));
    }

    public Intervals getKey() {
//                .sorted((note1, note2) -> Integer.compare(note1.getPositionInOctave(), note2.getPositionInOctave()))
//                .
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
        List<Note> notesDistinct = notes.stream().distinct().toList();
        int fitCounter = 0;
        for (Note note : notesDistinct) {
            if (scale.getNotes().contains(note)) {
                fitCounter ++;
            }
        }
        return fitCounter;
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