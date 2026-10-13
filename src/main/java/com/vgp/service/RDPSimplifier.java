package com.vgp.service;

import java.util.List;

public class RDPSimplifier {

    public static void recurseRDP(List<java.awt.geom.Point2D.Double> points, int first, int last, boolean[] keep, float epsilon){
        if(first+1 >= last) return; // No points in between to simplify

        float maxDistance = 0;
        int maxIndex = -1;

        for(int i=first+1;i<last-1;i++) {
            // Calculating perpendicular distance of the point from a straight line between sub segment of points
            // TO IMPLEMENT
            int distance = calculatePerpendicularDistance(points.get(i), points(first), points(last));
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
}
