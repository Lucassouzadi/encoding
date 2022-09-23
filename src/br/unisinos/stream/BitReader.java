package br.unisinos.stream;

import br.unisinos.exception.EndOfFileException;

import java.io.IOException;
import java.io.InputStream;

public class BitReader {

    private static final int BUFFER_LENGTH = 8;

    private final InputStream in;
    private byte buffer;
    private int count;

    public BitReader(InputStream in) {
        this.in = in;
        this.buffer = 0;
        this.count = BUFFER_LENGTH;
    }

    public byte readByte() throws EndOfFileException {
        int read = readByteFromInputStream();
        if (read == -1) {
            count = 0;
            buffer = 0;
            throw new EndOfFileException();
        }
        return (byte) read;
    }

    private int readByteFromInputStream() {
        try {
            return in.read();
        } catch (IOException ioe) {
            throw new RuntimeException("Erro ao ler arquivo", ioe);
        }
    }

    public byte readBits(int length) throws EndOfFileException {
        byte readByte = 0;
        for (int i = 0; i < length; i++) {
            readByte <<= 1;
            readByte |= readBit() ? 1 : 0;
        }
        return readByte;
    }

    public boolean readBit() throws EndOfFileException {
        if (count == BUFFER_LENGTH) {
            buffer = readByte();
            count = 0;
        }
        int shift = BUFFER_LENGTH - 1 - count++;
        return ((buffer >> shift) & 1) == 1;
    }

}
