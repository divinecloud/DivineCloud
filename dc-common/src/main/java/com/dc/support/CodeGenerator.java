/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
