package br.unisinos.encoding;

import br.unisinos.exception.EndOfStreamException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

public class EliasGamma extends Encoding {

    private Unary unary;

    private int prefixCount;
    private boolean countingPrefix;

    public EliasGamma() {
        this.unary = new Unary();
        countingPrefix = true;
    }

    @Override
    public byte getIdentifier() {
        return Encoding.ELIAS_GAMMA;
    }

    @Override
    public void encodeByte(BitWriter writer, byte value) {
        int intValue = Byte.toUnsignedInt(value) + 1;

        int prefixLength = log2(intValue);
        this.unary.encodeUnsignedByte(writer, (byte) prefixLength, true);

        int suffix = intValue - (int) Math.pow(2, prefixLength);
        writer.writeBinary(suffix, prefixLength);
    }

    @Override
    public byte decodeByte(BitReader reader) throws EndOfStreamException {
        while (true) {
            if (countingPrefix) {
                boolean bit = reader.readBit();
                if (!bit)
                    prefixCount++;
                else
                    countingPrefix = false;
            } else {
                byte suffix = reader.readBits(prefixCount);
                int decodedByte = (int) Math.pow(2, prefixCount) + suffix - 1;
                countingPrefix = true;
                prefixCount = 0;
                return (byte) decodedByte;
            }
        }
    }

}
