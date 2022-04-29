import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import java.util.List;

class ChordObject {
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
        this.chordDuration = chordDuration;
        this.actualChord = actualChord;
    }

    public ChordObject(List<Note> notesPrevious, List<Note> notesCurrent, List<Note> notesNext, double chordDuration) {
        this(notesPrevious, notesCurrent, notesNext, chordDuration,
                MusicUtilities.getRandomChordWithDuration(chordDuration));
    }
}