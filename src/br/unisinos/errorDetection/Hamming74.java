package br.unisinos.errorDetection;

public class Hamming74 {

    public boolean[] parityBits(boolean[] value) {
        boolean[] parity = new boolean[3];

        boolean s1 = value[0];
        boolean s2 = value[1];
        boolean s3 = value[2];
        boolean s4 = value[3];

        boolean t5 = s1 ^ s2 ^ s3;
        boolean t6 = s2 ^ s3 ^ s4;
        boolean t7 = s3 ^ s4 ^ s1;

        parity[0] = t5;
        parity[1] = t6;
        parity[2] = t7;

        return parity;
    }

    public void correctCodeword(boolean[] s, boolean[] t) {
        boolean s1 = s[0];
        boolean s2 = s[1];
        boolean s3 = s[2];
        boolean s4 = s[3];

        boolean t5 = (s1 ^ s2 ^ s3);
        boolean t6 = (s2 ^ s3 ^ s4);
        boolean t7 = (s3 ^ s4 ^ s1);

        boolean t5Check = t5 == t[0];
        boolean t6Check = t6 == t[1];
        boolean t7Check = t7 == t[2];

        if (!t5Check || !t6Check || !t7Check) {
            System.out.print("Erro detectado ao decriptar codeword ");
            for (boolean bit : s) System.out.print(bit ? 1 : 0);
            for (boolean bit : t) System.out.print(bit ? 1 : 0);
            String culpado = "";
            if (!t5Check && t6Check && !t7Check) {
                culpado = "s1";
                s[0] = !s[0];
            } else if (!t5Check && !t6Check && t7Check) {
                culpado = "s2";
                s[1] = !s[1];
            } else if (!t5Check && !t6Check && !t7Check) {
                culpado = "s3";
                s[2] = !s[2];
            } else if (t5Check && !t6Check && !t7Check) {
                culpado = "s4";
                s[3] = !s[3];
            } else if (!t5Check) {
                culpado = "t5";
                t[0] = !t[0];
            } else if (!t6Check) {
                culpado = "t6";
                t[1] = !t[1];
            } else if (!t7Check) {
                culpado = "t7";
                t[2] = !t[2];
            }
            System.out.print(", bit culpado: " + culpado + " - codeword corrigido: ");
            for (boolean bit : s) System.out.print(bit ? 1 : 0);
            for (boolean bit : t) System.out.print(bit ? 1 : 0);
            System.out.println();
        }
    }

    public static void testeRuido() {
        for (int i = 0; i < 16; i++) {
            boolean s1 = (i >> 3 & 1) == 1;
            boolean s2 = (i >> 2 & 1) == 1;
            boolean s3 = (i >> 1 & 1) == 1;
            boolean s4 = (i & 1) == 1;

            boolean t5 = (s1 ^ s2 ^ s3);
            boolean t6 = (s2 ^ s3 ^ s4);
            boolean t7 = (s3 ^ s4 ^ s1);

            new Hamming74().correctCodeword(new boolean[]{s1, s2, s3, s4}, new boolean[]{t5, t6, t7});
            new Hamming74().correctCodeword(new boolean[]{!s1, s2, s3, s4}, new boolean[]{t5, t6, t7});
            new Hamming74().correctCodeword(new boolean[]{s1, !s2, s3, s4}, new boolean[]{t5, t6, t7});
            new Hamming74().correctCodeword(new boolean[]{s1, s2, !s3, s4}, new boolean[]{t5, t6, t7});
            new Hamming74().correctCodeword(new boolean[]{s1, s2, s3, !s4}, new boolean[]{t5, t6, t7});
            new Hamming74().correctCodeword(new boolean[]{s1, s2, s3, s4}, new boolean[]{!t5, t6, t7});
            new Hamming74().correctCodeword(new boolean[]{s1, s2, s3, s4}, new boolean[]{t5, !t6, t7});
            new Hamming74().correctCodeword(new boolean[]{s1, s2, s3, s4}, new boolean[]{t5, t6, !t7});
            System.out.println();
        }
    }
}
