import org.jfugue.theory.Chord;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;

import java.util.*;

class ChordObject {
    static double POINTS_FOR_KEY_SCALE = 3.0;
    static double POINTS_FOR_CURRENT_CHORD_UNIT = 3.0;
    static double POINTS_FOR_DIFFERENT_CHORD_UNIT = 1.5;
    static double POINTS_FOR_ROOT_NOTE = 1.0;
    Intervals keyScale;
    List<Note> notesPrevious;
    List<Note> notesCurrent;
    List<Note> notesNext;
    double chordDuration;
    Chord actualChord;

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
                MusicUtilities.getRandomChordWithDuration(chordDuration / MusicUtilities.QUARTER_TO_WHOLE));
    }
    private List<Note> chordNotes() {
        return Arrays.stream(actualChord.getNotes()).map(MusicUtilities::commonNoteVersion).toList();
    }


    /**
     * @return a numerical value reflecting how musically appealing (from a theoretical point of view) the chord is
     */
    public double fitnessFunction() {
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
        coefficient * 0.5 * (chordNotes().get(0).equals(firstNote)? 1.0 : 0.0);
    }
}

class ChordsReproduction {
//    static List<ChordObject> crossOver(ChordObject chord1, ChordObject chord2) {
//        Chord chordA = MusicUtilities.getChord(
//                chord1.actualChord.getRoot().getPositionInOctave(),
//                chord2.actualChord.getChordType(), chord2.chordDuration, chord1.actualChord.getInversion());
//
//        Chord chordB = MusicUtilities.getChord(
//                chord2.actualChord.getRoot().getPositionInOctave(),
//                chord1.actualChord.getChordType(), chord1.chordDuration, chord2.actualChord.getInversion());
//
//        ChordObject chordObject1 =
//                new ChordObject
//                        (chord1.notesPrevious, chord1.notesCurrent, chord1.notesNext, chord1.chordDuration, chordA);
//
//        ChordObject chordObject2 =
//                new ChordObject
//                        (chord2.notesPrevious, chord2.notesCurrent, chord2.notesNext, chord2.chordDuration, chordB);
//
//        return Arrays.asList(chordObject1, chordObject2);
//    }

    static ChordObject crossOver(ChordObject chord1, ChordObject chord2) {
        Random random = new Random();
        ChordObject[] chords = new ChordObject[] {chord1, chord2};
        Chord newChord =
                MusicUtilities.getChord(chords[random.nextInt(2)].actualChord.getRoot().getPositionInOctave(),
                        chords[random.nextInt(2)].actualChord.getChordType(),
                        chord2.chordDuration,
                        chords[random.nextInt(2)].actualChord.getInversion());
        ChordObject worseChord = chord1.fitnessFunction() > chord2.fitnessFunction() ? chord2 : chord1;
        return new ChordObject(worseChord.keyScale, worseChord.notesPrevious, worseChord.notesCurrent, worseChord.notesNext,
                worseChord.chordDuration, newChord);
    }

    static void mutateChordType(ChordObject chord) {
        if (chord.actualChord.isMinor()) {
            chord.actualChord = MusicUtilities.getChord(chord.actualChord.getRoot().getPositionInOctave(),
                    MusicUtilities.MINOR_7_CHORD,
                    chord.chordDuration / MusicUtilities.QUARTER_TO_WHOLE,
                    chord.actualChord.getInversion());
        }
        else if (chord.actualChord.isMajor()) {
            chord.actualChord = MusicUtilities.getChord(chord.actualChord.getRoot().getPositionInOctave(),
                    MusicUtilities.MAJOR_7_CHORD,
                    chord.chordDuration / MusicUtilities.QUARTER_TO_WHOLE,
                    chord.actualChord.getInversion());
        }
        else if (chord.actualChord.getChordType().equalsIgnoreCase(MusicUtilities.MAJOR_7_CHORD_NAME)) {
            chord.actualChord = MusicUtilities.getChord(chord.actualChord.getRoot().getPositionInOctave(),
                    MusicUtilities.MAJOR_CHORD,
                    chord.chordDuration / MusicUtilities.QUARTER_TO_WHOLE,
                    chord.actualChord.getInversion());
        }
        else {
            chord.actualChord = MusicUtilities.getChord(chord.actualChord.getRoot().getPositionInOctave(),
                    MusicUtilities.MINOR_CHORD,
                    chord.chordDuration / MusicUtilities.QUARTER_TO_WHOLE,
                    chord.actualChord.getInversion());
        }
    }

    static void mutateChordInversion(ChordObject chord) {
        int inversion = chord.actualChord.getInversion();
        chord.actualChord.setInversion(inversion + (int) Math.ceil(Math.random() * 2));

    }
}

class Evolution {
    static double CHORD_TYPE_MUTATION_FREQUENCY = 0.05;
    static double INVERSION_MUTATION_FREQUENCY = 0.15;
    static double ELITE_PERCENTAGE = 0.1;
    List<ChordObject> initialGeneration ;
    double wantedAverageFitness;
    Random generator;
    public Evolution(List<ChordObject> initialGeneration, double wantedAverageFitness) {
        this.initialGeneration = initialGeneration;
        this.wantedAverageFitness = wantedAverageFitness;
        generator = new Random();
    }

    private void crossOver(List<ChordObject> oldGeneration, List<ChordObject> newGeneration) {
        while(oldGeneration.size() >= 2) {
            ChordObject parent1 = oldGeneration.remove(generator.nextInt(oldGeneration.size()));
            ChordObject parent2 = oldGeneration.remove(generator.nextInt(oldGeneration.size()));
            ChordObject betterParent = parent1.fitnessFunction() > parent2.fitnessFunction() ? parent1 : parent2;
            ChordObject offspring = ChordsReproduction.crossOver(parent1, parent2);

            double mutationDeterminative = generator.nextDouble();
            if (mutationDeterminative < INVERSION_MUTATION_FREQUENCY) {
                ChordsReproduction.mutateChordInversion(offspring);
            }

            if (mutationDeterminative < CHORD_TYPE_MUTATION_FREQUENCY) {
                ChordsReproduction.mutateChordType(offspring);
            }
            newGeneration.add(betterParent);
            newGeneration.add(offspring);
        }
        // in case one element is left in the oldGeneration list
        newGeneration.addAll(oldGeneration);
    }

    private void performElitism(List<ChordObject> oldGeneration, List<ChordObject> newGeneration) {
        int size = oldGeneration.size();
        oldGeneration.sort((chord1, chord2) -> (int) Math.signum(chord1.fitnessFunction() - chord2.fitnessFunction()));
        for (int i = 0 ; i < ELITE_PERCENTAGE * size ; i++) {
            // remove the elite elements from the oldGeneration to the new Ones
            newGeneration.add(oldGeneration.remove(oldGeneration.size() - 1));
        }
    }

    public void simulateEvolution() {
        double totalFitness = 0;
        int populationSize = initialGeneration.size();

        while (totalFitness < populationSize * wantedAverageFitness) {
            List<ChordObject> newGeneration = new ArrayList<>();
            performElitism(initialGeneration, newGeneration);
            crossOver(initialGeneration, newGeneration);
        }
    }

}



