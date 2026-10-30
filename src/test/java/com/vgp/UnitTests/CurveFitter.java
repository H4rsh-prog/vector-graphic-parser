package com.vgp.UnitTests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    
    @Test
    void computesNormalizedLeftTangentInForwardDirection() {
	List<Point2D.Double> points = List.of(
		new Point2D.Double(1, 1),
		new Point2D.Double(4, 5),
		new Point2D.Double(9, 5));

	Point2D.Double tangent = com.vgp.service.CurveFitter.leftTangent(points, 0);

	assertEquals(0.6, tangent.x, 1e-12);
	assertEquals(0.8, tangent.y, 1e-12);
    }

    @Test
    void computesNormalizedRightTangentTowardPreviousPoint() {
	List<Point2D.Double> points = List.of(
		new Point2D.Double(1, 1),
		new Point2D.Double(4, 5),
		new Point2D.Double(9, 5));

	Point2D.Double tangent = com.vgp.service.CurveFitter.rightTangent(points, points.size() - 1);

	assertEquals(-1.0, tangent.x, 1e-12);
	assertEquals(0.0, tangent.y, 1e-12);
    }

	@Test
	void solvesSymmetricHandleLengthsForAnArch() {
		List<Point2D.Double> points = List.of(
				new Point2D.Double(0, 0),
				new Point2D.Double(0.5, 0.5),
				new Point2D.Double(1, 0));
		double[] parameters = {0.0, 0.5, 1.0};
		double diagonal = 1.0 / Math.sqrt(2.0);
		Point2D.Double leftTangent = new Point2D.Double(diagonal, diagonal);
		Point2D.Double rightTangent = new Point2D.Double(-diagonal, diagonal);

		double[] handles = com.vgp.service.CurveFitter.leastSquaresHandleLengths(
				points, parameters, leftTangent, rightTangent);

		double expectedHandle = 2.0 * Math.sqrt(2.0) / 3.0;
		assertArrayEquals(new double[] {expectedHandle, expectedHandle}, handles, 1e-12);
	}

	@Test
	void fallsBackToOneThirdOfTheEndpointDistanceForCollinearSamples() {
		List<Point2D.Double> points = List.of(
				new Point2D.Double(0, 0),
				new Point2D.Double(1, 0),
				new Point2D.Double(2, 0));
		double[] parameters = {0.0, 0.5, 1.0};
		Point2D.Double leftTangent = new Point2D.Double(1, 0);
		Point2D.Double rightTangent = new Point2D.Double(-1, 0);

		double[] handles = com.vgp.service.CurveFitter.leastSquaresHandleLengths(
				points, parameters, leftTangent, rightTangent);

		assertArrayEquals(new double[] {2.0 / 3.0, 2.0 / 3.0}, handles, 1e-12);
	}
}
