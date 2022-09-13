package br.unisinos.encoding;

import br.unisinos.exception.EndOfStreamException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

import java.util.Map;

public abstract class Encoding {

    public static final byte GOLOMB = 0;
    public static final byte ELIAS_GAMMA = 1;
    public static final byte FIBONACCI = 2;
    public static final byte UNARY = 3;
    public static final byte DELTA = 4;
    public static final byte BINARY = 5;

    public static Encoding getInstance(int identifier, int arg) {
        switch (identifier) {
            case GOLOMB:
                return new Golomb(arg);
            case ELIAS_GAMMA:
                return new EliasGamma();
            case FIBONACCI:
                return new Fibonacci();
            case DELTA:
                return new Delta(arg);
            case UNARY:
                return new Unary(arg > 0);
            case BINARY:
                return new Binary();
        }
        return null;
    }

    public void encodeByte(Map<Integer, Integer> dictionary, BitWriter writer, byte value) {
        try {
            encodeByte(writer, dictionary.get(Byte.toUnsignedInt(value)).byteValue());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public abstract void encodeByte(BitWriter writer, byte value);

    public abstract byte decodeByte(BitReader reader) throws EndOfStreamException;

    public void encodeByteArray(BitWriter writer, byte[] bytes) {
        for (byte b : bytes) {
            this.encodeByte(writer, b);
        }
    }

    public void encodeByteArray(Map<Integer, Integer> dictionary, BitWriter writer, byte[] bytes) {
        for (byte b : bytes) {
            this.encodeByte(dictionary, writer, b);
        }
    }

    public void encodeStream(Map<Integer, Integer> dictionary, BitWriter writer, BitReader bitReader) throws EndOfStreamException {
        while (true) {
            byte read = bitReader.readByte();
            this.encodeByte(dictionary, writer, read);
        }
    }

    public void encodeStream(BitWriter writer, BitReader bitReader) throws EndOfStreamException {
        while (true) {
            byte read = bitReader.readByte();
            this.encodeByte(writer, read);
        }
    }

    public abstract byte getIdentifier();

    public byte getArg() {
        return 0;
    }

    public boolean getFillupBit() {
        return false;
    }

    public void writeHeader(BitWriter writer) {
        writer.writeByte(getIdentifier());
        writer.writeByte(getArg());
    }

    public static int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
    }

}
