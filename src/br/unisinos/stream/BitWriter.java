package br.unisinos.stream;

import java.io.IOException;
import java.io.OutputStream;

public class BitWriter implements AutoCloseable {

    private static final int BUFFER_LENGTH = 8;

    private OutputStream out;
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

    public void writeBinary(int value, int length) throws IOException {
        int shift = length - 1;
        while (shift >= 0) {
            int bit = (value >> shift--) & 1; // get bit at position [shift]
            writeBit(bit == 1);
        }
    }

    public void writeBit(boolean bit) throws IOException {
        writeBit(bit, true);
    }

    private void writeBit(boolean bit, boolean flush) throws IOException {
        this.count++;
        this.buffer <<= 1;
        this.buffer |= bit ? 1 : 0;
        if (flush && this.count == BUFFER_LENGTH) {
            flush();
        }
    }

    public void flush() throws IOException {
        if (this.count == 0) return;
        if (this.count < BUFFER_LENGTH) {
            writeBit(fillupBit, false);
            flush();
        } else {
            this.out.write(buffer);
            this.buffer = 0;
            this.count = 0;
        }
    }

    public void writeByte(byte value) throws IOException {
        this.writeBinary(value, 8);
    }

    @Override
    public void close() throws Exception {
        flush();
    }
}
