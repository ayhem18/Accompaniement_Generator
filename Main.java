import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static double WANTED_AVERAGE_FITNESS = 8;

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        String file = "src/testFiles/input1.mid";
        for (int i = 0; i < 5; i ++) {
            Pattern chords = generateAccompaniment(file);
            System.out.println(chords);
            displayMidiFile(file, chords, i);
        }
         // displayMidiFile("src/testFiles/input1.+chords4.mid");
//        Pattern mainChords = new Pattern("T180 V0 D4Min9hqit Ri G3Maj13hqi Ri C4Maj9wh Rh");
//        mainChords.add("D4Minhqit Ri G4Majhqi Ri C4Majwh Rht");
//
//        Pattern pianoTouch = new Pattern("T180 V1 Rw | Rw | Rhi | G4qi G3s A3is CMajis ri");
//        pianoTouch.add("Rw | Rw | Rhi | G4s C5wa100d0");
//
//        (new Player()).play(mainChords, pianoTouch);
    }

    public static Pattern generateAccompaniment(String filePath) throws InvalidMidiDataException, IOException {

        MidiParser parser = new MidiParser();
        // determine the key scale of the song
        KeyParserListener keyListener = new KeyParserListener();
        parser.addParserListener(keyListener);

        TimeParserListener timeListener = new TimeParserListener();
        parser.addParserListener(timeListener);

        // parse first time to determine the key and time signatures
        parser.parse(MidiSystem.getSequence(
                new File(filePath)));

        timeListener.getTheTimeSignature();
        // set the measures listener
        MeasuresParserListener measuresListener = new MeasuresParserListener(timeListener.numerator,
                timeListener.denominator);
        // add the measures' listener to the parser
        parser.addParserListener(measuresListener);

        // parse a second time to determine the notes in each measure
        parser.parse(MidiSystem.getSequence(
                new File(filePath)));

        ChordsGenerator generator = new ChordsGenerator(keyListener.getKeyScale(), measuresListener.measures,
                measuresListener.timePerMeasure);

        List<ChordObject> randomChords = generator.generateRandomChords(keyListener.getKeyScale());
        randomChords.removeIf(ChordObject::isRestChord);
        List<ChordObject> restChords = new ArrayList<>(randomChords.stream().filter(ChordObject::isRestChord).toList());

        Evolution evolution = new Evolution(generator.generateRandomChords(keyListener.getKeyScale()), WANTED_AVERAGE_FITNESS);
        evolution.simulateEvolution();

        restChords.addAll(evolution.initialGeneration);

        restChords.sort((c1, c2) -> (int) Math.signum(c1.chordRank - c2.chordRank));

        Pattern accompaniment = new Pattern("V1").setInstrument("flute").setVoice(13);
        System.out.println("ACCOMPANIMENT: " + accompaniment.toString());
        for (ChordObject chord : evolution.initialGeneration) {
            if (chord.isRestChord) {
                accompaniment.add(chord.rest);
            }
            else {
                accompaniment.add(chord.actualChord);}
        }
        accompaniment.setInstrument(10);
        return accompaniment;
    }

    public static void displayMidiFile(String filePath) {
        Pattern loadedFile = new Pattern();
        try {
            loadedFile = MidiFileManager.loadPatternFromMidi(new File(filePath));
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(loadedFile);
        new Player().play(loadedFile);
    }

    public static void displayMidiFile(String filePath, Pattern accompaniment, int i) {
        Pattern loadedFile = new Pattern();
        try {
            loadedFile = MidiFileManager.loadPatternFromMidi(new File(filePath));
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(loadedFile);
        loadedFile.setInstrument(1);
        loadedFile.add(accompaniment);

        try {
            File file = new File(filePath.substring(0, filePath.length() - 3)
                    + "+chords" + i + ".mid");
            MidiFileManager.savePatternToMidi(loadedFile, file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Player().play(loadedFile);
    }

    public static void testKeyParser() throws InvalidMidiDataException, IOException {

        Pattern loadedFile = new Pattern();
        try {
            File filePath = new File("src/testFiles/you are only lonely L.mid");
            loadedFile = MidiFileManager.loadPatternFromMidi(filePath);
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(loadedFile);


        MidiParser parser = new MidiParser(); // Remember, you can use any Parser!
        KeyParserListener keyListener = new KeyParserListener();
        parser.addParserListener(keyListener);

        TimeParserListener timeListener = new TimeParserListener();
        parser.addParserListener(timeListener);

        // parse first time to determine the key and time signatures
        parser.parse(MidiSystem.getSequence(
                new File("src/testFiles/you are only lonely L.mid")));


        timeListener.getTheTimeSignature();

        // set the measures listener
        MeasuresParserListener measuresListener = new MeasuresParserListener(timeListener.numerator,
                timeListener.denominator);
        // add the measures' listener to the parser
        parser.addParserListener(measuresListener);

        // parse a second time to determine the notes in each measure
        parser.parse(MidiSystem.getSequence(
                new File("src/testFiles/you are only lonely L.mid")));

        ChordsGenerator generator = new ChordsGenerator(keyListener.getKeyScale(), measuresListener.measures,
                measuresListener.timePerMeasure);

        generator.generateNotesPerChord().forEach(System.out::println);
    }
}

