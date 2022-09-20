package AccompanimentGenerator;

import Chords.ChordObject;
import Chords.Evolution;
import MidiMusicUtilities.ChordsGenerator;
import MidiMusicUtilities.KeyParserListener;
import MidiMusicUtilities.MeasuresParserListener;
import MidiMusicUtilities.TimeParserListener;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

public class Generator {
    // This value has been tuned mainly by experimenting: finding the tradeoff between the computation cost and
    // quality of chords generated. The quality here is determined subjectively by ear.
    static final double WANTED_AVERAGE_FITNESS = 8.2;

    public static Pattern generateAccompaniment(String filePath) throws InvalidMidiDataException, IOException {
        System.out.println(
                "The evolution process is taking place!! We are generating fit chords for your piece of music !!");

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

}
