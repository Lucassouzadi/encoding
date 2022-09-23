package br.unisinos;

import br.unisinos.errorDetection.CRC8;
import br.unisinos.errorDetection.Hamming74;
import br.unisinos.utils.Properties;
import br.unisinos.utils.Utils;

import java.io.File;
import java.lang.reflect.Field;

public class Main {

    public static Properties PROPERTIES;

    public static void main(String[] args) throws IllegalAccessException {
        setProperties(args);

        Encoder encoder = new Encoder(PROPERTIES.encoding, PROPERTIES.arg);
        Encoder decoder = new Encoder();
        switch (PROPERTIES.action) {
            case "encode":
                encoder.encodeAndEncryptFile(PROPERTIES.file);
                break;
            case "decode":
                decoder.decryptAndDecodeFile(PROPERTIES.file);
                break;
            case "encodeAndDecode":
                encoder.encodeAndEncryptFile(PROPERTIES.file);
                decoder.decryptAndDecodeFile(Utils.switchFileExtension(PROPERTIES.file, "ecc"));
                break;
            case "testHamming":
                Hamming74.test();
                break;
            case "testCRC":
                CRC8.test();
                break;
            default:
                System.out.println("Ação \"" + PROPERTIES.action + "\" não conhecida");
        }
    }

    private static void setProperties(String[] args) throws IllegalAccessException {
        PROPERTIES = new Properties();
        for (String arg : args) {
            int separatorIndex = arg.indexOf(':');
            if (separatorIndex != -1) {
                String key = arg.substring(0, separatorIndex);
                String value = arg.substring(separatorIndex + 1);
                PROPERTIES.setProp(key, value);
            }
        }

        System.out.println("---Parâmetros aplicados:---");
        for (Field field : Properties.class.getFields()) {
            System.out.println("\t" + field.getName() + ":" + field.get(PROPERTIES));
        }
        System.out.println("---------------------------");

        if (PROPERTIES.file == null) {
            System.out.println("Informe o caminho de um arquivo por meio do parâmetro \"file\", por exemplo \"file:alice29.txt\"");
        } else if (!new File(PROPERTIES.file).exists()) {
            System.out.println(PROPERTIES.file + " não encontrado");
        }
    }

}
