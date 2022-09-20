package CompanientGenerator;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;

public class FilePlayer {
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

}
