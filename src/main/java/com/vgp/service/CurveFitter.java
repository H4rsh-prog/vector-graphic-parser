package com.vgp.service;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.vgp.dto.Cubic;

public class CurveFitter {

    /**
     * @param points point sequences to fit
     * @return the fitted cubic Bezier curves
     */
    public static List<Cubic> fit(List<List<Point2D.Double>> points){
        throw new UnsupportedOperationException();
    }

    /**
     * distance formula : d(p1.p2) = sqrt((x2-x1)^2 + (y2-y1)^2)
     *
     * @param points ordered samples to parameterize
     * @return one normalized parameter per sample, from 0 to 1
     */
    public static double @Nullable [] chordLengthParameterize(List<Double> points) {
        if(points.isEmpty()) return null;
        int count = points.size();
        double[] d_i = new double[count];
        double[] c_i = new double[count];
        double[] u_i = new double[count];
        u_i[0] = c_i[0] = d_i[0] = 0;

        for(int i=1; i<count; i++) {
            Point2D.Double P1 = points.get(i-1);
            Point2D.Double P2 = points.get(i);

            double d_x = P2.x - P1.x;
            double d_y = P2.y - P1.y;

            d_i[i] = Math.sqrt((d_x*d_x) + (d_y*d_y));
        }
        for(int i=1; i<count; i++) {
            c_i[i] = c_i[i-1] + d_i[i];
        }

        for(int i=1; i<count;i++) {
            u_i[i] = c_i[i] / c_i[count-1];
        }

        return u_i;
    }

}
