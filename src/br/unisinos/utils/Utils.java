package br.unisinos.utils;

public class Utils {

    public static String switchFileExtension(String filePath, String newExtension) {
        int lastIndexOfPeriod = filePath.lastIndexOf('.');
        lastIndexOfPeriod = lastIndexOfPeriod != -1 ? lastIndexOfPeriod : filePath.length();

        String fileNameWithoutExtension = filePath.substring(0, lastIndexOfPeriod);

        return fileNameWithoutExtension + '.' + newExtension;
    }

}
