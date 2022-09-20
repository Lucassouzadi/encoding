package br.unisinos;

import br.unisinos.encoding.Delta;
import br.unisinos.encoding.Encoding;
import br.unisinos.errorDetection.CRC8;
import br.unisinos.exception.EndOfStreamException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;
import br.unisinos.utils.Tuple;
import br.unisinos.utils.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Encoder {

    private Encoding encoding;

    public Encoder() {
    }

    public Encoder(int encodingId, int arg) {
        this.encoding = Encoding.getInstance(encodingId, arg);
    }

    public void getEncoding(BitReader reader) throws EndOfStreamException {
        int encodingIdentifier = Byte.toUnsignedInt(reader.readByte());
        int arg1 = Byte.toUnsignedInt(reader.readByte());
        this.encoding = Encoding.getInstance(encodingIdentifier, arg1);
    }

    public void encodeFile(String filePath) {
        String codedPath = Utils.switchFileExtension(filePath, "cod");
        String encryptedPath = Utils.switchFileExtension(filePath, "ecc");

        encodeFile(filePath, codedPath);
        encryptFile(codedPath, encryptedPath);
    }

    public void decodeFile(String filePath) {
        String decryptedPath = Utils.switchFileExtension(filePath, "dcc");
        String decodedPath = Utils.switchFileExtension(filePath, "dec");

        if (decryptFile(filePath, decryptedPath))
            decodeFile(decryptedPath, decodedPath);
    }

    private void encodeFile(String filePath, String targetPath) {
        try (
                FileInputStream inputStream = new FileInputStream(filePath);
                FileOutputStream outputStream = new FileOutputStream(targetPath);
                BitWriter bitWriter = new BitWriter(outputStream)
        ) {
            BitReader bitReader = new BitReader(inputStream);

            if (encoding instanceof Delta) {
                ((Delta) encoding).setMaxLeap(getMaxLeap(filePath));
            }

            bitWriter.setFillupBit(encoding.getFillupBit());
            encoding.writeHeader(bitWriter);

            if (Main.PROPERTIES.useDictionary) {
                int[] histogram = getHistogram(filePath);
                List<Tuple<Integer, Integer>> sortedHistogram = sortedHistogram(histogram);
                Map<Integer, Integer> dictionary = buildDictionary(sortedHistogram);

                byte range = Integer.valueOf(dictionary.size() - 1).byteValue();
                bitWriter.writeByte(range);

                for (Tuple<Integer, Integer> codeword : sortedHistogram) {
                    bitWriter.writeByte(codeword.getKey().byteValue());
                }
                encoding.encodeStream(dictionary, bitWriter, bitReader);
            } else {
                encoding.encodeStream(bitWriter, bitReader);
            }
        } catch (EndOfStreamException ex) {
            System.out.println(filePath + " encoded to " + targetPath);
        } catch (IOException e) {
            System.out.println("Error while encoding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void encryptFile(String filePath, String targetPath) {
        try (
                FileInputStream inputStream = new FileInputStream(filePath);
                FileOutputStream outputStream = new FileOutputStream(targetPath);
                BitWriter bitWriter = new BitWriter(outputStream)
        ) {
            BitReader bitReader = new BitReader(inputStream);

            getEncoding(bitReader);
            encoding.writeHeader(bitWriter);

            List<Byte> crcBytes = new ArrayList<>();
            crcBytes.add(encoding.getIdentifier());
            crcBytes.add(encoding.getArg());

            if (Main.PROPERTIES.useDictionary) {
                byte dictionarySizeByte = bitReader.readBits(8);
                crcBytes.add(dictionarySizeByte);
                bitWriter.writeByte(dictionarySizeByte);
                for (int i = 0; i <= Byte.toUnsignedInt(dictionarySizeByte); i++) {
                    byte readByte = bitReader.readBits(8);
                    crcBytes.add(readByte);
                    bitWriter.writeByte(readByte);
                }
            }
            bitWriter.writeByte(CRC8.calculate(crcBytes.toArray(new Byte[0])));
            boolean[] hammingShort = new boolean[4];
            while (true) {
                hammingShort[0] = bitReader.readBit();
                hammingShort[1] = bitReader.readBit();
                hammingShort[2] = bitReader.readBit();
                hammingShort[3] = bitReader.readBit();
                bitWriter.writeHammingNibble(hammingShort);
            }
        } catch (EndOfStreamException ex) {
            System.out.println(filePath + " encrypted to " + targetPath);
        } catch (IOException e) {
            System.out.println("Error while encrypting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean decryptFile(String filePath, String targetPath) {
        try (
                FileInputStream inputStream = new FileInputStream(filePath);
                FileOutputStream outputStream = new FileOutputStream(targetPath);
                BitWriter bitWriter = new BitWriter(outputStream);
        ) {
            BitReader bitReader = new BitReader(inputStream);
            getEncoding(bitReader);
            encoding.writeHeader(bitWriter);

            List<Byte> crcBytes = new ArrayList<>();
            crcBytes.add(encoding.getIdentifier());
            crcBytes.add(encoding.getArg());

            if (Main.PROPERTIES.useDictionary) {
                byte dictionarySizebyte = bitReader.readBits(8);
                crcBytes.add(dictionarySizebyte);
                bitWriter.writeByte(dictionarySizebyte);
                for (int i = 0; i <= Byte.toUnsignedInt(dictionarySizebyte); i++) {
                    byte readByte = bitReader.readBits(8);
                    crcBytes.add(readByte);
                    bitWriter.writeByte(readByte);
                }
            }

            byte readCrc = bitReader.readBits(8);
            byte calculatedCrc = CRC8.calculate(crcBytes.toArray(new Byte[0]));

            if (readCrc != calculatedCrc) {
                System.out.println("Erro de validação de CRC para " + filePath + " - calculado: " + Byte.toUnsignedInt(calculatedCrc) + ", lido: " + Byte.toUnsignedInt(readCrc));
                return false;
            }

            while (true) {
                boolean[] hammingCodeword = new boolean[7];
                hammingCodeword[0] = bitReader.readBit();
                hammingCodeword[1] = bitReader.readBit();
                hammingCodeword[2] = bitReader.readBit();
                hammingCodeword[3] = bitReader.readBit();
                hammingCodeword[4] = bitReader.readBit();
                hammingCodeword[5] = bitReader.readBit();
                hammingCodeword[6] = bitReader.readBit();
                bitWriter.writeHammingCodeword(hammingCodeword);
            }
        } catch (EndOfStreamException ex) {
            System.out.println(filePath + " decrypted to " + targetPath);
        } catch (IOException e) {
            System.out.println("Error while decrypting: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void decodeFile(String filePath, String targetPath) {
        try (
                FileInputStream inputStream = new FileInputStream(filePath);
                FileOutputStream outputStream = new FileOutputStream(targetPath);
                BitWriter bitWriter = new BitWriter(outputStream);
        ) {
            BitReader bitReader = new BitReader(inputStream);
            getEncoding(bitReader);

            Map<Integer, Integer> dictionary = new HashMap<>();
            if (Main.PROPERTIES.useDictionary) {
                int range = Byte.toUnsignedInt(bitReader.readBits(8)) + 1;
                for (int i = 0; i < range; i++)
                    dictionary.put(i, Byte.toUnsignedInt(bitReader.readBits(8)));
            }

            while (true) {
                byte decodedByte = encoding.decodeByte(bitReader);
                if (Main.PROPERTIES.useDictionary) {
                    decodedByte = dictionary.get(Byte.toUnsignedInt(decodedByte)).byteValue();
                }
                bitWriter.writeByte(decodedByte);
            }
        } catch (EndOfStreamException ex) {
            System.out.println(filePath + " decoded to " + targetPath);
        } catch (IOException e) {
            System.out.println("Error while decoding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int[] getHistogram(String filePath) {
        int[] histogram = new int[256];
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            int read;
            while ((read = inputStream.read()) != -1) {
                histogram[read]++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public int getMaxLeap(String filePath) {
        int maxLeap = 0;
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            int lastRead, read = inputStream.read();
            lastRead = read;
            while (read != -1) {
                int leap = Math.abs(read - lastRead);
                maxLeap = Math.max(leap, maxLeap);
                lastRead = read;
                read = inputStream.read();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return maxLeap;
    }

}
