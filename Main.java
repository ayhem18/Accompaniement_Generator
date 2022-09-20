import Chords.ChordObject;
import Chords.Evolution;
import CompanientGenerator.FilePlayer;
import CompanientGenerator.Generator;
import MidiMusicUtilities.ChordsGenerator;
import MidiMusicUtilities.KeyParserListener;
import MidiMusicUtilities.MeasuresParserListener;
import MidiMusicUtilities.TimeParserListener;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        String file = "src/testFiles/input1.mid";
        FilePlayer.displayMidiFile(file);
        Pattern chords = Generator.generateAccompaniment(file);
        System.out.println(chords);
        FilePlayer.displayMidiFile(file, chords);
    }

}

