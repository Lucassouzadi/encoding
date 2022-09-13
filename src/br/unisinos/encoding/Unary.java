package br.unisinos.encoding;

import br.unisinos.exception.EndOfStreamException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

public class Unary extends Encoding {

    private final boolean stopBit;
    private int count;

    public Unary() {
        this.stopBit = true;
        this.count = 0;
    }

    public Unary(boolean stopBit) {
        this.stopBit = stopBit;
    }

    @Override
    public void encodeByte(BitWriter writer, byte value) {
        encodeUnsignedByte(writer, value, true);
    }

    @Override
    public byte decodeByte(BitReader reader) throws EndOfStreamException {
        while (true) {
            boolean bit = reader.readBit();
            if (bit == stopBit) {
                break;
            }
            count++;
        }
        byte decodedByte = (byte) count;
        count = 0;
        return decodedByte;
    }

    @Override
    public byte getIdentifier() {
        return UNARY;
    }

    @Override
    public byte getArg() {
        return (byte) (stopBit ? 1 : 0);
    }

    @Override
    public boolean getFillupBit() {
        return !stopBit;
    }

    public void encodeUnsignedByte(BitWriter writer, byte value, boolean useStopBit) {
        int intValue = Byte.toUnsignedInt(value);
        for (int i = 0; i < intValue; i++)
            writer.writeBit(!stopBit);
        if (useStopBit)
            writer.writeBit(stopBit);
    }


}
