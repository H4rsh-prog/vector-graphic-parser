package com.vgp.UnitTests;


import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.image.BufferedImage;
import java.io.File;

import org.junit.jupiter.api.Test;

import io.github.cdimascio.dotenv.Dotenv;

public class ImageReader {
    @Test
    public void readImage(){
        String filePath = Dotenv.configure().load().get("test-image-path");
        BufferedImage result = com.vgp.service.ImageReader.readImage(new File(filePath));
        assertNotEquals(result, null);
    }
}
