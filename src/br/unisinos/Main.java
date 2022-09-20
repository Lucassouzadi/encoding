package br.unisinos;

public class Main {

    public static void main(String[] args) {
//        Properties
//        Hamming74.testeRuido();
        Encoder encoder = new Encoder(args);
        encoder.encodeFile("C:\\git\\alice29.txt");
        encoder.encodeFile("C:\\git\\sum");
        encoder.encodeFile("C:\\git\\teste.txt");

        Encoder decoder = new Encoder();
        decoder.decodeFile("C:\\git\\alice29.ecc");
        decoder.decodeFile("C:\\git\\sum.ecc");
        decoder.decodeFile("C:\\git\\teste.ecc");
    }

}
