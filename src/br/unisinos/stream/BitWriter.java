package br.unisinos.stream;

import br.unisinos.errorDetection.Hamming74;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class BitWriter implements Closeable {

    private static final int BUFFER_LENGTH = 8;

    private final OutputStream out;
    private byte buffer;
    private int count;
    private boolean fillupBit = false;

    public BitWriter(OutputStream out) {
        this.out = out;
        this.count = 0;
    }

    public void setFillupBit(boolean bit) {
        this.fillupBit = bit;
    }

    public void writeBinary(int value, int length) {
        int shift = length - 1;
        while (shift >= 0) {
            int bit = (value >> shift--) & 1; // get bit at position [shift]
            writeBit(bit == 1);
        }
    }

    public void decodeHammingCodeword(boolean[] bits) {
        boolean[] s = new boolean[4];
        boolean[] t = new boolean[3];
        s[0] = bits[0];
        s[1] = bits[1];
        s[2] = bits[2];
        s[3] = bits[3];
        t[0] = bits[4];
        t[1] = bits[5];
        t[2] = bits[6];
        Hamming74.correctCodeword(s, t);
        for (boolean bit : s) {
            writeBit(bit);
        }
    }

    public void writeBit(boolean bit) {
        writeBit(bit, true);
    }

    private void writeBit(boolean bit, boolean flush) {
        count++;
        buffer <<= 1;
        buffer |= bit ? 1 : 0;
        if (flush && count == BUFFER_LENGTH) {
            flush();
        }
    }

    public void encodeHammingNibble(boolean[] hammingShort) {
        for (boolean bit : hammingShort) {
            writeBit(bit, true);
        }

        boolean[] parityBits = Hamming74.parityBits(hammingShort);
        for (boolean parityBit : parityBits) {
            writeBit(parityBit, true);
        }
    }

    private void flush() {
        if (count == 0) return;
        while (count < BUFFER_LENGTH) { // Fillup buffer (may occour when flushing the last byte of the file)
            writeBit(fillupBit, false);
        }
        writeByteToOutputStream(buffer);
        buffer = 0;
        count = 0;
    }

    private void writeByteToOutputStream(byte value) {
        try {
            out.write(value);
        } catch (IOException ioe) {
            throw new RuntimeException("Error while writing do OutputStream", ioe);
        }
    }

    public void writeByte(byte value) {
        writeBinary(value, 8);
    }

    @Override
    public void close() {
        flush();
    }
}
