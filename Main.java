import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        testKeyParser();
    }

    public static void testKeyParser() throws InvalidMidiDataException, IOException {
        Pattern loadedFile = new Pattern();
        try {
            File filePath = new File("src/testFiles/barbie_girl.mid");
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
                new File("src/testFiles/barbie_girl.mid")));


        timeListener.getTheTimeSignature();
        System.out.println(timeListener.numerator);
        System.out.println(timeListener.denominator);

        // set the measures listener
        MeasuresParserListener measuresListener = new MeasuresParserListener(timeListener.numerator,
                timeListener.denominator);
        // add the measures' listener to the parser
        parser.addParserListener(measuresListener);

        // parse a second time to determine the notes in each measure
        parser.parse(MidiSystem.getSequence(
                new File("src/testFiles/barbie_girl.mid")));

        // display the results
        measuresListener.measures.forEach(measure -> {
            System.out.println(measure);
            System.out.println();
        });

    }
}

