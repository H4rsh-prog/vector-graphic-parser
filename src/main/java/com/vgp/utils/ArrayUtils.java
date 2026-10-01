package com.vgp.utils;

import java.lang.reflect.Array;
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
    public static <T> T[] normalize2DArray(T[][] array) {
        int h = array.length;
        int w = array[0].length;
        Class<?> clazz = array.getClass().getComponentType().getComponentType();
        T[] normalizedArray = (T[]) Array.newInstance(clazz, w*h);
        int indx = 0;
        for(T[] row : array) {
            for(T n : row) {
                normalizedArray[indx++] = n;
            }
        }
        return normalizedArray;
    }
}