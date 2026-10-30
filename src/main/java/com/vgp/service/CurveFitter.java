package com.vgp.service;

import java.awt.geom.Point2D;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.vgp.dto.Cubic;

public class CurveFitter {

    /**
     * @param points point sequences to fit
     * @return the fitted cubic Bezier curves
     */
    public static List<Cubic> fit(List<List<Point2D.Double>> points) {
        for (List<Point2D.Double> contour : points) {
            double[] u_i = chordLengthParameterize(contour);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * distance formula : d(p1.p2) = sqrt((x2-x1)^2 + (y2-y1)^2)
     *
     * @param points ordered samples to parameterize
     * @return one normalized parameter per sample, from 0 to 1
     */
    public static double @Nullable [] chordLengthParameterize(List<Point2D.Double> points) {
        if (points.isEmpty())
            return null;
        int count = points.size();
        double[] d_i = new double[count];
        double[] c_i = new double[count];
        double[] u_i = new double[count];
        u_i[0] = c_i[0] = d_i[0] = 0;

        for (int i = 1; i < count; i++) {
            Point2D.Double P1 = points.get(i - 1);
            Point2D.Double P2 = points.get(i);

            double d_x = P2.x - P1.x;
            double d_y = P2.y - P1.y;

            d_i[i] = Math.sqrt((d_x * d_x) + (d_y * d_y));
        }
        for (int i = 1; i < count; i++) {
            c_i[i] = c_i[i - 1] + d_i[i];
        }

        for (int i = 1; i < count; i++) {
            u_i[i] = c_i[i] / c_i[count - 1];
        }

        return u_i;
    }

    /**
     * T0 = normalize(p1-p0)
     * => dx = (p1.x-p0.x)
     * => dy = (p1.y-p0.y)
     * => q = sqrt(dx^2 + dy^2)
     * => T0 = (dx/q , dy/q)
     * 
     * @param points list of points
     * @param i      index of starting point
     * @return point of tangent
     */
    public static Point2D.Double leftTangent(List<Point2D.Double> points, int i) {
        Point2D.Double P0 = points.get(i);
        Point2D.Double P1 = points.get(i+1);

        double d_x = P1.x - P0.x;
        double d_y = P1.y - P0.y;

        double q = Math.sqrt((d_x*d_x) + (d_y*d_y));

        return new Point2D.Double(d_x/q, d_y/q);
    }

    /**
     * T0 = normalize(p2-p3)
     * => dx = (p2.x-p3.x)
     * => dy = (p2.y-p3.y)
     * => q = sqrt(dx^2 + dy^2)
     * => T0 = (dx/q , dy/q)
     * 
     * @param points list of points
     * @param i      index of ending point
     * @return point of tangent
     */
    public static Point2D.Double rightTangent(List<Point2D.Double> points, int i) {
        Point2D.Double P3 = points.get(i);
        Point2D.Double P2 = points.get(i-1);

        double d_x = P2.x - P3.x;
        double d_y = P2.y - P3.y;

        double q = Math.sqrt((d_x*d_x) + (d_y*d_y));

        return new Point2D.Double(d_x/q, d_y/q);
    }

}
