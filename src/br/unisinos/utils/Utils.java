package br.unisinos.utils;

public class Utils {

    public static String switchFileExtension(String filePath, String newExtension) {
        int lastPeriod = filePath.lastIndexOf('.');
        lastPeriod = lastPeriod != -1 ? lastPeriod : filePath.length();

        String fileNameWithoutExtension = filePath.substring(0, lastPeriod);

        return fileNameWithoutExtension + '.' + newExtension;
    }

}
