package Chords;

import MidiMusicUtilities.MusicUtilities;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChordObject {
    static double POINTS_FOR_KEY_SCALE = 3.0;
    static double POINTS_FOR_CURRENT_CHORD_UNIT = 3.0;
    static double POINTS_FOR_DIFFERENT_CHORD_UNIT = 1.5;
    static double POINTS_FOR_ROOT_NOTE = 1.0;
    Intervals keyScale;
    List<Note> notesPrevious;
    List<Note> notesCurrent;
    List<Note> notesNext;
    double chordDuration;
    public Chord actualChord;

    double fitnessValue;
    public ChordObject(Intervals keyScale, List<Note> notesPrevious, List<Note> notesCurrent, List<Note> notesNext,
                       double chordDuration, Chord actualChord) {
        this.keyScale = keyScale;
        this.notesPrevious = notesPrevious;
        this.notesCurrent = notesCurrent;
        this.notesNext = notesNext;
        this.chordDuration = chordDuration ;
        this.actualChord = actualChord;
    }

    public ChordObject(Intervals keyScale, List<Note> notesPrevious, List<Note> notesCurrent, List<Note> notesNext,
                       double chordDuration) {
        this(keyScale, notesPrevious, notesCurrent, notesNext, chordDuration,
                MusicUtilities.getRandomChordWithDuration(chordDuration));
    }
    private List<Note> chordNotes() {
        return Arrays.stream(actualChord.getNotes()).map(MusicUtilities::commonNoteVersion).toList();
    }

    /**
     * @return a numerical value reflecting how musically appealing (from a theoretical point of view) the chord is
     */
    public double fitnessFunction() {
        fitnessValue = Math.round(pointsForKeyScale(keyScale, POINTS_FOR_KEY_SCALE) +
                pointsForChordUnit(notesCurrent, POINTS_FOR_CURRENT_CHORD_UNIT) +
                pointsForChordUnit(notesNext, POINTS_FOR_DIFFERENT_CHORD_UNIT) +
                pointsForChordUnit(notesPrevious, POINTS_FOR_DIFFERENT_CHORD_UNIT) +
                pointsForRootNote(notesCurrent, POINTS_FOR_ROOT_NOTE));
        fitnessValue = (new BigDecimal(fitnessValue)).setScale(3, RoundingMode.HALF_UP).doubleValue();
        return fitnessValue * 1;

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
        List<Double> values = chordUnit.stream().mapToDouble(Note::getDuration).boxed().toList();
        double minDuration = chordUnit.stream().mapToDouble(Note::getDuration).min().orElse(0.25);

        // List<Note> chordUnitNotes = chordUnit.stream().map(MusicUtilities::commonNoteVersion).toList();
        List<Note> chordUnitNotes = chordUnit.stream().map((note) -> {
            List<Note> list = new ArrayList<>();
            for (int i = 0; i < note.getDuration() / minDuration; i++) {
                list.add(MusicUtilities.commonNoteVersion(note));
            }
            return list;
        }).flatMap(List::stream).toList();

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
        boolean contains = chordNotes().contains(firstNote);//? 1.0 : 0.0);
        boolean firstEqual = chordNotes().get(0).equals(firstNote);
        return coefficient * 0.5 * (chordNotes().contains(firstNote)? 1.0 : 0.0) +
        coefficient * 0.5 * (chordNotes().get(0).equals(firstNote)? 1.0 : 0.0);
    }
}