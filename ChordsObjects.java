import org.jfugue.theory.Chord;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;

import java.util.Arrays;
import java.util.List;

class ChordObject {
    static double POINTS_FOR_KEY_SCALE = 3.0;
    static double POINTS_FOR_CURRENT_CHORD_UNIT = 3.0;
    static double POINTS_FOR_DIFFERENT_CHORD_UNIT = 1.5;
    static double POINTS_FOR_ROOT_NOTE = 1.0;

    List<Note> notesPrevious;
    List<Note> notesCurrent;
    List<Note> notesNext;
    double chordDuration;
    Chord actualChord;

    public ChordObject(List<Note> notesPrevious, List<Note> notesCurrent, List<Note> notesNext,
                       double chordDuration, Chord actualChord) {
        this.notesPrevious = notesPrevious;
        this.notesCurrent = notesCurrent;
        this.notesNext = notesNext;
        this.chordDuration = chordDuration / MusicUtilities.QUARTER_TO_WHOLE;
        this.actualChord = actualChord;
    }

    public ChordObject(List<Note> notesPrevious, List<Note> notesCurrent, List<Note> notesNext, double chordDuration) {
        this(notesPrevious, notesCurrent, notesNext, chordDuration,
                MusicUtilities.getRandomChordWithDuration(chordDuration));
    }
    private List<Note> chordNotes() {
        return Arrays.stream(actualChord.getNotes()).map(MusicUtilities::commonNoteVersion).toList();
    }


    /**
     * @return a numerical value reflecting how musically appealing (from a theoretical point of view) the chord is
     */
    public double fitnessFunction(Intervals keyScale) {
        return pointsForKeyScale(keyScale, POINTS_FOR_KEY_SCALE) +
                pointsForChordUnit(notesCurrent, POINTS_FOR_CURRENT_CHORD_UNIT) +
                pointsForChordUnit(notesNext, POINTS_FOR_DIFFERENT_CHORD_UNIT) +
                pointsForChordUnit(notesPrevious, POINTS_FOR_DIFFERENT_CHORD_UNIT) +
                pointsForRootNote(notesCurrent, POINTS_FOR_ROOT_NOTE);
    }

    private double pointsForKeyScale(Intervals keyScale, double coefficient) {
        List<Note> keyScaleNotes = keyScale.getNotes().stream().map(MusicUtilities::commonNoteVersion).toList();
        int count = 0;
        int total = 0;
        for (Note n : chordNotes()) {
            total ++;
            if (keyScaleNotes.contains(n)) count++;
        }
        return  (count * coefficient) / total ;
    }

    private double pointsForChordUnit(List<Note> chordUnit, double coefficient) {
        if (chordUnit == null) {
            return 0.5 * coefficient;
        }

        List<Note> chordUnitNotes = chordUnit.stream().map(MusicUtilities::commonNoteVersion).toList();
        int count = 0; int total = 0;
        for (Note n: chordUnitNotes) {
            total ++;
            if (chordNotes().contains(n))
                count++;
        }
        return (count * coefficient) / total;
    }

    private double pointsForRootNote(List<Note> chordUnit, double coefficient) {
        Note firstNote = MusicUtilities.commonNoteVersion(chordUnit.get(0));
        return coefficient * 0.5 * (chordNotes().contains(firstNote)? 1.0 : 0.0) +
        coefficient * 0.5 * (chordNotes().get(0).equals(firstNote)? 1.0: 0.0);
    }

}