package br.unisinos.encoding;

import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

import java.io.IOException;

public class Binary extends Encoding {

    public void encodeByte(BitWriter writer, byte value) throws IOException {
        writer.writeByte(value);
    }

    @Override
    public byte decodeByte(BitReader reader) throws IOException {
        return reader.readByte();
    }

    @Override
    public byte getIdentifier() {
        return BINARY;
    }

}
