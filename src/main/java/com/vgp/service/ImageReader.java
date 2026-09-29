package com.vgp.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageReader {
    public BufferedImage readImage(File path){
        BufferedImage result = null;
        try {
            result = ImageIO.read(path);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
