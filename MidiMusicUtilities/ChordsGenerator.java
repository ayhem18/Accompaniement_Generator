package MidiMusicUtilities;

import Chords.ChordObject;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;

public class ChordsGenerator {
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

        // add the first chord explicitly as it does not have a previousNotes attributes
        chords.add(new ChordObject
                (keyScale, null, notesPerChord.get(0), notesPerChord.get(1), timePerChord));
        int i;

        for (i = 1; i < notesPerChord.size() - 1 ; i++) {
            chords.add(new ChordObject(keyScale,
                            notesPerChord.get(i - 1), notesPerChord.get(i), notesPerChord.get(i + 1), timePerChord));
        }
        // i at this point is equal to notesPerChord.size() - 1
        // add the last chord explicitly as it does not have a nextNotes attribute
        chords.add(
                new ChordObject(keyScale, notesPerChord.get(i - 1), notesPerChord.get(i), null, timePerChord));

//        for (List<Note> ln : notesPerChord) {
//            System.out.println(ln.stream().mapToDouble(Note::getDuration).sum());
//        }
        return chords;
    }
}