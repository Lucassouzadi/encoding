package br.unisinos.encoding;

import br.unisinos.exception.EndOfFileException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

public class Delta extends Encoding {

    private int leapSizeBits; // leap size in bits (not considering stop bit)

    private Byte previousByte;

    public Delta(int leapSize) {
        this.leapSizeBits = leapSize;
    }

    @Override
    public byte getIdentifier() {
        return Encoding.DELTA;
    }

    @Override
    public byte getArg() {
        return (byte) this.leapSizeBits;
    }

    @Override
    public void encodeByte(BitWriter writer, byte value) {
        if (previousByte == null) {
            writer.writeByte(value);
            previousByte = value;
            return;
        }

        int intValue = Byte.toUnsignedInt(value);
        int lastIntValue = Byte.toUnsignedInt(previousByte);
        previousByte = value;

        int delta = intValue - lastIntValue;

        if (delta == 0) {
            writer.writeBit(false);
        } else {
            writer.writeBit(true);
            writer.writeBit(delta < 0); // sign bit
            writer.writeBinary(Math.abs(delta) - 1, leapSizeBits);
        }
    }

    @Override
    public byte decodeByte(BitReader reader) throws EndOfFileException {
        if (previousByte == null) {
            previousByte = reader.readByte();
            return previousByte;
        }
        boolean stepped = reader.readBit();
        if (stepped) {
            boolean sign = reader.readBit();
            int step = reader.readBits(leapSizeBits) + 1;
            int decodedByte = sign ? previousByte - step : previousByte + step;
            previousByte = (byte) decodedByte;
        }
        return previousByte;
    }

    public void setLeapSizeBits(int maxLeap) {
        this.leapSizeBits = log2(maxLeap) + 1;
    }

}
