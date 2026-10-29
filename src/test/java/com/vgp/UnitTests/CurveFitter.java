package com.vgp.UnitTests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.geom.Point2D;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CurveFitter {
    @Test
    void parameterizesByCumulativeChordLength() {
	List<Point2D.Double> points = List.of(
		new Point2D.Double(0, 0),
		new Point2D.Double(3, 4),
		new Point2D.Double(6, 4));

	assertArrayEquals(new double[] {0.0, 5.0 / 8.0, 1.0},
		com.vgp.service.CurveFitter.chordLengthParameterize(points), 1e-12);
    }

    @Test
    void returnsNullForEmptyInput() {
	assertNull(com.vgp.service.CurveFitter.chordLengthParameterize(List.of()));
    }

    @Test
    void handlesSinglePointInput() {
	assertArrayEquals(new double[] {0.0},
		com.vgp.service.CurveFitter.chordLengthParameterize(
			List.of(new Point2D.Double(2, 5))), 1e-12);
    }
}
