package com.vgp.UnitTests;


import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

public class ImageReader {
    @Test
    public void readImage(){
        com.vgp.service.ImageReader reader = new com.vgp.service.ImageReader();
        BufferedImage result = reader.readImage(null);
        assertNotEquals(result, null);
    }
}
