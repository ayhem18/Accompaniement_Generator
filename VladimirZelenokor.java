import javax.sound.midi.*;
import java.util.*;
import java.io.File;
import java.io.*;
import org.jfugue.player.Player;
import org.jfugue.pattern.Pattern;
import org.jfugue.midi.MidiFileManager;

public class VladimirZelenokor {

    ArrayList<Pattern> initPopul = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        Pattern loadedFile = new Pattern("V0");
        try {
            File filePath = new File("src/barbiegirl.mid");
            loadedFile = MidiFileManager.loadPatternFromMidi(filePath);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        Player player = new Player();

        String input = loadedFile.toString();
        System.out.println(input);

//        String tempo = "";
//
//        for(int i = 0; i < input.length(); i++) {
//            if (input.charAt(i) != ' ') {
//                tempo = tempo + input.charAt(i);
//            } else { break; }
//        }
//
//        Pattern mainChords = new Pattern(tempo + " V1 G#2Min3w F#2Min3w E2Min3w F#2Min3w");
//
//        player.play(loadedFile.repeat(2), mainChords.repeat(2));

    }

    public static void generInitialPopulation() {
        for(int i = 0; i < 100; i++) {

        }
    }
}
