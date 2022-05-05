import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

public class Main {

    static double WANTED_AVERAGE_FITNESS = 7.0;

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        String file = "src/testFiles/input1.mid";
        displayMidiFile(file);
        Pattern chords = generateAccompaniment(file);
        System.out.println(chords);
        displayMidiFile(file, chords);

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

        ChordsGenerator generator = new ChordsGenerator(keyListener.getKey(), measuresListener.measures,
                measuresListener.timePerMeasure);

        Evolution evolution = new Evolution(
                generator.generateRandomChords(keyListener.getKey()), WANTED_AVERAGE_FITNESS);

        evolution.simulateEvolution();

        Pattern accompaniment = new Pattern("V1").setInstrument("Steel_String_Guitar");
        for (ChordObject chord : evolution.initialGeneration) {
            accompaniment.add(chord.actualChord);
        }

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

    public static void displayMidiFile(String filePath, Pattern accompaniment) {
        Pattern loadedFile = new Pattern();
        try {
            loadedFile = MidiFileManager.loadPatternFromMidi(new File(filePath));
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(loadedFile);
        new Player().play(loadedFile, accompaniment);
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

        ChordsGenerator generator = new ChordsGenerator(keyListener.getKey(), measuresListener.measures,
                measuresListener.timePerMeasure);

        generator.generateNotesPerChord().forEach(System.out::println);
    }
}

