package br.unisinos;

import br.unisinos.encoding.Delta;
import br.unisinos.encoding.Encoding;
import br.unisinos.exception.EndOfStreamException;
import br.unisinos.stream.BitReader;
import br.unisinos.stream.BitWriter;
import br.unisinos.utils.Tuple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Encoder {

    private final boolean useDictionary = true;
    private Encoding encoding;

    public Encoder() {
    }

    public Encoder(String[] args) {
        this.encoding = Encoding.getInstance(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    public void getEncoding(BitReader reader) throws EndOfStreamException {
        int encodingIdentifier = Byte.toUnsignedInt(reader.readByte());
        int arg1 = Byte.toUnsignedInt(reader.readByte());
        this.encoding = Encoding.getInstance(encodingIdentifier, arg1);
    }

    public void encodeFile(String filePath, String targetPath) {
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

            if (useDictionary) {
                int[] histogram = getHistogram(filePath);
                List<Tuple<Integer, Integer>> sortedHistogram = sortedHistogram(histogram);
                Map<Integer, Integer> dictionary = buildDictionary(sortedHistogram);

                bitWriter.writeByte(Integer.valueOf(dictionary.size() - 1).byteValue());

                for (Tuple<Integer, Integer> codeword : sortedHistogram) {
                    bitWriter.writeByte(codeword.getKey().byteValue());
                }

                encoding.encodeStream(dictionary, bitWriter, bitReader);
            } else {
                encoding.encodeStream(bitWriter, bitReader);
            }
        } catch (EndOfStreamException ex) {
            System.out.println("File encoded successfully");
        } catch (IOException e) {
            System.out.println("Error while encoding file");
            e.printStackTrace();
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

            Map<Integer, Integer> dictionary = new HashMap<>();
            if (useDictionary) {
                int range = Byte.toUnsignedInt(bitReader.readBits(8)) + 1;
                for (int i = 0; i < range; i++)
                    dictionary.put(i, Byte.toUnsignedInt(bitReader.readBits(8)));
            }

            while (true) {
                byte decodedByte = encoding.decodeByte(bitReader);
                if (useDictionary) {
                    decodedByte = dictionary.get(Byte.toUnsignedInt(decodedByte)).byteValue();
                }
                bitWriter.writeByte(decodedByte);
            }
        } catch (EndOfStreamException ex) {
            System.out.println("File decoded successfully");
        } catch (IOException e) {
            System.out.println("Error while decoding file");
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
