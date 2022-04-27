import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Temp {
    static List<String> CMajor = Arrays.asList("C4q", "D4q", "E4q", "F4q", "G4q", "A4q", "B4q", "C5q");

    public static void test2() {
        Player player = new Player();
        Pattern vocals = new Pattern();
        vocals.add("Rh G5is E5i Ri | G5s Ris E5q Rs | G5q E5i Rs D5q rs C5h Rs");
        vocals.add("C4i A5q G5isa50d0 Rs A5s E5i D5is Rs C5qis");
        vocals.add("Rqi A4s G5i E5i Rs | G5is Rs E5q | D5is C5i Rs C5q G4q Ri");
        vocals.add("G3is A3s C4is D4s C4is D4s G4is A4s G4is A4s | E4q rs F4h");
        vocals.add("G5is E5i Ri | G5s Ris E5q Rs | G5q E5i Rs A5is rs G5q A5s E5i D5i ri C5h Ri");
        vocals.add("C5s A3q C5i Rs | D5i Rs Eb5qs Rs | D5q Eb5i Rs D5is Eb5s D4q Rs | C5i A4q C5h");

        vocals.setTempo(160);
        vocals.setInstrument(50);
        player.play(vocals);
    }
    public static void test1() {
        Random generator = new Random();

        // each note can is expressed as a string Note_OctaveNumber_time: C4q: the note D in the 4th
        String music = "C4q D4q E4q F4q G4q A4q B4q C5q";
        Player player = new Player();
        player.play(music);

        List<Integer> positions  = Stream.generate(() -> generator.nextInt(8)).limit(20).toList();
        StringBuilder music2 = new StringBuilder();

        for (int i : positions){
            music2.append(CMajor.get(i)).append(" ");
        }
        music2.delete(music2.length() - 1, music2.length());

        player.play(music2.toString());
    }

    public static void test3() {
        Pattern loadedFile = new Pattern();
        try {
            File filePath = new File("src/barbie_girl.mid");
            loadedFile = MidiFileManager.loadPatternFromMidi(filePath);
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(loadedFile.toString().split(" ").length - 2);
    }
}

