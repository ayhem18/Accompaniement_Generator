import org.jfugue.theory.Chord;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public ChordObject(ChordObject another) {
        this(another.keyScale, another.notesPrevious, another.notesCurrent, another.notesNext, another.chordDuration,
                another.actualChord);
    }
    private List<Note> chordNotes() {
        return Arrays.stream(actualChord.getNotes()).map(MusicUtilities::commonNoteVersion).toList();
    }

    public void setActualChord(Chord actualChord) {
        this.actualChord = actualChord;
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

    /**
     * @param chord1 first chord
     * @param chord2 second chord
     * @return a new chord formed randomly out of the two passed chords
     */
    private static Chord crossOver(Chord chord1, Chord chord2, double duration) {
        Random generator = new Random();
        double choice = generator.nextDouble();
        // determine the chord type
        Intervals chordType;
        if (choice < 0.45) {
            chordType = chord1.getIntervals();
        }
        else if (choice < 0.9) {
            chordType = chord2.getIntervals();
        }
        else {
            chordType = Arrays.asList(MusicUtilities.MAJOR_CHORD, MusicUtilities.MINOR_7_CHORD,
                    MusicUtilities.MAJOR_7_CHORD, MusicUtilities.MINOR_7_CHORD).get(generator.nextInt(4));
        }
        // determine the root note
        int positionInOctave;
        choice = generator.nextDouble();

        if (choice < 0.45) {
            positionInOctave = chord1.getRoot().getPositionInOctave();
        }
        else if (choice < 0.9) {
            positionInOctave = chord2.getRoot().getPositionInOctave();
        }
        else {
            positionInOctave = generator.nextInt(Note.OCTAVE);
        }

        // determine the inversion
        choice = generator.nextDouble();
        int inversion;

        if (choice < 0.45) {
            inversion = chord1.getInversion();
        }
        else if (choice < 0.9) {
            inversion = chord2.getInversion();
        }
        else {
            inversion = generator.nextInt(chordType.size());
        }
        return MusicUtilities.getChord(positionInOctave, chordType, duration, inversion);
    }
    static List<ChordObject> crossOver(ChordObject chord1, ChordObject chord2) {
        assert chord1.chordDuration == chord2.chordDuration;
        List<ChordObject> newPair = new ArrayList<>();
        newPair.add(new ChordObject(chord1));
        newPair.add(new ChordObject(chord2));
        newPair.get(0).setActualChord(crossOver(chord1.actualChord, chord2.actualChord, chord1.chordDuration));
        newPair.get(1).setActualChord(crossOver(chord1.actualChord, chord2.actualChord, chord1.chordDuration));
        return newPair;
    }

//    static ChordObject crossOver(ChordObject chord1, ChordObject chord2) {
//
//        assert chord1.chordDuration == chord2.chordDuration;
//
//        Random random = new Random();
//        // an array of chordObject to facilitate the randomization
//        ChordObject[] chords = new ChordObject[] {chord1, chord2};
//        Chord newChord =
//                MusicUtilities.getChord(chords[random.nextInt(2)].actualChord.getRoot().getPositionInOctave(),
//                        chords[random.nextInt(2)].actualChord.getChordType(),
//                        chord2.chordDuration,
//                        chords[random.nextInt(2)].actualChord.getInversion());
//        ChordObject worseChord = chord1.fitnessValue > chord2.fitnessValue ? chord2 : chord1;
//        return new ChordObject(worseChord.keyScale, worseChord.notesPrevious, worseChord.notesCurrent, worseChord.notesNext,
//                worseChord.chordDuration, newChord);
//    }

//    static Chord crossOver(Chord chord1, Chord chord2, double duration) {
//        Random random = new Random();
//        // an array of chordObject to facilitate the randomization
//        Chord[] chords = new Chord[] {chord1, chord2};
//        return MusicUtilities.getChord(chords[random.nextInt(2)].getRoot().getPositionInOctave(),
//                        chords[random.nextInt(2)].getChordType(),
//                        duration,
//                        chords[random.nextInt(2)].getInversion());
//    }

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
        chord.actualChord.setInversion(newInversion % chord.actualChord.getIntervals().size());
    }
}

class Evolution {
    static double CHORD_TYPE_MUTATION_FREQUENCY = 0.05;
    static double INVERSION_MUTATION_FREQUENCY = 0.1;
    static double ELITE_PERCENTAGE = 0.1;
    List<ChordObject> initialGeneration ;
    double wantedAverageFitness;
    Random generator;
    public Evolution(List<ChordObject> initialGeneration, double wantedAverageFitness) {
        this.initialGeneration = initialGeneration;
        this.wantedAverageFitness = wantedAverageFitness;
        generator = new Random();
    }

    private void performElitism(List<ChordObject> oldGeneration, List<ChordObject> newGeneration) {
        int size = oldGeneration.size();
        oldGeneration.sort((chord1, chord2) -> (int) Math.signum(chord1.fitnessFunction() - chord2.fitnessFunction()));
        for (int i = 0 ; i < ELITE_PERCENTAGE * size ; i++) {
            // remove the elite elements from the oldGeneration to the new Ones
            newGeneration.add(oldGeneration.remove(oldGeneration.size() - 1));
        }
    }
    private void crossOver(List<ChordObject> oldGeneration, List<ChordObject> newGeneration) {
        while(oldGeneration.size() >= 2) {
            ChordObject parent1 = oldGeneration.remove(generator.nextInt(oldGeneration.size()));
            ChordObject parent2 = oldGeneration.remove(generator.nextInt(oldGeneration.size()));
            List<ChordObject> offsprings = ChordsReproduction.crossOver(parent1, parent2);

            // decide whether to mutate the first offspring
            double mutationDeterminative = generator.nextDouble();
            if (mutationDeterminative < INVERSION_MUTATION_FREQUENCY) {
                ChordsReproduction.mutateChordInversion(offsprings.get(0));
            }

            if (mutationDeterminative < CHORD_TYPE_MUTATION_FREQUENCY) {
                ChordsReproduction.mutateChordType(offsprings.get(0));
            }

            // decide whether to mutate the second offspring
            mutationDeterminative = generator.nextDouble();

            if (mutationDeterminative < INVERSION_MUTATION_FREQUENCY) {
                ChordsReproduction.mutateChordInversion(offsprings.get(1));
            }

            if (mutationDeterminative < CHORD_TYPE_MUTATION_FREQUENCY) {
                ChordsReproduction.mutateChordType(offsprings.get(1));
            }
            // add the two offsprings to the new generation
            newGeneration.addAll(offsprings);
        }
        // in case one element is left in the oldGeneration list
        newGeneration.addAll(oldGeneration);
    }
    private double evaluateTotalFitness() {
        return initialGeneration.stream().mapToDouble(ChordObject::fitnessFunction).sum();
    }

    public void simulateEvolution() {
        double totalFitness = 0;
        int populationSize = initialGeneration.size();

        while (totalFitness < populationSize * wantedAverageFitness) {
            List<ChordObject> newGeneration = new ArrayList<>();
            // pass the best parents directly to the next generation
            performElitism(initialGeneration, newGeneration);
            // perform crossOver to (possibly) obtain better offspring
            crossOver(initialGeneration, newGeneration);
            // set the corresponding variables
            initialGeneration = newGeneration;
            // evaluate the total performance
            totalFitness = evaluateTotalFitness();
        }
    }
}



