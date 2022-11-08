package com.company;

import java.io.FileReader;
import java.util.Scanner;

public class LinearShiftRegister {
    private static final int DIGITS = 17;
    private static final int GAMMA_SIZE = 32;
    private String cells;
    private String links;

    public LinearShiftRegister(String keyFilename) throws Exception {
        readKeyFromFile(keyFilename);
    }

    public String makeGamma() {
        StringBuilder gamma = new StringBuilder();
        for(int i = 0; i < GAMMA_SIZE; i++) {
            System.out.println(cells + " - состояние регистра № " + (i + 1));
            gamma.append(performStep(cells.charAt(cells.length() - 1)));
        }
        return gamma.toString();
    }

    private char performStep(char bit) {
        for(int i = cells.length() - 2; i >= 0; i--) {
            if(links.charAt(i) == '1') {
                bit = charXOR(cells.charAt(i), bit);
            }
        }
        cells = bit + cells.substring(0, DIGITS - 1);
        return bit;
    }

    private char charXOR(char first, char second) {
        if(first == second)
            return '0';
        else return '1';
    }

    private void readKeyFromFile(String filename) throws Exception {
        try(FileReader fr = new FileReader(filename)) {
            Scanner scanner = new Scanner(fr);
            cells =  scanner.nextLine();
            if (cells.length() < DIGITS)
                throw new Exception("Недостаточно разрядов регистра!");
            cells = cells.substring(0, DIGITS);
            checkLine(cells);
            links = scanner.nextLine();
            if (links.length() < DIGITS - 1)
                throw new Exception("Недостаточно связей регистра!");
            links = links.substring(0, DIGITS - 1);
            checkLine(links);
        }
    }

    private void checkLine(String line) throws Exception {
        for(int i = 0; i < DIGITS; i++) {
            if(cells.charAt(i) != '0' && cells.charAt(i) != '1') {
                throw new Exception("Ключ должен содержать только нули и единицы!");
            }
        }
    }
}
