package com.company;

import java.math.BigInteger;

public class LinearCongruentGenerator {
    private static final BigInteger a = new BigInteger("1664525");
    private static final BigInteger b = new BigInteger("3");
    private static final BigInteger m = new BigInteger("4294967296");
    private static final int LENGTH = 28 * 8;

    public static String makeGamma(String input) {
        long decimalInput = Long.parseLong(input, 2);
        BigInteger previousY = new BigInteger(decimalInput + "");
        StringBuilder gamma = new StringBuilder();
        while (gamma.length() < LENGTH) {
            BigInteger Y = (a.multiply(previousY).add(b)).mod(m);//(a * Yi-1 + b) mod m
            String binaryY = Y.toString(2);
            gamma.append(binaryY);
        }
        return gamma.substring(0, LENGTH);
    }
}
