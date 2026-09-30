package com.vgp.utils;

import java.util.Arrays;

public class ArrayUtils {
    public static <T> void printArray(T[][] array) {
        for(T[] row : array) {
            System.out.println();
            printArray(row);
        }
    }
    public static <T> void printArray(T[] array) {
        System.out.println(Arrays.deepToString(array));
    }
    public static <T> String getArray(T[][] array) {
        String result = "";
        for(T[] row : array) {
            result += getArray(row);
        }
        return  result;
    }
    public static <T> String getArray(T[] array) {
        return Arrays.deepToString(array);
    }
}
