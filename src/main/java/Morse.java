/*
 * Copyright 2026 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
import java.util.Map;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
/**
 * Simple Morse code encoder and decoder in Java. 
 * This program can convert text to Morse code and vice versa, as well as play the Morse code as audio tones. 
 */
public class Morse {

    static final int SAMPLE_RATE = 44100;
    static final int SAMPLE_SIZE = 8; // 8 bits per sample
    static final int NUM_CHANNELS = 1; // Mono
    static final int DOT_DURATION = 100; // Duration of a dot in milliseconds
    static final int DASH_DURATION = DOT_DURATION * 3; // Duration of a dash is dot * 3
    static final int FREQUENCY = 800; // Frequency of the Morse code tone in Hz
    static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("[0-9a-zA-Z\\s]+");
    static final Pattern DOTS_DASHES_PATTERN = Pattern.compile("[\\.\\-\\s]+");
    private boolean playAudio = true;

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

    public Morse() {

    }

    public Morse(boolean playAudio) {
        this.playAudio = playAudio;
    }

    public static void main(String[] args)throws Exception{
        if(args.length != 2){
            usage();
            System.exit(1);
        }
        boolean encode = "-e".equals(args[0]);
        String text = args[1].toUpperCase();
        Morse morse = new Morse();
        if(encode){
            System.out.println("Encoding: " + text);
            System.out.println("Encoded: " + morse.encode(text));
            System.exit(0);
        }else if(!"-d".equals(args[0])){
            usage();
            System.exit(1);
        }
        System.out.println("Decoding: " + text);
        System.out.println("Decoded: " + morse.decode(text));
        System.exit(0);
    }

    public static void usage(){
        System.out.println("Usage: java Morse <-e|-d> <text>");
        System.out.println("  -e: Encode text to Morse code");
        System.out.println("  -d: Decode Morse code to text");
    }

    public static String charToCode(char c){
        return CHAR_TO_CODE.get(c);
    }

    public static String codeToChar(String code){
        Character result = CODE_TO_CHAR.get(code);
        return result != null ? result.toString() : null;
    }

    public String encode(String text)throws Exception{
        if(!ALPHANUMERIC_PATTERN.matcher(text).matches()){
            throw new IllegalArgumentException("Can only encode alphanumerics (letters and digits only)");
        }
        char[] tokens = text.toCharArray();
        StringBuilder encoded = new StringBuilder();
        for (char token : tokens) {
            String code = charToCode(token);
            if (code != null) {
                System.out.println("\t"+token+"\t->\t" + code);
                encoded.append(code).append(" ");
                if(playAudio){
                    char[] dotsNDashes = code.toCharArray();
                    for(int i=0;i<dotsNDashes.length;i++){
                        if(dotsNDashes[i] == '.'){
                            playTone(DOT_DURATION, FREQUENCY);
                        }else if(dotsNDashes[i] == '-'){
                            playTone(DASH_DURATION, FREQUENCY);
                        }
                    }
                }
            }
        }
        return encoded.toString().trim();
    }

    public String decode(String encoded)throws Exception{
        if(!DOTS_DASHES_PATTERN.matcher(encoded).matches()){
            throw new IllegalArgumentException("Can only decode dots(.), dashes(-), and spaces");
        }
        String[] tokens = encoded.split(" ");
        StringBuilder decoded = new StringBuilder();
        for (String token : tokens) {
            String _char = codeToChar(token);
            if (_char != null) {
                System.out.println("\t"+token+"\t->\t"+_char);
                decoded.append(_char);
                if(playAudio){
                    char[] dotsNDashes = token.toCharArray();
                    for(int i=0;i<dotsNDashes.length;i++){
                        if(dotsNDashes[i] == '.'){
                            playTone(DOT_DURATION, FREQUENCY+200);
                        }else if(dotsNDashes[i] == '-'){
                            playTone(DASH_DURATION, FREQUENCY+200);
                        }
                    }
                }
            }else{
                System.out.println("\t"+token+"\t->\t???");
            }
        }
        return decoded.toString().trim();
    }

    private static void playTone(int durationMs, int frequency) throws Exception {
        byte[] buffer = new byte[durationMs * SAMPLE_RATE / 1000];
        for (int i = 0; i < buffer.length; i++) {
            double angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE;
            buffer[i] = (byte) (Math.sin(angle) * 127.0);
        }
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, NUM_CHANNELS, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        line.start();
        line.write(buffer, 0, buffer.length);
        line.drain();
        line.close();
    }

}
