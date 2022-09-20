package MidiMusicUtilities;

import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;

public class MeasuresParserListener extends ParserListenerAdapter {
    byte numerator;
    byte denominator;
    // timePerMeasure can be induced from numerator and denominator
    public double timePerMeasure;
    private double currentTime;
    public List<List<Note>> measures;
    public MeasuresParserListener(byte numerator, byte denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.timePerMeasure = numerator/ (Math.pow(2, denominator));
        this.currentTime = 0.0;
        measures = new ArrayList<>();
    }

    @Override
    public void onNoteParsed(Note note) {
        if (measures.isEmpty() || currentTime >= timePerMeasure) {
            measures.add(new ArrayList<>());
            currentTime = 0;
        }
        double noteDuration = MusicUtilities.fitDuration(note.getDuration());
        currentTime += noteDuration;
        measures.get(measures.size() - 1).add(
                new Note(MusicUtilities.C_RIGHT_HAND_VALUE + note.getPositionInOctave(), noteDuration));
    }

}
