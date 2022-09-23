package br.unisinos.encoding;

import br.unisinos.exception.EndOfFileException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

public class EliasGamma extends Encoding {

    private final Unary unary;

    private int prefixCount;
    private boolean isCounting;

    public EliasGamma() {
        this.unary = new Unary();
        this.isCounting = true;
    }

    @Override
    public byte getIdentifier() {
        return Encoding.ELIAS_GAMMA;
    }

    @Override
    public void encodeByte(BitWriter writer, byte value) {
        int intValue = Byte.toUnsignedInt(value) + 1;

        int prefixLength = log2(intValue);
        unary.encodeUnsignedByte(writer, (byte) prefixLength);

        int suffix = intValue - (int) Math.pow(2, prefixLength);
        writer.writeBinary(suffix, prefixLength);
    }

    @Override
    public byte decodeByte(BitReader reader) throws EndOfFileException {
        while (true) {
            if (isCounting) {
                boolean bit = reader.readBit();
                if (!bit)
                    prefixCount++;
                else
                    isCounting = false;
            } else {
                byte suffix = reader.readBits(prefixCount);
                int decodedByte = (int) Math.pow(2, prefixCount) + suffix - 1;
                isCounting = true;
                prefixCount = 0;
                return (byte) decodedByte;
            }
        }
    }

}
