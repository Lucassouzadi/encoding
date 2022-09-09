package br.unisinos;

public class Main {

    public static void main(String[] args) {
        Encoder encoder = new Encoder(args);
        encoder.encodeFile("C:\\git\\alice29.txt", "C:\\git\\alice29.cod");
        encoder.encodeFile("C:\\git\\sum", "C:\\git\\sum.cod");

        Encoder decoder = new Encoder();
        decoder.decodeFile("C:\\git\\alice29.cod", "C:\\git\\alice29.dec");
        decoder.decodeFile("C:\\git\\sum.cod", "C:\\git\\sum.dec");
    }

}
