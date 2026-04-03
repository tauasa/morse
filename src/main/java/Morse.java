//package org.tauasa.morse;

import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Morse {

    static final int SAMPLE_RATE = 44100;//8000
    static final int DOT_DURATION = 100; // Duration of a dot in milliseconds
    static final int DASH_DURATION = DOT_DURATION * 3; // Duration of a dash is
    static final int FREQUENCY = 800; // Frequency of the Morse code tone in Hz

    private static final Map<Character, String> CHAR_TO_CODE = Map.ofEntries(
        Map.entry('A', ".-"), Map.entry('B', "-..."), Map.entry('C', "-.-."), Map.entry('D', "-.."),
        Map.entry('E', "."), Map.entry('F', "..-."), Map.entry('G', "--."), Map.entry('H', "...."),
        Map.entry('I', ".."), Map.entry('J', ".---"), Map.entry('K', "-.-"), Map.entry('L', ".-.."),
        Map.entry('M', "--"), Map.entry('N', "-."), Map.entry('O', "---"), Map.entry('P', ".--."),
        Map.entry('Q', "--.-"), Map.entry('R', ".-."), Map.entry('S', "..."), Map.entry('T', "-"),
        Map.entry('U', "..-"), Map.entry('V', "...-"), Map.entry('W', ".--"), Map.entry('X', "-..-"),
        Map.entry('Y', "-.--"), Map.entry('Z', "--.."),
        Map.entry('0', "-----"), Map.entry('1', ".----"), Map.entry('2', "..---"), Map.entry('3', "...--"),
        Map.entry('4', "....-"), Map.entry('5', "....."), Map.entry('6', "-...."), Map.entry('7', "--..."),
        Map.entry('8', "---.."), Map.entry('9', "----.")
    );
    private static final Map<String, Character> CODE_TO_CHAR = Map.ofEntries(
        Map.entry(".-", 'A'), Map.entry("-...", 'B'), Map.entry("-.-.", 'C'), Map.entry("-..", 'D'),
        Map.entry(".", 'E'), Map.entry("..-.", 'F'), Map.entry("--.", 'G'), Map.entry("....", 'H'),
        Map.entry("..", 'I'), Map.entry(".---", 'J'), Map.entry("-.-", 'K'), Map.entry(".-..", 'L'),
        Map.entry("--", 'M'), Map.entry("-.", 'N'), Map.entry("---", 'O'), Map.entry(".--.", 'P'),
        Map.entry("--.-", 'Q'), Map.entry(".-.", 'R'), Map.entry("...", 'S'), Map.entry("-", 'T'),
        Map.entry("..-", 'U'), Map.entry("...-", 'V'), Map.entry(".--", 'W'), Map.entry("-..-", 'X'),
        Map.entry("-.--", 'Y'), Map.entry("--..", 'Z'),
        Map.entry("-----", '0'), Map.entry(".----", '1'), Map.entry("..---", '2'), Map.entry("...--", '3'),
        Map.entry("....-", '4'), Map.entry(".....", '5'), Map.entry("-....", '6'), Map.entry("--...", '7'),
        Map.entry("---..", '8'), Map.entry("----.", '9')
    );

    public static void main(String[] args)throws Exception{
        String text = "HELLO WORLD 123";
        String encoded = encode(text);
        System.out.println("Encoded: " + encoded);

        String decoded = decode(encoded);
        System.out.println("Decoded: " + decoded);
    }

    public static String charToCode(char c){
        return CHAR_TO_CODE.get(c);
    }

    public static String codeToChar(String code){
        return CODE_TO_CHAR.get(code).toString();
    }

    public static String encode(String text)throws Exception{

        char[] tokens = text.toCharArray();

        StringBuilder encoded = new StringBuilder();

        for (char token : tokens) {
            String code = charToCode(token);
            if (code != null) {
                System.out.println(token+" -> "+code + " ");
                encoded.append(code).append(" ");
                char[] dotsNDashes = code.toCharArray();
                for(int i=0;i<dotsNDashes.length;i++){
                    if(dotsNDashes[i] == '.'){
                        playTone(FREQUENCY, DOT_DURATION);
                    }else if(dotsNDashes[i] == '-'){
                        playTone(FREQUENCY, DASH_DURATION);
                    }
                }
                playTone(FREQUENCY, DOT_DURATION);
            }
        }

        //return the encoded string
        return encoded.toString().trim();

    }

    public static String decode(String encoded){

        String[] tokens = encoded.split(" ");

        StringBuilder decoded = new StringBuilder();

        for (String token : tokens) {
            String _char = codeToChar(token);

            if (_char != null) {
                System.out.println(token+" -> "+_char + " ");
                decoded.append(_char).append(" ");
            }
        }

        //return the encoded string
        return decoded.toString().trim();

    }

    private static void playTone(int frequency, int durationMs) throws Exception {
        byte[] buffer = new byte[durationMs * SAMPLE_RATE / 1000];
        for (int i = 0; i < buffer.length; i++) {
            double angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE;
            buffer[i] = (byte) (Math.sin(angle) * 127.0);
        }
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        line.start();
        line.write(buffer, 0, buffer.length);
        line.drain();
        line.close();
    }

}
