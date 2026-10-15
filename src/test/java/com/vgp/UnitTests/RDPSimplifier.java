package com.vgp.UnitTests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vgp.utils.ArrayUtils;

public class RDPSimplifier {
    @Test 
    public void simplify(){
        try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("src\\test\\java\\com\\vgp\\UnitTests\\resource\\test-image-path-binary-mask")));
            Object o = ois.readObject();
            boolean[][] mask = (boolean[][]) o;
            List<List<java.awt.geom.Point2D.Double>> contour = com.vgp.service.ContourTracing.trace(mask);
            String comparatorSVG = ArrayUtils.serializePolylineToSVG(contour, 300, 300);
            contour = com.vgp.service.RDPSimplifier.simplify(contour, 0.55f);
            String SVG = ArrayUtils.serializePolylineToSVG(contour, 300, 300);
            FileWriter fw = new FileWriter(new File("target/RDPSimplificationTestOutput.svg"));
            fw.write(SVG);
            fw.close();
            assertNotEquals(SVG, comparatorSVG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
