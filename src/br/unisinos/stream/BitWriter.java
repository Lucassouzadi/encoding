package br.unisinos.stream;

import br.unisinos.errorDetection.Hamming74;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class BitWriter implements Closeable {

    private static final int BUFFER_LENGTH = 8;
    private static final Hamming74 HAMMING = new Hamming74();

    private OutputStream out;
    private byte buffer;
    private int count;
    private int hammingCount;
    private short hammingBuffer;
    private boolean useHamming = false;
    private boolean fillupBit = false;

    public BitWriter(OutputStream out) {
        this.out = out;
        this.count = 0;
    }

    public void setUseHamming(boolean flag) {
        this.useHamming = flag;
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

    public void writeHammingCodeword(boolean[] bits) {
        boolean[] s = new boolean[4];
        boolean[] t = new boolean[3];
        s[0] = bits[0];
        s[1] = bits[1];
        s[2] = bits[2];
        s[3] = bits[3];
        t[0] = bits[4];
        t[1] = bits[5];
        t[2] = bits[6];
        HAMMING.correctCodeword(s, t);
        for (boolean bit : s) {
            writeBit(bit);
        }
    }

    public void writeBit(boolean bit) {
        if (this.useHamming) {
            writeBitHamming(bit);
        } else {
            writeBit(bit, true);
        }
    }

    private void writeBit(boolean bit, boolean flush) {
        this.count++;
        this.buffer <<= 1;
        this.buffer |= bit ? 1 : 0;
        if (flush && this.count == BUFFER_LENGTH) {
            flush();
        }
    }

    private void writeBitHamming(boolean bit) {
        this.hammingCount++;
        this.hammingBuffer <<= 1;
        this.hammingBuffer |= bit ? 1 : 0;
        if (this.hammingCount == HAMMING.K) {
            hammingFlush();
        }
    }

    private void hammingFlush() {
        boolean[] parityBits = HAMMING.parityBits(this.hammingBuffer);

        for (int i = HAMMING.K; i > 0; i--) {
            int bit = (this.hammingBuffer >> i - 1) & 1;
            writeBit(bit == 1, true);
        }
        for (boolean parityBit : parityBits) {
            writeBit(parityBit, true);
        }
        this.hammingBuffer = 0;
        this.hammingCount = 0;
    }

    public void flush() {
        if (this.count == 0) return;
        if (this.count < BUFFER_LENGTH) {
            writeBit(fillupBit, false);
            flush();
        } else {
            this.writeByteToOutPutStream(buffer);
            this.buffer = 0;
            this.count = 0;
        }
    }

    private void writeByteToOutPutStream(byte value) {
        try {
            this.out.write(value);
        } catch (IOException ioe) {
            throw new RuntimeException("Erro ao ler arquivo", ioe);
        }
    }

    public void writeByte(byte value) {
        this.writeBinary(value, 8);
    }

    @Override
    public void close() {
        flush();
    }
}
