package org.tauasa.apps.morse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MorseConverterTest {

    private MorseConverter converter;

    @BeforeEach
    void setUp() {
        converter = new MorseConverter();
    }

    // ── Encoding ──────────────────────────────────────────────────────────────

    @Test
    void encodesSingleLetter() {
        assertEquals(".-", converter.encode("A"));
    }

    @Test
    void encodesWord() {
        assertEquals("... --- ...", converter.encode("SOS"));
    }

    @Test
    void encodesMultipleWords() {
        String result = converter.encode("HI THERE");
        assertEquals(".... .. / - .... . .-. .", result);
    }

    @Test
    void encodesLowerCase() {
        assertEquals(".-", converter.encode("a"));
    }

    @Test
    void encodesDigits() {
        assertEquals(".---- ..--- ...--", converter.encode("123"));
    }

    @Test
    void encodesPunctuation() {
        assertEquals(".-.-.-", converter.encode("."));
    }

    @Test
    void throwsOnUnsupportedCharacter() {
        assertThrows(IllegalArgumentException.class, () -> converter.encode("Hello #World"));
    }

    @Test
    void throwsOnBlankInput() {
        assertThrows(IllegalArgumentException.class, () -> converter.encode("   "));
    }

    // ── Decoding ──────────────────────────────────────────────────────────────

    @Test
    void decodesSingleCode() {
        assertEquals("A", converter.decode(".-"));
    }

    @Test
    void decodesWord() {
        assertEquals("SOS", converter.decode("... --- ..."));
    }

    @Test
    void decodesMultipleWords() {
        assertEquals("HI THERE", converter.decode(".... .. / - .... . .-. ."));
    }

    @Test
    void decodesDigits() {
        assertEquals("123", converter.decode(".---- ..--- ...--"));
    }

    @Test
    void throwsOnUnknownCode() {
        assertThrows(IllegalArgumentException.class, () -> converter.decode("..---."));
    }

    @Test
    void throwsOnBlankMorse() {
        assertThrows(IllegalArgumentException.class, () -> converter.decode(""));
    }

    // ── Round-trip ────────────────────────────────────────────────────────────

    @Test
    void roundTripSimple() {
        String original = "HELLO WORLD";
        assertEquals(original, converter.decode(converter.encode(original)));
    }

    @Test
    void roundTripWithDigitsAndPunctuation() {
        String original = "MEETING AT 3PM";
        assertEquals(original, converter.decode(converter.encode(original)));
    }
}
