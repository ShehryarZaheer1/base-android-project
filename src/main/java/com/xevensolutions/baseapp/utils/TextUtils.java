package com.xevensolutions.baseapp.utils;

public class TextUtils {

    public static boolean isStringEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean compareStrings(String stringOne, String stringTwo) {
        if (stringOne == null || stringTwo == null)
            return false;
        return stringOne.equals(stringTwo);
    }

}
