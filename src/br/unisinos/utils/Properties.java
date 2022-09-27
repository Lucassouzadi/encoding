package br.unisinos.utils;

import java.lang.reflect.Field;

public class Properties {

    public String action = "encodeAndDecode";
    public String file = "alice29.txt";
    public int encoding = 0;
    public int arg = 8;
    public boolean useDictionary = true;

    public boolean logHistogram = false;

    public void setProp(String key, String value) {
        try {
            Field field = Properties.class.getDeclaredField(key);
            switch (field.getType().toString()) {
                case "boolean":
                    field.set(this, Boolean.valueOf(value));
                    break;
                case "int":
                    field.set(this, Integer.valueOf(value));
                    break;
                default:
                    field.set(this, value);
                    break;
            }
        } catch (NoSuchFieldException e) {
            System.err.printf("Parâmetro desconhecido: \"%s\"\n", key);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
