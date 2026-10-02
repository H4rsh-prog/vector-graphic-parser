package com.vgp.UnitTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

public class BinaryMasker {
    @Test 
    public void mask(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("src\\test\\java\\com\\vgp\\UnitTests\\resource\\test-image-path-grayscale-map")));
            Object o = ois.readObject();
            float[][] image = (float[][]) o;
            boolean[][] mask = com.vgp.service.BinaryMasker.mask(image, (float) 0.3);
            java.awt.image.BufferedImage preview = new java.awt.image.BufferedImage(mask[0].length, mask.length, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < mask.length; y++) {
                for (int x = 0; x < mask[0].length; x++) {
                    int gray = (mask[y][x]?255:0);
                    preview.getRaster().setSample(x, y, 0, gray);
                }
            }
            ImageIO.write(preview, "png", new File("target/binaryMaskTest.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
}
