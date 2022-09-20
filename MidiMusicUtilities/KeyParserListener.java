package MidiMusicUtilities;

import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;

public class KeyParserListener extends ParserListenerAdapter {
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
