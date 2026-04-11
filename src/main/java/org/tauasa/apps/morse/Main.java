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
package org.tauasa.apps.morse;

/**
 * Morse Code Converter - Command Line Application
 *
 * Usage:
 *   java -jar morse.jar encode [--play] <text>
 *   java -jar morse.jar decode [--play] <morse>
 *   java -jar morse.jar --help
 *
 * Morse input uses '.' for dot, '-' for dash, ' ' between letters, '/' between words.
 */
public class Main {

    private static final String HELP =
        """
        ╔══════════════════════════════════════════════════╗
        ║           Morse Code Converter v1.0              ║
        ╚══════════════════════════════════════════════════╝

        USAGE:
          java -jar morse.jar <command> [options] <input>

        COMMANDS:
          encode    Convert plain text to Morse code
          decode    Convert Morse code to plain text

        OPTIONS:
          --play    Play audio tones while printing output
          --help    Show this help message

        MORSE FORMAT:
          . = dot      - = dash
          (space)  = letter separator
          /        = word separator

        EXAMPLES:
          java -jar morse.jar encode "Hello World"
          java -jar morse.jar encode --play "SOS"
          java -jar morse.jar decode "... --- ..."
          java -jar morse.jar decode --play ".... . .-.. .-.. --- / .-- --- .-. .-.. -.."

        SUPPORTED CHARACTERS:
          A-Z, 0-9, and punctuation: . , ? ! - / @ ( )
        """;

    public static void main(String[] args) {
        if (args.length == 0) {
            printError("No command specified.");
            System.out.println("Run with --help for usage information.");
            System.exit(1);
        }

        if (args[0].equals("--help") || args[0].equals("-h")) {
            System.out.println(HELP);
            return;
        }

        String command = args[0].toLowerCase();
        if (!command.equals("encode") && !command.equals("decode")) {
            printError("Unknown command: '" + args[0] + "'. Expected 'encode' or 'decode'.");
            System.exit(1);
        }

        boolean play = false;
        StringBuilder inputBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--play")) {
                play = true;
            } else if (args[i].equals("--help")) {
                System.out.println(HELP);
                return;
            } else {
                if (inputBuilder.length() > 0) inputBuilder.append(" ");
                inputBuilder.append(args[i]);
            }
        }

        String input = inputBuilder.toString().trim();
        if (input.isEmpty()) {
            printError("No input provided.");
            System.exit(1);
        }

        MorseConverter converter = new MorseConverter();
        MorsePlayer player = play ? new MorsePlayer() : null;

        try {
            if (command.equals("encode")) {
                runEncode(converter, player, input);
            } else {
                runDecode(converter, player, input);
            }
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
            System.exit(1);
        } finally {
            if (player != null) player.close();
        }
    }

    private static void runEncode(MorseConverter converter, MorsePlayer player, String text) {
        System.out.println("┌─ Input (Text) " + "─".repeat(40));
        System.out.println("│  " + text);
        System.out.println("├─ Output (Morse) " + "─".repeat(38));

        String morse = converter.encode(text);
        System.out.println("│  " + morse);
        System.out.println("└" + "─".repeat(56));

        if (player != null) {
            System.out.println("\n♪ Playing Morse tones...");
            player.play(morse);
            System.out.println("♪ Done.");
        }
    }

    private static void runDecode(MorseConverter converter, MorsePlayer player, String morse) {
        System.out.println("┌─ Input (Morse) " + "─".repeat(39));
        System.out.println("│  " + morse);
        System.out.println("├─ Output (Text) " + "─".repeat(39));

        String text = converter.decode(morse);
        System.out.println("│  " + text);
        System.out.println("└" + "─".repeat(56));

        if (player != null) {
            System.out.println("\n♪ Playing Morse tones...");
            player.play(morse);
            System.out.println("♪ Done.");
        }
    }

    private static void printError(String msg) {
        System.err.println("ERROR: " + msg);
    }
}
