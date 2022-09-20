package br.unisinos;

import br.unisinos.errorDetection.Hamming74;
import br.unisinos.utils.Properties;
import br.unisinos.utils.Utils;

import java.io.File;
import java.lang.reflect.Field;

public class Main {

    public static Properties PROPERTIES;

    public static void main(String[] args) throws IllegalAccessException {
        setProperties(args);

        if (PROPERTIES.testHamming) {
            Hamming74.testeRuido();
        }

        if (PROPERTIES.action.equals("encode")) {
            Encoder encoder = new Encoder(PROPERTIES.encoding, PROPERTIES.arg);
            encoder.encodeFile(PROPERTIES.file);
        } else if (PROPERTIES.action.equals("decode")) {
            Encoder decoder = new Encoder();
            decoder.decodeFile(PROPERTIES.file);
        } else if (PROPERTIES.action.equals("encodeAndDecode")) {
            Encoder encoder = new Encoder(PROPERTIES.encoding, PROPERTIES.arg);
            encoder.encodeFile(PROPERTIES.file);
            Encoder decoder = new Encoder();
            decoder.decodeFile(Utils.switchFileExtension(PROPERTIES.file, "ecc"));
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

        System.out.println("---Utilizando os seguintes parâmetros:---");
        for (Field field : Properties.class.getFields()) {
            System.out.println("\t" + field.getName() + ":" + field.get(PROPERTIES));
        }
        System.out.println("-----------------------------------------");

        if (PROPERTIES.file == null) {
            System.out.println("Informe o caminho de um arquivo por meio do parâmetro \"file\", por exemplo \"file:C:/alice29.txt\"");
            return;
        } else if (!new File(PROPERTIES.file).exists()) {
            System.out.println(PROPERTIES.file + " não encontrado");
            return;
        }
    }

}
