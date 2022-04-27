import org.jfugue.midi.MidiParser;
import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        testKeyParser();
    }

    public static void testKeyParser() throws InvalidMidiDataException, IOException {

        MidiParser parser = new MidiParser(); // Remember, you can use any Parser!
        KeyParser keyListener = new KeyParser();
        parser.addParserListener(keyListener);

        MeasuresParser measureListener = new MeasuresParser();
        parser.addParserListener(measureListener);


        measureListener.getTheTimeSignature();
        System.out.println(measureListener.numerator);
        System.out.println(measureListener.denominator);

    }
}

