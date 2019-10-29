package com.example.lukas.multiplicationtablelearner.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lukas on 26/12/2016.
 */

public final class NumberGenerator {
    private static List<Integer> digits = new ArrayList<>(Arrays.asList(0));
    public static final int MAX_DIGIT_VALUE = 9;
    private static final int MAX_SIZE = 3;

    public static boolean addDigit(int digit) {
        int size = digits.size();
        if(size == MAX_SIZE || (size == 1 && digit == 0))
            return false;
        if(size > 0)
            digits.add(size - 1, digit);
        return true;
    }

    public static void removeDigit() {
        int size = digits.size();
        if(size == 1) {
            resetDigits();
        } else {
            digits.remove(size-2);
        }
    }

    public static void resetDigits() {
        digits.clear();
        digits.add(0);
    }

    public static Integer getPreviousDigit() {
        int size = digits.size();
        if(size > 1) {
            return digits.get(size-2);
        } else {
            return 0;
        }
    }

    public static Integer getNumber() {
        int asInteger = 0;
        try {
            asInteger = Integer.parseInt(getNumberAsString());
        } catch (NumberFormatException ex){

        }
        return asInteger;
    }

    public static String getNumberAsString() {
        String str = digits.toString().replace("[", "")
                .replace("]", "").replace(",", "").replace(" ", "");

        return str;
    }
}
