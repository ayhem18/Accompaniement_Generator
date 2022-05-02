import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

public class Main {

    static double WANTED_AVERAGE_FITNESS = 6;

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        analyzeMidiFile("src/testFiles/input1.mid");
    }

    public static void analyzeMidiFile(String filePath) throws InvalidMidiDataException, IOException {
//        Pattern loadedFile = new Pattern();
//        try {
//            loadedFile = MidiFileManager.loadPatternFromMidi(new File(filePath));
//        } catch (InvalidMidiDataException | IOException e) {
//            e.printStackTrace();
//        }

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

