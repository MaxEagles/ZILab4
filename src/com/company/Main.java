package com.company;

import javax.sound.midi.Soundbank;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

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
            encodeDecode("message.txt", "encode.txt");

            encodeDecode("encode.txt", "decode.txt");
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

    private static void readMessageFromFile(String filename) throws Exception{
        try(FileReader fr = new FileReader(filename)) {
            Scanner scanner = new Scanner(fr);
            message = scanner.nextLine();
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
        return sb.toString();
    }

    private static String convertBinaryToText(String binary) {
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
        if(binaryMessageCopy.length() % BLOCK_SIZE != 0)
            blocksCount++;
        blocks = new String[blocksCount];
        for(int i = 0; i < blocksCount - 1; i++) {
            blocks[i] = binaryMessageCopy.substring(0, BLOCK_SIZE);
            binaryMessageCopy = binaryMessageCopy.substring(BLOCK_SIZE);
        }
        blocks[blocksCount - 1] = binaryMessageCopy;
    }

    private static String encodeBlock(String gamma, String block) {
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

    private static void encodeDecode(String messageFileName, String destinationFileName) throws Exception {
        prepareMessage(messageFileName);
        LinearShiftRegister firstStage = new LinearShiftRegister("key.txt");
        StringBuilder binary = new StringBuilder();
        for (String block : blocks) {
            String secondStageInput = firstStage.makeGamma();
            System.out.println(secondStageInput + " - вход второй ступени");
            binary.append(secondStage(secondStageInput, block));
        }
        String text = convertBinaryToText(binary.toString());
        System.out.println(text + " - текст");
        writeToFile( destinationFileName, text);
    }

    private static void prepareMessage(String messageFileName) throws Exception {
        readMessageFromFile(messageFileName);
        System.out.println(message + " - исходное сообщение");
        binaryMessage = convertMessageToBinary();
        System.out.println(binaryMessage + " - сообщение в двоичном виде");
        makeBlocks();
    }

    private static String secondStage(String secondStageInput, String block) {
        String gamma = LinearCongruentGenerator.makeGamma(secondStageInput);
        System.out.println("гамма шифра:");
        System.out.println(gamma);
        System.out.println(block + " - блок");
        String encodedBinary = encodeBlock(gamma, block);
        System.out.println(encodedBinary + " - результат");
        return encodedBinary;
    }

    private static void writeToFile(String filename, String text) throws Exception {
        try(FileWriter fw = new FileWriter(filename)) {
            fw.write(text);
        }
    }
}
