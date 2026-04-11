package org.tauasa.apps.morse;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles encoding text → Morse and decoding Morse → text.
 * Letter separator: single space
 * Word separator:  " / "
 */
public class MorseConverter {

    // ── Encoding map (character → morse) ──────────────────────────────────────
    private static final Map<Character, String> ENCODE_MAP = new HashMap<>();

    // ── Decoding map (morse → character) ──────────────────────────────────────
    private static final Map<String, Character> DECODE_MAP = new HashMap<>();

    static {
        // Letters
        String[][] table = {
            {"A", ".-"},   {"B", "-..."},  {"C", "-.-."},  {"D", "-.."},
            {"E", "."},    {"F", "..-."},  {"G", "--."},   {"H", "...."},
            {"I", ".."},   {"J", ".---"},  {"K", "-.-"},   {"L", ".-.."},
            {"M", "--"},   {"N", "-."},    {"O", "---"},   {"P", ".--."},
            {"Q", "--.-"}, {"R", ".-."},   {"S", "..."},   {"T", "-"},
            {"U", "..-"},  {"V", "...-"},  {"W", ".--"},   {"X", "-..-"},
            {"Y", "-.--"}, {"Z", "--.."},
            // Digits
            {"0", "-----"}, {"1", ".----"}, {"2", "..---"}, {"3", "...--"},
            {"4", "....-"}, {"5", "....."}, {"6", "-...."}, {"7", "--..."},
            {"8", "---.."}, {"9", "----."},
            // Punctuation
            {".", ".-.-.-"}, {",", "--..--"}, {"?", "..--.."},
            {"!", "-.-.--"}, {"-", "-....-"}, {"/", "-..-."},
            {"@", ".--.-."}, {"(", "-.--."}, {")", "-.--.-"},
        };

        for (String[] pair : table) {
            char ch = pair[0].charAt(0);
            String code = pair[1];
            ENCODE_MAP.put(ch, code);
            DECODE_MAP.put(code, ch);
        }
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Encodes plain text to Morse code.
     * Letters are separated by ' ', words by ' / '.
     *
     * @throws IllegalArgumentException if the text contains unsupported characters
     */
    public String encode(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Input text must not be empty.");
        }

        StringBuilder result = new StringBuilder();
        String[] words = text.trim().toUpperCase().split("\\s+");

        for (int w = 0; w < words.length; w++) {
            if (w > 0) result.append(" / ");
            String word = words[w];

            for (int c = 0; c < word.length(); c++) {
                char ch = word.charAt(c);
                String code = ENCODE_MAP.get(ch);
                if (code == null) {
                    throw new IllegalArgumentException(
                        "Unsupported character: '" + ch + "' (position " + (c + 1) + " in word \"" + word + "\")."
                    );
                }
                if (c > 0) result.append(" ");
                result.append(code);
            }
        }

        return result.toString();
    }

    /**
     * Decodes Morse code to plain text.
     * Expects letters separated by ' ', words separated by ' / '.
     *
     * @throws IllegalArgumentException if an unknown Morse sequence is encountered
     */
    public String decode(String morse) {
        if (morse == null || morse.isBlank()) {
            throw new IllegalArgumentException("Input Morse code must not be empty.");
        }

        // Normalise: collapse multiple spaces, trim
        String normalised = morse.trim().replaceAll(" {2,}", " ");

        StringBuilder result = new StringBuilder();
        String[] words = normalised.split(" / ");

        for (int w = 0; w < words.length; w++) {
            if (w > 0) result.append(" ");
            String[] letters = words[w].trim().split(" ");

            for (String code : letters) {
                if (code.isEmpty()) continue;
                Character ch = DECODE_MAP.get(code);
                if (ch == null) {
                    throw new IllegalArgumentException(
                        "Unknown Morse sequence: '" + code + "'."
                    );
                }
                result.append(ch);
            }
        }

        return result.toString();
    }

    /** Returns a copy of the encode map for external use (e.g., help display). */
    public Map<Character, String> getEncodeMap() {
        return new HashMap<>(ENCODE_MAP);
    }
}
