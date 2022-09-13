package br.unisinos.encoding;

import br.unisinos.exception.EndOfStreamException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

public class Delta extends Encoding {

    private int maxLeap;
    private int leapLength;

    private Byte lastByte;

    public Delta(int maxLeap) {
        this.setMaxLeap(maxLeap);
    }

    @Override
    public byte getIdentifier() {
        return Encoding.DELTA;
    }

    @Override
    public byte getArg() {
        return (byte) maxLeap;
    }

    @Override
    public void encodeByte(BitWriter writer, byte value) {
        if (lastByte == null) {
            writer.writeByte(value);
            lastByte = value;
            return;
        }

        int intValue = Byte.toUnsignedInt(value);
        int lastIntValue = Byte.toUnsignedInt(lastByte);
        lastByte = value;

        int delta = intValue - lastIntValue;

        if (delta == 0) {
            writer.writeBit(false);
        } else {
            writer.writeBit(true);
            writer.writeBit(delta < 0);
            writer.writeBinary(Math.abs(delta) - 1, leapLength);
        }
    }

    @Override
    public byte decodeByte(BitReader reader) throws EndOfStreamException {
        // TODO: treat tail fillup bits
        while (true) {
            if (lastByte == null) {
                lastByte = reader.readByte();
                return lastByte;
            }
            boolean stepped = reader.readBit();
            if (stepped) {
                boolean sign = reader.readBit();
                int step = reader.readBits(leapLength) + 1;
                int decodedByte = sign ? lastByte - step : lastByte + step;
                lastByte = (byte) decodedByte;
            }
            return lastByte;
        }
    }

    public void setMaxLeap(int maxLeap) {
        this.leapLength = log2(maxLeap) + 1;
        this.maxLeap = (int) Math.pow(2, this.leapLength) - 1;
        this.lastByte = null;
    }

}
