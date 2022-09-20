package br.unisinos.errorDetection;

public class CRC8 {
    private static final int POLYNOMIAL = 0x107;

    public static byte calculate(Byte[] bytes) {
        byte crc = 0;
        for (byte b : bytes) {
            crc ^= b;
            for (int j = 0; j < 8; j++) {
                if ((crc >> 7) == 0) {
                    crc <<= 1;
                } else {
                    crc = (byte) (crc << 1 ^ POLYNOMIAL);
                }
            }
        }
        return crc;
    }

}
