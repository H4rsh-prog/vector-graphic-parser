package com.vgp.service;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.vgp.dto.Cubic;

public class CurveFitter {

    /**
     * @param points point sequences to fit
     * @return the fitted cubic Bezier curves
     */
    public static List<List<Cubic>> fit(List<List<Point2D.Double>> points, double tolerance) {
        List<List<Cubic>> BezierCurves = new ArrayList<>();
        for(List<Point2D.Double> contour : points) {
            BezierCurves.add(makeCubic(contour, tolerance, null, null));
        }
        return BezierCurves;
    }
    public static List<Cubic> makeCubic(List<Point2D.Double> contour, double tolerance, Point2D.Double T0r, Point2D.Double T1r) {
        int size = contour.size();
        Point2D.Double P0 = contour.get(0);
        Point2D.Double P3 = contour.get(size-1);

        double[] u_i = chordLengthParameterize(contour);
        Point2D.Double T0 = (T0r!=null)? T0r: leftTangent(contour, 0);
        Point2D.Double T1 = (T1r!=null)? T1r: rightTangent(contour, size-1);
        double[] a = leastSquaresHandleLengths(contour, u_i, T0, T1);

        Point2D.Double a0T0 = new Point2D.Double(T0.x*a[0], T0.y*a[0]);
        Point2D.Double a1T1 = new Point2D.Double(T1.x*a[1], T1.y*a[1]);

        Point2D.Double P1 = new Point2D.Double(P0.x+a0T0.x, P0.y+a0T0.y);
        Point2D.Double P2 = new Point2D.Double(P3.x+a1T1.x, P3.y+a1T1.y);

        Cubic C = new Cubic(P0, P1, P2, P3);
        double maxErrorSquared = 0;
        int k = 0;
        for(int i=0; i<size; i++) {
            Point2D.Double P_i = contour.get(i); 
            double[] B = calculateBernsteinWeights(u_i[i]);
            Point2D.Double Cu = new Point2D.Double(
                P0.x*B[0] + P1.x*B[1] + P2.x*B[2] + P3.x*B[3],
                P0.y*B[0] + P1.y*B[1] + P2.y*B[2] + P3.y*B[3]);
            
            double epsilon = Math.pow(P_i.x-Cu.x, 2) + Math.pow(P_i.y-Cu.y, 2);
            if(epsilon>maxErrorSquared) {
                maxErrorSquared = epsilon;
                k = i;
            }
        }
        if(maxErrorSquared>(tolerance*tolerance)) {
            Point2D.Double kMinusOne = contour.get(k-1);
            Point2D.Double kPlusOne = contour.get(k+1);
            double d_x = kMinusOne.x - kPlusOne.x;
            double d_y = kMinusOne.y - kPlusOne.y;

            double q = Math.sqrt((d_x * d_x) + (d_y * d_y));
            if (q == 0) {
                Point2D.Double Pk = contour.get(k);
                d_x = kMinusOne.x - Pk.x;
                d_y = kMinusOne.y - Pk.y;
                q = Math.sqrt((d_x * d_x) + (d_y * d_y));
            }
            if (q == 0) {
                Point2D.Double Pk = contour.get(k);
                d_x = Pk.x - kPlusOne.x;
                d_y = Pk.y - kPlusOne.y;
                q = Math.sqrt((d_x * d_x) + (d_y * d_y));
            }
            if (q == 0) {
                throw new IllegalArgumentException("Cannot compute a center tangent from coincident points");
            }

            Point2D.Double Tc = new Point2D.Double(d_x/q, d_y/q);
            
            List<Cubic> Cubics = makeCubic(contour.subList(0, k+1), tolerance, null, Tc);
            Cubics.addAll(makeCubic(contour.subList(k, size), tolerance, new Point2D.Double(-Tc.x, -Tc.y), null));
            
            return Cubics;
        } else {
            return new ArrayList<Cubic>(List.of(C));
        }
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
        Point2D.Double P1 = points.get(i + 1);

        double d_x = P1.x - P0.x;
        double d_y = P1.y - P0.y;

        double q = Math.sqrt((d_x * d_x) + (d_y * d_y));

        return new Point2D.Double(d_x / q, d_y / q);
    }

