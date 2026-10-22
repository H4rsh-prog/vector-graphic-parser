package com.vgp.UnitTests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vgp.utils.ArrayUtils;

public class RDPSimplifier {
    @Test 
    public void simplify(){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("src\\test\\java\\com\\vgp\\UnitTests\\resource\\test-image-path-binary-mask")));
             FileWriter fw = new FileWriter(new File("target/RDPSimplificationTestOutput.svg"))) {
            Object o = ois.readObject();
            boolean[][] mask = (boolean[][]) o;
            List<List<java.awt.geom.Point2D.Double>> contour = com.vgp.service.ContourTracing.trace(mask);
            String comparatorSVG = ArrayUtils.serializePolylineToSVG(contour, 300, 300);
            contour = com.vgp.service.RDPSimplifier.simplify(contour, 0.55f);
            String SVG = ArrayUtils.serializePolylineToSVG(contour, 300, 300);
            fw.write(SVG);
            assertNotEquals(SVG, comparatorSVG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
