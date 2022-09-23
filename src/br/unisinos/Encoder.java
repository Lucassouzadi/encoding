package br.unisinos;

import br.unisinos.encoding.Delta;
import br.unisinos.encoding.Encoding;
import br.unisinos.errorDetection.CRC8;
import br.unisinos.exception.EndOfFileException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;
import br.unisinos.utils.Tuple;
import br.unisinos.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static br.unisinos.Main.PROPERTIES;

public class Encoder {

    private Encoding encoding;

    public Encoder() {
    }

    public Encoder(int encodingId, int arg) {
        this.encoding = Encoding.getInstance(encodingId, arg);
    }

    public void readEncodingFromFile(BitReader reader) throws EndOfFileException {
        int encodingIdentifier = Byte.toUnsignedInt(reader.readByte());
        int arg = Byte.toUnsignedInt(reader.readByte());
        this.encoding = Encoding.getInstance(encodingIdentifier, arg);
    }

    public void encodeAndEncryptFile(String filePath) {
        File origin = new File(filePath);
        File encoded = new File(Utils.switchFileExtension(filePath, "cod"));
        File encrypted = new File(Utils.switchFileExtension(filePath, "ecc"));

        encodeFile(origin, encoded);
        encryptFile(encoded, encrypted);

        printSizes(origin, encoded, encrypted);
    }

    public void decryptAndDecodeFile(String filePath) {
        File origin = new File(filePath);
        File decrypted = new File(Utils.switchFileExtension(filePath, "dcc"));
        File decoded = new File(Utils.switchFileExtension(filePath, "dec"));

        if (decryptFile(origin, decrypted))
            decodeFile(decrypted, decoded);

        printSizes(origin, decrypted, decoded);
    }

    private void encodeFile(File origin, File target) {
        try (
                FileInputStream inputStream = new FileInputStream(origin);
                FileOutputStream outputStream = new FileOutputStream(target);
                BitWriter bitWriter = new BitWriter(outputStream)
        ) {
            BitReader bitReader = new BitReader(inputStream);

            if (encoding instanceof Delta) {
                ((Delta) encoding).setLeapSizeBits(getMaxLeap(origin));
            }

            bitWriter.setFillupBit(encoding.getFillupBit());
            encoding.writeHeader(bitWriter);

            if (PROPERTIES.useDictionary) {
                int[] histogram = getHistogram(origin);
                List<Tuple<Integer, Integer>> sortedHistogram = sortedHistogram(histogram);
                Map<Integer, Integer> dictionary = buildDictionary(sortedHistogram);

                logHistogramAndDictionary(histogram, sortedHistogram);

                bitWriter.writeByte((byte) (dictionary.size() - 1));

                for (Tuple<Integer, Integer> codeword : sortedHistogram) {
                    bitWriter.writeByte(codeword.getKey().byteValue());
                }
                encoding.encodeStream(dictionary, bitWriter, bitReader);
            } else {
                encoding.encodeStream(bitWriter, bitReader);
            }
        } catch (EndOfFileException ex) {
            System.out.printf("%s encoded to %s using %s\n", origin.getAbsolutePath(), target.getAbsolutePath(), encoding.getName());
        } catch (IOException e) {
            throw new RuntimeException("Error while encoding " + origin.getAbsolutePath(), e);
        }
    }

