package com.vgp.service;

import java.util.ArrayList;
import java.util.List;

public class RDPSimplifier {
    public static List<List<java.awt.geom.Point2D.Double>> simplify(List<List<java.awt.geom.Point2D.Double>> points, float epsilon){
        List<List<java.awt.geom.Point2D.Double>> result = new ArrayList<>();
        int outer_size = points.size();

        for(int i=0;i<outer_size;i++) {
            List<java.awt.geom.Point2D.Double> simplified = new ArrayList<>();
            List<java.awt.geom.Point2D.Double> segment = points.get(i);
            int size = segment.size();
            boolean[] keep = new boolean[size];
            keep[0] = true;
            keep[size-1] = true;

            recurseRDP(segment, 0, size-1, keep, epsilon);

            for(int j=1;j<size-2;j++) {
                if(!keep[j]) continue;
                simplified.add(segment.get(j));
            }
            result.add(simplified);
        }
        return result;
    }
    public static void recurseRDP(List<java.awt.geom.Point2D.Double> points, int first, int last, boolean[] keep, float epsilon){
        if(first+1 >= last) return; // No points in between to simplify

        double maxDistance = 0;
        int maxIndex = -1;

        for(int i=first+1;i<last-1;i++) {
            // Calculating perpendicular distance of the point from a straight line between sub segment of points
            // TO IMPLEMENT
            double distance = calculatePerpendicularDistance(points.get(i), points.get(first), points.get(last));
            if(distance > maxDistance) {
                maxDistance = distance;
                maxIndex = i;
            }
        }

        if(maxDistance > epsilon) {
            keep[maxIndex] = true;
            recurseRDP(points, first, maxIndex, keep, epsilon);
            recurseRDP(points, maxIndex, last, keep, epsilon);
        } else {
            // All points lie closer than the epsilon so we remove them.
            for(int i=first+1;i<last-1;i++) {
                keep[i] = false;
            }
        }
    }
    public static double calculatePerpendicularDistance(java.awt.geom.Point2D.Double P, java.awt.geom.Point2D.Double A, java.awt.geom.Point2D.Double B){
        double Dx = B.x - A.x;
        double Dy = B.y - A.y;

        double len2 = (Dx*Dx) + (Dy*Dy);
        double cross = ((P.x - A.x)* Dy) - ((P.y - A.y)* Dx);
        
        double distance = Math.abs(cross) / Math.sqrt(len2);

        return distance;
    }
}
