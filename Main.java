import org.jfugue.midi.MidiParser;
import org.jfugue.theory.Note;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        testKeyParser();
    }

    public static void testKeyParser() throws InvalidMidiDataException, IOException {
        MidiParser parser = new MidiParser(); // Remember, you can use any Parser!
        KeyParser listener = new KeyParser();
        parser.addParserListener(listener);
        parser.parse(MidiSystem.getSequence(
                new File("src/testFiles/Pirates of the Caribbean - He's a Pirate (1).mid")));

    }
}

