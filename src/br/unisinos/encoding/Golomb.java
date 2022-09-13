package br.unisinos.encoding;

import br.unisinos.exception.EndOfStreamException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

public class Golomb extends Encoding {

    private int divisor;
    private Unary unary;

    private int prefixCount;
    private boolean countingPrefix;

    public Golomb(int divisor) {
        this.divisor = divisor;
        this.unary = new Unary();
        countingPrefix = true;
    }

    @Override
    public byte getIdentifier() {
        return Encoding.GOLOMB;
    }

    @Override
    public byte getArg() {
        return (byte) divisor;
    }

    @Override
    public void encodeByte(BitWriter writer, byte value) {
        int intValue = Byte.toUnsignedInt(value);

        int prefix = intValue / divisor;
        this.unary.encodeUnsignedByte(writer, (byte) prefix, true);

        int suffix = intValue % divisor;
        writer.writeBinary(suffix, suffixLength());
    }

    private int suffixLength() {
        return log2(divisor);
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
                byte suffix = reader.readBits(suffixLength());
                int decodedByte = (prefixCount * divisor + suffix);
                countingPrefix = true;
                prefixCount = 0;
                return (byte) decodedByte;
            }
        }
    }

}
