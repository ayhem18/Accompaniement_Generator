package AccompanimentGenerator;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;

public class FilePlayer {
    public static void displayMidiFile(String filePath) {
        System.out.println("Now the project will process the midi file saved at: \n" + filePath);
        Pattern loadedFile = new Pattern();
        try {
            loadedFile = MidiFileManager.loadPatternFromMidi(new File(filePath));
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("The file can be musically written as:");
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

    /**
     * @param filePath: the file with the original melody
     * @param accompaniment the chords stored in a pattern object
     * @param newFileName the new file name where the combination will be saved
     */
    public static void displayMidiFile(String filePath, Pattern accompaniment, String newFileName) {
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
            File file = new File(newFileName);
            MidiFileManager.savePatternToMidi(loadedFile, file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Player().play(loadedFile);
    }

}
