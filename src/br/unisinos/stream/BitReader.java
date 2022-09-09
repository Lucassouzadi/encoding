package br.unisinos.stream;

import java.io.IOException;
import java.io.InputStream;

public class BitReader {

    private static final int BUFFER_LENGTH = 8;

    private InputStream in;
    private byte buffer;
    private int count;

    public BitReader(InputStream in) {
        this.in = in;
        this.buffer = 0;
        this.count = BUFFER_LENGTH;
    }

    public byte readByte() throws IOException {
        int read = in.read();
        if (read == -1) {
            count = 0;
            throw new RuntimeException("lalala fim do arquivo");
        }
        return (byte) read;
    }

    public byte readBits(int length) throws IOException {
        byte readByte = 0;
        for (int i = 0; i < length; i++) {
            readByte <<= i;
            readByte |= readBit() ? 1 : 0;
        }
        return readByte;
    }

    public boolean readBit() throws IOException {
        if (count == BUFFER_LENGTH) {
            buffer = readByte();
            count = 0;
        }
        int shift = BUFFER_LENGTH - 1 - count++;
        return ((buffer >> shift) & 1) == 1;
    }

}
