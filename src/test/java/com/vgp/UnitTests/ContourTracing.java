package com.vgp.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vgp.utils.ArrayUtils;


public class ContourTracing {
	@Test
	public void trace() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("src\\test\\java\\com\\vgp\\UnitTests\\resource\\test-image-path-binary-mask")));
            Object o = ois.readObject();
            boolean[][] mask = (boolean[][]) o;
            List<List<java.awt.geom.Point2D.Double>> contour = com.vgp.service.ContourTracing.trace(mask);
            String SVG = ArrayUtils.serializePolylineToSVG(contour, 300, 300);
            FileReader fr = new FileReader(new File("src\\test\\java\\com\\vgp\\UnitTests\\resource\\test-image-path-unblurred-contour"));
            String test_file = "";
			while(fr.ready()) {
				test_file += (char) fr.read();
			}
			System.out.println(SVG.equals(test_file));
            assertEquals(test_file, SVG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
