package br.unisinos;

import br.unisinos.encoding.Encoding;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Encoder {

    private Encoding encoding;

    public Encoder() {
    }

    public Encoder(String[] args) {
        this.encoding = Encoding.getInstance(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    public void getEncoding(BitReader reader) throws IOException {
        byte encodingIdentifier = reader.readByte();
        byte arg1 = reader.readByte();
        this.encoding = Encoding.getInstance(encodingIdentifier, arg1);
    }

    public void encodeFile(String filePath, String targetPath) {
        try (
                FileInputStream inputStream = new FileInputStream(filePath);
                FileOutputStream outputStream = new FileOutputStream(targetPath);
                BitWriter bitWriter = new BitWriter(outputStream)
        ) {
            bitWriter.setFillupBit(encoding.getFillupBit());
            encoding.writeHeader(bitWriter);

//            byte[] byteArray = new byte[]{2, 3, 0, 1, 1, 6, 2, 4, 4, 4, 1, 3, 5, 2};
//            encoding.encodeByteArray(bitWriter, byteArray);
            encoding.encodeStream(bitWriter, inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void decodeFile(String filePath, String targetPath) {
        try (
                FileInputStream inputStream = new FileInputStream(filePath);
                FileOutputStream outputStream = new FileOutputStream(targetPath);
                BitWriter bitWriter = new BitWriter(outputStream);
        ) {
            BitReader bitReader = new BitReader(inputStream);
            getEncoding(bitReader);

            while (true)
                bitWriter.writeByte(encoding.decodeByte(bitReader));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
