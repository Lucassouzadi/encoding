package br.unisinos.encoding;

import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

import java.io.IOException;
import java.io.InputStream;

public abstract class Encoding {

    public static final byte GOLOMB = 0;
    public static final byte ELIAS_GAMMA = 1;
    public static final byte FIBBONACCI = 2;
    public static final byte UNARY = 3;
    public static final byte DELTA = 4;
    public static final byte BINARY = 5;

    public static Encoding getInstance(int identifier, int arg) {
        switch (identifier) {
            case GOLOMB:
                return new Golomb(arg);
            case UNARY:
                return new Unary(arg > 0);
            case BINARY:
                return new Binary();
        }
        return null;
    }

    public abstract void encodeByte(BitWriter writer, byte value) throws IOException;

    public abstract byte decodeByte(BitReader reader) throws IOException;

    public void encodeByteArray(BitWriter writer, byte[] bytes) throws IOException {
        for (byte b : bytes) {
            this.encodeByte(writer, b);
        }
    }

    public void encodeStream(BitWriter writer, InputStream stream) throws IOException {
        int read;
        while ((read = stream.read()) != -1) {
            this.encodeByte(writer, (byte) read);
        }
    }

    public abstract byte getIdentifier();

    public byte getArg() {
        return 0;
    }

    public boolean getFillupBit() {
        return false;
    }

    public void writeHeader(BitWriter writer) throws IOException {
        writer.writeByte(getIdentifier());
        writer.writeByte(getArg());
    }

}
