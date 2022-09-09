package br.unisinos.encoding;

import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Fibonacci extends Encoding {

    private List<Integer> sequence;
    private int count;
    private int decodeBuffer;
    private boolean lastBit;

    public Fibonacci() {
        this.lastBit = false;
        this.count = 0;
        this.decodeBuffer = 0;
        this.sequence = new ArrayList<>();
        this.sequence.add(1);
        this.sequence.add(2);
    }

    @Override
    public byte getIdentifier() {
        return Encoding.FIBONACCI;
    }

    @Override
    public void encodeByte(BitWriter writer, byte value) throws IOException {
        int intValue = Byte.toUnsignedInt(value) + 1;

        int closest = updateSequence(intValue);

        boolean[] bits = new boolean[closest + 1];

        for (int i = bits.length - 1; i >= 0; i--) {
            bits[i] = intValue >= sequence.get(i);
            if (bits[i]) {
                intValue -= sequence.get(i);
            }
        }

        for (boolean bit : bits) {
            writer.writeBit(bit);
        }

        writer.writeBit(true);
    }

    @Override
    public byte decodeByte(BitReader reader) throws IOException {
        while (true) {
            boolean bit = reader.readBit();
            if (bit && lastBit) {
                int decodedByte = decodeBuffer - 1;
                decodeBuffer = 0;
                count = 0;
                lastBit = false;
                return (byte) decodedByte;
            } else {
                if (sequence.size() <= count) {
                    incrementSequence();
                }
                if (bit) {
                    decodeBuffer += sequence.get(count);
                }
                lastBit = bit;
                count++;
            }
        }
    }

    private int updateSequence(int value) {
        while (sequence.get(sequence.size() - 1) < value) {
            incrementSequence();
        }

        int closest = sequence.size() - 1;

        while (sequence.get(closest) > value) closest--;

        return closest;
    }

    private void incrementSequence() {
        int next = sequence.get(sequence.size() - 1) + sequence.get(sequence.size() - 2);
        sequence.add(next);
    }

}
