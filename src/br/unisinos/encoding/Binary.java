package br.unisinos.encoding;

import br.unisinos.exception.EndOfStreamException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

public class Binary extends Encoding {

    public void encodeByte(BitWriter writer, byte value) {
        writer.writeByte(value);
    }

    @Override
    public byte decodeByte(BitReader reader) throws EndOfStreamException {
        return reader.readByte();
    }

    @Override
    public byte getIdentifier() {
        return BINARY;
    }

}
