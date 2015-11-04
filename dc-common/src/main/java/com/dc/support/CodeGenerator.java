package com.dc.support;

import java.util.Random;

public class CodeGenerator {
    private static Random random = new Random(System.currentTimeMillis());

    public static long generateRandomNumeric(int length) {
        char[] digits = new char[12];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits).trim());
    }

    public static String generateRandomAlphaNumeric(int length) {
        char[] result = new char[length];
        int c = 'A';
        for (int p = 0; p < length; p++) {
            int num = random.nextInt(2);
            switch (num) {
            case 0:
                c = '0' + random.nextInt(10);
                break;
            case 1:
                c = 'A' + random.nextInt(26);
                break;
            }
            result[p] = (char) c;
        }
        return new String(result);
    }

    public static String generateRandomAlphabets(int length) {
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            char c = (char) (random.nextInt(26) + 'a');
            boolean changeCase = random.nextBoolean();
            result[i] = changeCase ? Character.toUpperCase(c) : c;
        }
        return new String(result);
    }
}
