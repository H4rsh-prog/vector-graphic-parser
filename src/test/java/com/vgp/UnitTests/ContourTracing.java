package com.vgp.UnitTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
			System.out.println(Arrays.deepToString(mask));
			System.out.println(contour);
			for(List<java.awt.geom.Point2D.Double> loop : contour) {
				System.out.println(String.format(Locale.ROOT, "LOOP %s ---------------", loop.toString()));
				for(java.awt.geom.Point2D.Double point : loop) {
					System.out.println(String.format(Locale.ROOT, "{ %.2f, %.2f}", point.x, point.y));
				}
				System.out.println(String.format(Locale.ROOT, "LOOP %s ---------------", loop.toString()));
			}
            String SVG = ArrayUtils.serializePolylineToSVG(contour, 300, 300);
            FileWriter fw = new FileWriter(new File("target/contourTracingTestOutput.svg"));
            fw.write(SVG);
            fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