    private void encryptFile(File origin, File target) {
        try (
                FileInputStream inputStream = new FileInputStream(origin);
                FileOutputStream outputStream = new FileOutputStream(target);
                BitWriter bitWriter = new BitWriter(outputStream)
        ) {
            BitReader bitReader = new BitReader(inputStream);

            List<Byte> crcBytes = new ArrayList<>();
            skipHeaders(crcBytes, bitReader, bitWriter);

            bitWriter.writeByte(CRC8.calculate(crcBytes.toArray(new Byte[0])));
            boolean[] hammingNibble = new boolean[4];
            while (true) {
                hammingNibble[0] = bitReader.readBit();
                hammingNibble[1] = bitReader.readBit();
                hammingNibble[2] = bitReader.readBit();
                hammingNibble[3] = bitReader.readBit();
                bitWriter.encodeHammingNibble(hammingNibble);
            }
        } catch (EndOfFileException ex) {
            System.out.printf("%s encrypted to %s using CRC8 for header and Hamming(7,4) for data\n",
                    origin.getAbsolutePath(),
                    target.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error while encrypting " + origin.getAbsolutePath(), e);
        }
    }

    private boolean decryptFile(File origin, File target) {
        try (
                FileInputStream inputStream = new FileInputStream(origin);
                FileOutputStream outputStream = new FileOutputStream(target);
                BitWriter bitWriter = new BitWriter(outputStream);
        ) {
            BitReader bitReader = new BitReader(inputStream);

            List<Byte> crcBytes = new ArrayList<>();
            skipHeaders(crcBytes, bitReader, bitWriter);

            byte headerCrc = bitReader.readBits(8);
            byte calculatedCrc = CRC8.calculate(crcBytes.toArray(new Byte[0]));

            if (headerCrc != calculatedCrc) {
                System.out.printf("Erro de validação de CRC para %s - calculado: 0x%02X, lido: 0x%02X\n",
                        origin.getAbsolutePath(),
                        Byte.toUnsignedInt(calculatedCrc),
                        Byte.toUnsignedInt(headerCrc));
                return false;
            }

            boolean[] hammingCodeword = new boolean[7];
            while (true) {
                hammingCodeword[0] = bitReader.readBit();
                hammingCodeword[1] = bitReader.readBit();
                hammingCodeword[2] = bitReader.readBit();
                hammingCodeword[3] = bitReader.readBit();
                hammingCodeword[4] = bitReader.readBit();
                hammingCodeword[5] = bitReader.readBit();
                hammingCodeword[6] = bitReader.readBit();
                bitWriter.decodeHammingCodeword(hammingCodeword);
            }
        } catch (EndOfFileException ex) {
            System.out.printf("%s decrypted to %s validating CRC8 on header and Hamming(7,4) for data\n",
                    origin.getAbsolutePath(),
                    target.getAbsolutePath());
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error while decrypting " + origin.getAbsolutePath(), e);
        }
    }

    private void skipHeaders(List<Byte> crcBytes, BitReader bitReader, BitWriter bitWriter) throws EndOfFileException {
        readEncodingFromFile(bitReader);
        encoding.writeHeader(bitWriter);

        crcBytes.add(encoding.getIdentifier());
        crcBytes.add(encoding.getArg());

        if (PROPERTIES.useDictionary) {
            byte dictionarySizebyte = bitReader.readBits(8);
            crcBytes.add(dictionarySizebyte);
            bitWriter.writeByte(dictionarySizebyte);
            for (int i = 0; i <= Byte.toUnsignedInt(dictionarySizebyte); i++) {
                byte readByte = bitReader.readBits(8);
                crcBytes.add(readByte);
                bitWriter.writeByte(readByte);
            }
        }
    }

    private void decodeFile(File origin, File target) {
        try (
                FileInputStream inputStream = new FileInputStream(origin);
                FileOutputStream outputStream = new FileOutputStream(target);
                BitWriter bitWriter = new BitWriter(outputStream);
        ) {
            BitReader bitReader = new BitReader(inputStream);
            readEncodingFromFile(bitReader);

            Map<Integer, Integer> dictionary = new HashMap<>();
            if (PROPERTIES.useDictionary) {
                int dictionarySize = Byte.toUnsignedInt(bitReader.readBits(8)) + 1;
                for (int i = 0; i < dictionarySize; i++)
                    dictionary.put(i, Byte.toUnsignedInt(bitReader.readBits(8)));
            }

            while (true) {
                byte decodedByte = encoding.decodeByte(bitReader);
                if (PROPERTIES.useDictionary) {
                    decodedByte = dictionary.get(Byte.toUnsignedInt(decodedByte)).byteValue();
                }
                bitWriter.writeByte(decodedByte);
            }
        } catch (EndOfFileException ex) {
            System.out.printf("%s decoded to %s using %s\n", origin.getAbsolutePath(), target.getAbsolutePath(), encoding.getName());
        } catch (IOException e) {
            throw new RuntimeException("Error while decoding " + origin.getAbsolutePath(), e);
        }
    }

    public int[] getHistogram(File file) throws IOException {
        int[] histogram = new int[256];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int read;
            while ((read = inputStream.read()) != -1) {
                histogram[read]++;
            }
        }
        return histogram;
    }

    private List<Tuple<Integer, Integer>> sortedHistogram(int[] histogram) {
        AtomicInteger i = new AtomicInteger(0);
        List<Tuple<Integer, Integer>> sortedHistogram = Arrays.stream(histogram)
                .mapToObj(v -> new Tuple<>(i.getAndIncrement(), v))
                .filter(t -> t.getValue() > 0)
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());
        Collections.reverse(sortedHistogram);
        return sortedHistogram;
    }

    private Map<Integer, Integer> buildDictionary(List<Tuple<Integer, Integer>> sortedHistogram) {
        Map<Integer, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < sortedHistogram.size(); i++) {
            dictionary.put(sortedHistogram.get(i).getKey(), i);
        }
        return dictionary;
    }

    private int getMaxLeap(File file) throws IOException {
        int maxLeap = 0;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int currentByte = inputStream.read();
            int previousByte = currentByte;
            while (currentByte != -1) {
                int leap = Math.abs(currentByte - previousByte);
                maxLeap = Math.max(leap, maxLeap);
                previousByte = currentByte;
                currentByte = inputStream.read();
            }
        }
        return maxLeap;
    }

    private void printSizes(File... files) {
        if (PROPERTIES.logFileSizes && files.length > 0) {
            System.out.printf("%d bytes", files[0].length());
            for (int i = 1; i < files.length; i++) {
                System.out.printf(" --> %d bytes", files[i].length());
            }
            System.out.println();
        }
    }

    private void logHistogramAndDictionary(int[] histogram, List<Tuple<Integer, Integer>> sortedHistogram) {
        if (PROPERTIES.logHistogram) {
            System.out.println("Histograma completo:");
            for (int i = 0; i < histogram.length; i++) {
                System.out.printf("\t[%c] 0x%02X (%d): %d ocorrências\n", i, i, i, histogram[i]);
            }
            System.out.printf("\nDicionário resultante(tamanho=%d):\n", sortedHistogram.size());
            for (int i = 0; i < sortedHistogram.size(); i++) {
                int key = sortedHistogram.get(i).getKey();
                int value = sortedHistogram.get(i).getValue();
                System.out.printf("\t[%c] 0x%02X, com %d ocorrências, mapeado para 0x%02X\n", key, key, value, i);
            }
            System.out.println();
        }
    }

}
