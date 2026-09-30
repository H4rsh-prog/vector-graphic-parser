package com.vgp.service;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ValueMapper {
    public static float[][] createGrayscaleMap(BufferedImage image) {
        int H = image.getHeight();
        int W = image.getWidth();
        float[][] result = new float[H][W];
        for(int y=0;y<H;y++) {
            for(int x=0;x<W;x++) {
                Color color = new Color(image.getRGB(x, y));
                result[y][x] = (float) (
                    (color.getGreen() * 0.71) +
                    (color.getRed() * 0.22) +
                    (color.getBlue() * 0.07)
                ) /255;
            }
        }
        return result;
    }
}
