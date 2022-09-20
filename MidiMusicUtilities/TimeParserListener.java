package MidiMusicUtilities;

import org.jfugue.parser.ParserListenerAdapter;

public class TimeParserListener extends ParserListenerAdapter {
    public byte numerator;
    public byte denominator;

    @Override
    public void onTimeSignatureParsed (byte numerator, byte denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public void getTheTimeSignature () {
        for (byte d = 1; d <= MusicUtilities.DENOMINATOR_LIMIT; d++) {
            for (byte n = 1; n <= Math.pow(2, d); n++) {
                if (this.numerator != 0 && this.denominator != 0) break;

                onTimeSignatureParsed(d, n);
            }
        }
    }
}
