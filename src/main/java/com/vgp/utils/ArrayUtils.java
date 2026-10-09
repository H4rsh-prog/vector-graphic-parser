package com.vgp.utils;

import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
    public static String serializePolylineToSVG(List<List<java.awt.geom.Point2D.Double>> polyline, int width, int height) {
    	StringBuilder SVGTag = new StringBuilder(String.format(Locale.ROOT,"<svg xmlns=\"https://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\">%n"
    												+ "<rect width=\"%d\" height=\"%d\" fill=\"white\" />%n", width, height, width, height, width, height));
    	for(List<Point2D.Double> loop : polyline) {
    		if(loop.isEmpty()) continue;
    		StringBuilder d = new StringBuilder();
    		Point2D.Double first = loop.get(0);
    		d.append(String.format(Locale.ROOT, "M %.2f %.2f", first.x, first.y));
    		int loopSize = loop.size();
    		for(int i=1;i<loopSize;i++) {
    			Point2D.Double p = loop.get(i);
    			d.append(String.format(Locale.ROOT, "L %.2f %.2f", p.x, p.y));
    		}
    		d.append("Z");
    		SVGTag.append(String.format(Locale.ROOT, " <path d=\"%s\" fill=\"none\" stroke=\"black\" stroke-width=\"0.5\"/> %n", d));
    	}
    	SVGTag.append("</svg>");
    	return SVGTag.toString();
    }
}