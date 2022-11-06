package com.company;

import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    //Двухступенчатый генератор псевдослучайных чисел
    //1. 17-разрядный линейный сдвиговый регистр генерирует 32 двоичных разряда
    //2. конгруэнтный генератор с модулем 2^32 генерирует фрагмент гаммы длиной 28 байт
    //32-символьная кодовая таблица -> один символ - 5 бит
    public static HashMap<Character, String> codeTable = new HashMap<>();
    public static final int BLOCK_SIZE = 32;
    public static final int BITS_PER_LETTER = 5;
    private static String message;
    private static String binaryMessage;
    private static String[] blocks;
    public static void main(String[] args) {
        makeCodeTable();
        try {

            readMessageFromFile("message.txt");
            binaryMessage = convertMessageToBinary();
            System.out.println(message + " - исходное сообщение");
            makeBlocks();

            LinearShiftRegister firstStage = new LinearShiftRegister("key.txt");
            StringBuilder text = new StringBuilder();
            for(int i = 0; i < blocks.length; i++) {
                System.out.println((i+1) + "-й блок, первая ступень:");
                String secondStageInput = firstStage.makeGamma(blocks[i]);
                System.out.println(secondStageInput + " - гамма для входа второй ступени");
                System.out.println("Вторая ступень:");
                String gammaPart = LinearCongruentGenerator.makeGamma(secondStageInput);
                System.out.println("гамма шифра:\n" + gammaPart);
                System.out.println(blocks[i] + " - блок № " + (i + 1));
                String encodedBinary = encode(gammaPart, blocks[i]);
                System.out.println(encodedBinary + " - зашифрованный блок №" + (i + 1));
                String encodedText = convertBinaryToText(encodedBinary);
                System.out.println(encodedText + " - текст блока № " + (i + 1));
                text.append(encodedText);
            }
            System.out.println(text + " - полный текст");
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
            codeTable.put((char)i, v.substring(v.length() - BITS_PER_LETTER));
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

    private static String convertBinaryToText(String binary) {
        while (binary.length() % BITS_PER_LETTER != 0) {
            binary += "0";
        }
        int lettersCount = binary.length() / BITS_PER_LETTER;
        StringBuilder text = new StringBuilder();
        Collection<Character> letters = codeTable.keySet();
        for(int i = 0; i < lettersCount; i++) {
            String binaryLetter = binary.substring(0, BITS_PER_LETTER);
            binary = binary.substring(BITS_PER_LETTER);
            for(Character letter: letters) {
                String code = codeTable.get(letter);
                if(code.equals(binaryLetter)) {
                    text.append(letter);
                }
            }
        }
        return text.toString();
    }

    private static void makeBlocks() {
        String binaryMessageCopy = binaryMessage;
        int blocksCount = binaryMessageCopy.length() / BLOCK_SIZE;
        blocks = new String[blocksCount];
        for(int i = 0; i < blocksCount; i++) {
            blocks[i] = binaryMessageCopy.substring(0, BLOCK_SIZE);
            binaryMessageCopy = binaryMessageCopy.substring(BLOCK_SIZE);
        }
    }

    private static String encode(String gamma, String block) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < block.length(); i++) {
            char tmp = charXOR(block.charAt(i), gamma.charAt(i));
            sb.append(tmp);
        }
        return sb.toString();
    }

    private static char charXOR(char first, char second) {
        if(first == second)
            return '0';
        else return '1';
    }


}
