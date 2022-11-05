package com.company;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    //Двухступенчатый генератор псевдослучайных чисел
    //1. 17-разрядный линейный сдвиговый регистр генерирует 32 двоичных разряда
    //2. конгруэнтный генератор с модулем 2^32 генерирует фрагмент гаммы длиной 28 байт
    //32-символьная кодовая таблица -> один символ - 5 бит
    public static HashMap<Character, String> codeTable = new HashMap<>();
    public static final int BLOCK_SIZE = 32;
    private static String message;
    private static String binaryMessage;
    private static String[] blocks;
    public static void main(String[] args) {
        makeCodeTable();
        try {
            readMessageFromFile("message.txt");
            binaryMessage = convertMessageToBinary();
            System.out.println(message + " - исходное сообщение");
            System.out.println(binaryMessage + " - сообщение в двоичном виде");
            makeBlocks();

            LinearShiftRegister firstStage = new LinearShiftRegister("key.txt");
            String secondStageInput = firstStage.makeGamma(blocks[0]);
            System.out.println(secondStageInput);


        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void makeCodeTable() {
        int currentValue = 0;
        for (int i = 'а'; i <= 'я'; i++) {
            StringBuilder v = new StringBuilder("00000");
            v.append(Integer.toBinaryString(currentValue));
            codeTable.put((char)i, v.substring(v.length() - 5));
            currentValue++;
        }
    }

    private static void readMessageFromFile(String filename) {
        try(FileReader fr = new FileReader(filename)) {
            Scanner scanner = new Scanner(fr);
            message = scanner.nextLine();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String convertMessageToBinary() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < message.length(); i++) {
            String code = codeTable.getOrDefault(message.charAt(i), "404");
            if(!code.equals("404")) {
                sb.append(code);
            }
        }
        while(sb.length() % BLOCK_SIZE != 0) {
            sb.append("0");
        }
        return sb.toString();
    }

    private static void makeBlocks() {
        int blocksCount = binaryMessage.length() / BLOCK_SIZE;
        blocks = new String[blocksCount];
        for(int i = 0; i < blocksCount; i++) {
            blocks[i] = binaryMessage.substring(0, BLOCK_SIZE);
            binaryMessage = binaryMessage.substring(BLOCK_SIZE);
        }
    }
}
