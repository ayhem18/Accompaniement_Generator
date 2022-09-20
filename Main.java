import AccompanimentGenerator.Generator;
import org.jfugue.pattern.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;
import java.util.Scanner;

import static AccompanimentGenerator.FilePlayer.displayMidiFile;

public class Main {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final String ASK_FOR_PATH = "Please enter the path of the Midi file";
    private static final String ASK_FOR_PATH_SAVE = "Please enter the path where to save out magic";

    public static void main2(String[] args) throws InvalidMidiDataException, IOException {
        String file = "src/testFiles/input1.mid";
        displayMidiFile(file);
        Pattern chords = Generator.generateAccompaniment(file);
        System.out.println(chords);
        displayMidiFile(file, chords);
    }

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        // if command line arguments are passed: the first is the file path
        // and the second is the path where the result should be stored
        String filePath ;
        String newFilePath ;
        if (args.length == 2) {
            filePath = args[0];
            newFilePath = args[1];
        }

        else {
            // if the arguments are not passed (correctly at least) prompt the user to enter them through the console
            System.out.println();
            filePath = INPUT.nextLine();
            newFilePath = INPUT.nextLine();
        }
        // display original melody
        displayMidiFile(filePath);

        try {
            // generate accompaniment
            Pattern chords = Generator.generateAccompaniment(filePath);
            // display the result of the combination
            displayMidiFile(filePath, chords, newFilePath);
        } catch (InvalidMidiDataException | IOException e) {
            System.out.println("Please make sure the file path is correct");
        }

    }

}

