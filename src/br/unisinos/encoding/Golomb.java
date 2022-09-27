package br.unisinos.encoding;

import br.unisinos.exception.EndOfFileException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

public class Golomb extends Encoding {

    private final Unary unary;
    private final int divisor;

    private int prefixCount;
    private boolean countingPrefix;

    public Golomb(int divisor) {
        this.divisor = divisor;
        this.unary = new Unary();
        this.countingPrefix = true;
    }

    @Override
    public byte getIdentifier() {
        return Encoding.GOLOMB;
    }

    @Override
    public byte getArg() {
        return (byte) this.divisor;
    }

    @Override
    public void encodeByte(BitWriter writer, byte value) {
        int intValue = Byte.toUnsignedInt(value);

        int prefix = intValue / divisor;
        unary.encodeUnsignedByte(writer, (byte) prefix);

        int suffix = intValue % divisor;
        writer.writeBinary(suffix, suffixLength());
    }

    private int suffixLength() {
        return (int) Math.ceil(Math.log(divisor) / Math.log(2));
    }

    @Override
    public byte decodeByte(BitReader reader) throws EndOfFileException {
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