    /**
     * T1 = normalize(p2-p3)
     * => dx = (p2.x-p3.x)
     * => dy = (p2.y-p3.y)
     * => q = sqrt(dx^2 + dy^2)
     * => T1 = (dx/q , dy/q)
     * 
     * @param points list of points
     * @param i      index of ending point
     * @return point of tangent
     */
    public static Point2D.Double rightTangent(List<Point2D.Double> points, int i) {
        Point2D.Double P3 = points.get(i);
        Point2D.Double P2 = points.get(i - 1);

        double d_x = P2.x - P3.x;
        double d_y = P2.y - P3.y;

        double q = Math.sqrt((d_x * d_x) + (d_y * d_y));

        return new Point2D.Double(d_x / q, d_y / q);
    }

    /**
     * Solves for the left and right Bezier handle lengths using the supplied
     * chord-length parameters and endpoint tangents.
     *
     * For each parameter u_i calculate Bernstein Weights:
     * B0 = (1-u_i)^3
     * B1 = 3u_i(1-u_i)^2
     * B2 = 3u_i^2(1-u_i)
     * B3 = u_i^3
     * 
     * Then calculate the Error
     * D_i = P_i-((p0*(B0(u_i)+B1(u_i))+(p3*(B2(u_i)+B3(u_i)))) //(raw errors)
     * D_iT0 = D_i(dot)T0 //(error projected on left tangent)
     * D_iT1 = D_i(dot)T1 //(error projected on right tangent)
     * 
     * Then we multiply both weights (Bernstein and Error Tangent)
     * x_0 = sigma(B1(u_i)*D_iT0)
     * x_1 = sigma(B2(u_i)*D_iT1)
     * 
     * Then we create the Normal Matrix
     * N [ c00 , c01
     * c10 , c11 ]
     * c00 = sigma(B1(u_i)^2)
     * c11 = sigma(B2(u_i)^2)
     * c01 (and) c10 = sigma(B1(u_i)*B2(u_i)*(T0(dot)T1))
     * 
     * Then we calculate the determinant
     * det = (c00*c11)-(c01*c01)
     * 
     * Then finally we can calculate the alpha (a) values
     * a_0 = ((c11*x_0)-(c01*x_1))/det
     * a_1 = ((c00*x_1)-(c01*x_0))/det
     * 
     * 
     * If the system is singular or either handle length is non-positive,
     * use a_0=a_1=|p(n-1)-p0|/3.
     *
     * @param points     ordered input samples
     * @param parameters one parameter in [0, 1] per sample
     * @param T0         normalized tangent pointing from the first sample
     *                   toward the next sample
     * @param T1         normalized tangent pointing from the last sample
     *                   toward the previous sample
     * @return handle lengths in the order [alpha_L, alpha_R]
     */
    public static double[] leastSquaresHandleLengths(
            List<Point2D.Double> points,
            double[] u,
            Point2D.Double T0,
            Point2D.Double T1) {
        int uLength = u.length;
        double[][] B = new double[uLength][4];

        for (int i = 0; i < uLength; i++) {
            B[i] = calculateBernsteinWeights(u[i]);
        }
        List<Point2D.Double> D_i = calculateRawError(points, B);
        double[] D_iT0 = new double[uLength];
        double[] D_iT1 = new double[uLength];

        for (int i = 0; i < uLength; i++) {
            Point2D.Double D = D_i.get(i);
            D_iT0[i] = dot(D, T0);
            D_iT1[i] = dot(D, T1);
        }

        double x_0 = 0, x_1 = 0;
        for (int i = 0; i < uLength; i++) {
            x_0 += (B[i][1] * D_iT0[i]);
            x_1 += (B[i][2] * D_iT1[i]);
        }

        double[][] N = createNormalMatrix(B, T0, T1);
        double det = (N[0][0] * N[1][1]) - (N[0][1] * N[1][0]);
        if (!isValidDeterminant(N, det))
            return fallbackForHandleLength(points, uLength);

        double a_0 = ((N[1][1] * x_0) - (N[0][1] * x_1)) / det;
        if (a_0 <= 0)
            return fallbackForHandleLength(points, uLength);
        double a_1 = ((N[0][0] * x_1) - (N[1][0] * x_0)) / det;
        if (a_1 <= 0)
            return fallbackForHandleLength(points, uLength);

        return new double[] { a_0, a_1 };
    }

