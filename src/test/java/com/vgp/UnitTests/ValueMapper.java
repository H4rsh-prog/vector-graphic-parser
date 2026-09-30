package com.vgp.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

import com.vgp.service.ImageReader;
import com.vgp.utils.ArrayUtils;

import io.github.cdimascio.dotenv.Dotenv;

public class ValueMapper {
    @Test 
    public void createGrayscaleMap(){
        String filePath = Dotenv.configure().load().get("test-image-path");
        BufferedImage image = ImageReader.readImage(new File(filePath));
        float[][] map = com.vgp.service.ValueMapper.createGrayscaleMap(image);
        ArrayUtils.printArray(map);
    }
}
