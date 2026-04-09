import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class MorseTest {

    @Test
    public void testCharToCodeKnown() {
        assertEquals(".-", Morse.charToCode('A'));
        assertEquals(".", Morse.charToCode('E'));
        assertEquals("-----", Morse.charToCode('0'));
    }

    @Test
    public void testCharToCodeUnknown() {
        assertNull(Morse.charToCode('!'));
        assertNull(Morse.charToCode(' '));
    }

    @Test
    public void testCodeToCharKnown() {
        assertEquals("A", Morse.codeToChar(".-"));
        assertEquals("E", Morse.codeToChar("."));
        assertEquals("0", Morse.codeToChar("-----"));
    }

    @Test
    public void testCodeToCharUnknown() {
        assertNull(Morse.codeToChar("...!"));
        assertNull(Morse.codeToChar(""));
    }

    @Test
    public void testEncodeValid() throws Exception {
        Morse morse = new Morse(false); // no audio
        assertEquals(".- -...", morse.encode("AB"));
        assertEquals(".", morse.encode("E"));
    }

    @Test
    public void testEncodeInvalid() {
        Morse morse = new Morse(false);
        assertThrows(IllegalArgumentException.class, () -> morse.encode("A!"));
        //assertThrows(IllegalArgumentException.class, () -> morse.encode("A B"));
    }

    @Test
    public void testDecodeValid() throws Exception {
        Morse morse = new Morse(false);
        assertEquals("AB", morse.decode(".- -..."));
        assertEquals("E", morse.decode("."));
    }

    @Test
    public void testDecodeInvalid() {
        Morse morse = new Morse(false);
        assertThrows(IllegalArgumentException.class, () -> morse.decode(".- !"));
        assertThrows(IllegalArgumentException.class, () -> morse.decode("A"));
    }
}