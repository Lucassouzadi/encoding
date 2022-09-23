package br.unisinos.errorDetection;

public class CRC8 {

    private static final int POLYNOMIAL = 0b100000111; // x^8 + x^2 + x + 1

    public static byte calculate(Byte[] bytes) {
        byte crc8 = 0;
        for (byte b : bytes) {
            crc8 ^= b;
            for (int j = 0; j < 8; j++) {
                if (crc8 >> 7 == 0) {
                    crc8 <<= 1;
                } else {
                    crc8 = (byte) (crc8 << 1 ^ POLYNOMIAL);
                }
            }
        }
        return crc8;
    }

    public static void test() {
        System.out.println("Testes de CRC8");
        test(new Byte[]{0x57});
        test(new Byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39});
    }

    private static void test(Byte[] bytes) {
        byte crc = CRC8.calculate(bytes);

        System.out.printf("CRC8(0x%02X", bytes[0]);
        if (bytes.length > 1) {
            for (int i = 1; i < bytes.length; i++) {
                System.out.printf(", 0x%02X", bytes[i]);
            }
        }
        System.out.println("): " + String.format("0x%02X", Byte.toUnsignedInt(crc)));
    }


}
