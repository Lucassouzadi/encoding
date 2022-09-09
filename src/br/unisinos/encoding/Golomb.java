package br.unisinos.encoding;

import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

import java.io.IOException;

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
    public void encodeByte(BitWriter writer, byte value) throws IOException {
        int intValue = Byte.toUnsignedInt(value);

        int prefix = intValue / divisor;
        this.unary.encodeUnsignedByte(writer, (byte) prefix, false);

        writer.writeBit(true);

        int suffix = intValue % divisor;
        writer.writeBinary(suffix, suffixLength());
    }

    private int suffixLength() {
        return (int) (Math.log(divisor) / Math.log(2));
    }

    @Override
    public byte decodeByte(BitReader reader) throws IOException {
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