    // If the system is singular or either handle length is non-positive,
    // use a_0=a_1=sqrt((p(n-1).x-p0.x)^2+(p(n-1).y-p0.y)^2)/3.
    private static double[] fallbackForHandleLength(List<Point2D.Double> points, int n) {
        Point2D.Double P0 = points.get(0);
        Point2D.Double PnMinus1 = points.get(n - 1);
        double D_x = PnMinus1.x - P0.x;
        double D_y = PnMinus1.y - P0.y;

        double a = Math.sqrt((D_x * D_x) + (D_y * D_y)) / 3;

        return new double[] { a, a };
    }

    private static boolean isValidDeterminant(double[][] N, double det) {
        double scale = Math.max(
                Math.abs(N[0][0] * N[1][1]),
                N[0][1] * N[0][1]);

        if (det <= 1e-12 * scale)
            return false;
        return true;
    }

    // B0 = (1-u_i)^3
    // B1 = 3u_i(1-u_i)^2
    // B2 = 3u_i^2(1-u_i)
    // B3 = u_i^3
    private static double[] calculateBernsteinWeights(double u) {
        double[] weight = new double[4];
        double oneMinusU = 1.0 - u;
        double threeU = 3 * u;
        weight[0] = Math.pow(oneMinusU, 3);
        weight[1] = threeU * (oneMinusU * oneMinusU);
        weight[2] = threeU * u * oneMinusU;
        weight[3] = Math.pow(u, 3);
        return weight;
    }

    // D_i = P_i-(p0*(B0(u_i)+B1(u_i))+(p3*(B2(u_i)+B3(u_i))))
    private static List<Point2D.Double> calculateRawError(List<Point2D.Double> points, double[][] B) {
        List<Point2D.Double> error = new ArrayList<>();
        int uLength = B.length;
        Point2D.Double P0 = points.get(0);
        Point2D.Double P3 = points.get(points.size() - 1);

        for (int i = 0; i < uLength; i++) {
            Point2D.Double P_i = points.get(i);
            double B01 = B[i][0] + B[i][1];
            double B23 = B[i][2] + B[i][3];

            Point2D.Double weightedP0 = new Point2D.Double(P0.x * B01, P0.y * B01);
            Point2D.Double weightedP3 = new Point2D.Double(P3.x * B23, P3.y * B23);
            Point2D.Double weightedEnds = new Point2D.Double(weightedP0.x + weightedP3.x, weightedP0.y + weightedP3.y);

            error.add(new Point2D.Double(P_i.x - weightedEnds.x, P_i.y - weightedEnds.y));
        }
        return error;
    }

    // c00 = sigma(B1(u_i)^2)
    // c11 = sigma(B2(u_i)^2)
    // c01 (and) c10 = sigma(B1(u_i)*B2(u_i)*(T0(dot)T1))
    private static double[][] createNormalMatrix(double[][] B, Point2D.Double T0, Point2D.Double T1) {
        double c00 = 0, c11 = 0, c01 = 0;
        double T0T1 = dot(T0, T1);

        for (double[] b : B) {
            c00 += (b[1] * b[1]);
            c11 += (b[2] * b[2]);
            c01 += (b[1] * b[2] * T0T1);
        }
        return new double[][] { { c00, c01 },
                { c01, c11 } };
    }

    private static double dot(Point2D.Double a, Point2D.Double b) {
        return (a.x * b.x) + (a.y * b.y);
    }
}
