package br.unisinos.encoding;

import br.unisinos.exception.EndOfFileException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

import java.util.Map;

public abstract class Encoding {

    public static final byte GOLOMB = 0;
    public static final byte ELIAS_GAMMA = 1;
    public static final byte FIBONACCI = 2;
    public static final byte UNARY = 3;
    public static final byte DELTA = 4;

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
        }
        throw new RuntimeException("Encoding not found: " + identifier);
    }

    public String getName() {
        switch (getIdentifier()) {
            case GOLOMB:
                return "GOLOMB(K=" + Byte.toUnsignedInt(getArg()) + ")";
            case ELIAS_GAMMA:
                return "ELIAS_GAMMA";
            case FIBONACCI:
                return "FIBONACCI";
            case DELTA:
                return "DELTA(leapBits=" + getArg() + ")";
            case UNARY:
                return "UNARY(stopBit=" + getArg() + ")";
        }
        throw new RuntimeException("Encoding not found: " + getIdentifier());
    }

    public abstract void encodeByte(BitWriter writer, byte value);

    public abstract byte decodeByte(BitReader reader) throws EndOfFileException;

    public void encodeByte(Map<Integer, Integer> dictionary, BitWriter writer, byte value) {
        encodeByte(writer, dictionary.get(Byte.toUnsignedInt(value)).byteValue());
    }

    public void encodeByteArray(BitWriter writer, byte[] bytes) {
        for (byte b : bytes) {
            encodeByte(writer, b);
        }
    }

    public void encodeByteArray(Map<Integer, Integer> dictionary, BitWriter writer, byte[] bytes) {
        for (byte b : bytes) {
            encodeByte(dictionary, writer, b);
        }
    }

    public void encodeStream(Map<Integer, Integer> dictionary, BitWriter writer, BitReader bitReader) throws EndOfFileException {
        while (true) {
            byte read = bitReader.readByte();
            encodeByte(dictionary, writer, read);
        }
    }

    public void encodeStream(BitWriter writer, BitReader bitReader) throws EndOfFileException {
        while (true) {
            byte read = bitReader.readByte();
            encodeByte(writer, read);
        }
    }

    public static int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
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

}
