package com.vgp.UnitTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.vgp.utils.ArrayUtils;

public class GaussianBlur {
    @Test
    public void blur(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("src\\test\\java\\com\\vgp\\UnitTests\\resource\\test-image-path-grayscale-map")));
            Object o = ois.readObject();
            float[][] image = (float[][]) o;
            java.awt.image.BufferedImage preview = new java.awt.image.BufferedImage(image[0].length, image.length, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < image.length; y++) {
                for (int x = 0; x < image[0].length; x++) {
                    int gray = Math.round(image[y][x] * 255);
                    gray = Math.max(0, Math.min(255, gray));
                    preview.getRaster().setSample(x, y, 0, gray);
                }
            }
            ImageIO.write(preview, "png", new File("target/gaussianBlurUnitTestInput.png"));
            int h = image.length;
            int w = image[0].length;
            Float[][] boxedImage = new Float[h][w];
            for(int y=0;y<h;y++) {
                for(int x=0;x<w;x++) {
                    boxedImage[y][x] = image[y][x];
                }
            }
            Float[] flattenedImage = ArrayUtils.normalize2DArray(boxedImage);
            int pixelCount = flattenedImage.length;
            float[] pixels = new float[pixelCount];
            for(int i=0;i<pixelCount;i++) {
                pixels[i] = flattenedImage[i];
            } 
            float[] blurred = com.vgp.service.GaussianBlur.blur(pixels, image[0].length, image.length, 5);
            for (int y = 0; y < image.length; y++) {
                for (int x = 0; x < image[0].length; x++) {
                    int gray = Math.round(blurred[y * image[0].length + x] * 255);
                    gray = Math.max(0, Math.min(255, gray));
                    preview.getRaster().setSample(x, y, 0, gray);
                }
            }
            ImageIO.write(preview, "png", new File("target/gaussianBlurUnitTestResult.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
