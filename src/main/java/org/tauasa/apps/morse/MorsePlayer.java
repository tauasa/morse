package org.tauasa.apps.morse;

import java.io.Closeable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Plays Morse code as audio tones using the Java Sound API.
 *
 * Timing (standard Morse, ~20 WPM):
 *   dot      =  60 ms
 *   dash     = 180 ms  (3× dot)
 *   intra-character gap = 60 ms
 *   inter-letter gap    = 180 ms  (already present as space between codes)
 *   inter-word gap      = 420 ms  (7× dot; "/" handled explicitly)
 *
 * Tone frequency: 700 Hz (classic Morse receiver sound).
 */
public class MorsePlayer implements Closeable {

    private static final int   SAMPLE_RATE  = 44_100;  // Hz
    private static final float FREQUENCY    = 700f;     // Hz
    private static final float AMPLITUDE    = 0.5f;     // 0.0 – 1.0

    // Timing in milliseconds
    private static final int DOT_MS        = 60;
    private static final int DASH_MS       = DOT_MS * 3;
    private static final int SYMBOL_GAP_MS = DOT_MS;        // gap between dots/dashes within a letter
    private static final int LETTER_GAP_MS = DOT_MS * 3;    // gap between letters (minus symbol gap already added)
    private static final int WORD_GAP_MS   = DOT_MS * 7;    // gap between words

    private final SourceDataLine line;
    private final AudioFormat format;

    public MorsePlayer() {
        format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        SourceDataLine tmp = null;
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Warning: Audio output line not supported on this system. --play will be skipped.");
            } else {
                tmp = (SourceDataLine) AudioSystem.getLine(info);
                tmp.open(format, SAMPLE_RATE / 10); // small buffer for low latency
                tmp.start();
            }
        } catch (LineUnavailableException e) {
            System.err.println("Warning: Audio unavailable (" + e.getMessage() + "). --play will be skipped.");
        }
        this.line = tmp;
    }

    /**
     * Plays the given Morse string as audio tones.
     * Morse format: dots, dashes, spaces between letters, " / " between words.
     */
    public void play(String morse) {
        if (line == null) return; // audio not available

        String[] words = morse.split(" / ");
        for (int w = 0; w < words.length; w++) {
            if (w > 0) silence(WORD_GAP_MS);

            String[] letters = words[w].trim().split(" ");
            for (int l = 0; l < letters.length; l++) {
                if (l > 0) silence(LETTER_GAP_MS);

                String code = letters[l];
                for (int s = 0; s < code.length(); s++) {
                    if (s > 0) silence(SYMBOL_GAP_MS);
                    char sym = code.charAt(s);
                    if (sym == '.') {
                        tone(DOT_MS);
                    } else if (sym == '-') {
                        tone(DASH_MS);
                    }
                }
            }
        }

        line.drain();
    }

    /** Returns true if audio output is available on this system. */
    public boolean isAvailable() {
        return line != null;
    }

    /**
     * Plays the given Morse string as tones on a background thread.
     * Returns immediately; call {@link #stopAndDrain()} to wait for completion.
     *
     * @param morse  Morse string (dots, dashes, spaces, "/" word separators)
     * @param onDone Runnable called on the calling thread when playback ends
     */
    public Thread playAsync(String morse, Runnable onDone) {
        Thread t = new Thread(() -> {
            play(morse);
            if (onDone != null) onDone.run();
        }, "morse-audio");
        t.setDaemon(true);
        t.start();
        return t;
    }

    // ── Internals ──────────────────────────────────────────────────────────────

    /** Outputs a sine-wave tone for the given duration (ms). */
    private void tone(int durationMs) {
        int samples = msToSamples(durationMs);
        byte[] buf = new byte[samples * 2]; // 16-bit = 2 bytes per sample

        for (int i = 0; i < samples; i++) {
            // Sine wave with a short linear ramp-up/down to avoid clicks (10 ms)
            double t = (double) i / SAMPLE_RATE;
            double envelope = ramp(i, samples);
            double value = AMPLITUDE * envelope * Math.sin(2 * Math.PI * FREQUENCY * t);
            short sample = (short) (value * Short.MAX_VALUE);
            buf[2 * i]     = (byte) (sample & 0xFF);
            buf[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        line.write(buf, 0, buf.length);
    }

    /** Outputs silence for the given duration (ms). */
    private void silence(int durationMs) {
        int samples = msToSamples(durationMs);
        byte[] buf = new byte[samples * 2];
        line.write(buf, 0, buf.length);
    }

    /** Ramp envelope: fade in/out over ~10 ms to eliminate clicks. */
    private double ramp(int i, int total) {
        int rampSamples = msToSamples(10);
        if (i < rampSamples) {
            return (double) i / rampSamples;
        } else if (i > total - rampSamples) {
            return (double) (total - i) / rampSamples;
        }
        return 1.0;
    }

    private int msToSamples(int ms) {
        return (int) (SAMPLE_RATE * ms / 1000.0);
    }

    @Override
    public void close() {
        if (line != null) {
            line.drain();
            line.stop();
            line.close();
        }
    }
}
