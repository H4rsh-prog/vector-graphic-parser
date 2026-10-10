package com.vgp.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContourTracing {
    public static List<List<java.awt.geom.Point2D.Double>> trace(boolean[][] unpadded_mask){
        if (unpadded_mask == null || unpadded_mask.length == 0 || unpadded_mask[0] == null || unpadded_mask[0].length == 0) {
            return new ArrayList<>();
        }
        int h = unpadded_mask.length;
        int w = unpadded_mask[0].length;
        // Padding mask with 1 extra block to handle border tracing
        boolean[][] mask = new boolean[h + 2][w + 2];
        for(int y=0;y<h;y++) {
            for(int x=0;x<w;x++) {
                mask[y+1][x+1] = unpadded_mask[y][x];
            }
        }

        HashMap<Long, Long> segments = new HashMap<>();
        for(int y=0;y<=h;y++) {
            for(int x=0;x<=w;x++) {
                boolean TL = mask[y][x];
                boolean TR = mask[y][x+1];
                boolean BR = mask[y+1][x+1];
                boolean BL = mask[y+1][x];

                if(!TL && !TR && !BL && !BR) continue;

                // Doubling the grid context so midpoints can be represented as whole integers
                // Then packing them into a single 64 bit coordinate
                long T = packCordinates(2*x+1, 2*y);
                long R = packCordinates(2*x+2, 2*y+1);
                long B = packCordinates(2*x+1, 2*y+2);
                long L = packCordinates(2*x, 2*y+1);

                //BL => L -> B
                verifyAddSegment(segments, L, B, BL);
                //BR => B -> R
                verifyAddSegment(segments, B, R, BR);
                //TR => R -> T
                verifyAddSegment(segments, R, T, TR);
                //TL => T -> L
                verifyAddSegment(segments, T, L, TL);
            }
        }

        return closeLoops(segments);
    }
    private static void verifyAddSegment(HashMap<Long, Long> segments, long from, long to, boolean active) {
        if(!active) return;

        Long existing = segments.get(from);
        if(existing == null) {
            segments.put(from, to);
            return;
        }
        // if(!existing.equals(to)) {
        //     throw new IllegalStateException(
        //         "Conflicting contour segment at " + from + ": " + existing + " vs " + to
        //     );
        // }
    }
    private static List<List<java.awt.geom.Point2D.Double>> closeLoops(HashMap<Long, Long> segments) {
        List<List<java.awt.geom.Point2D.Double>> contours = new ArrayList<>();
        while(!segments.isEmpty()) {
            Long start = segments.keySet().iterator().next();
            Long cur = start;
            List<java.awt.geom.Point2D.Double> contour_current = new ArrayList<>();
            boolean broken = false;

            while(true) {
                Long next = segments.remove(cur);
                if(next==null) {
                    broken = true;
                    break;
                }
                int cx = unpackXCordinate(cur);
                int cy = unpackYCordinate(cur);
                //Dividing by two to reset the context grid and subtracting padding
                contour_current.add(new java.awt.geom.Point2D.Double((cx / 2.0) - 1.0, (cy / 2.0) - 1.0));
                cur = next;
                if (cur.equals(start)) {
                    break;
                }
            }

            if(!broken && contour_current.size() >= 3) {
                contours.add(contour_current);
            }
        }
        return contours;
    }

    //HELPER FUNCTION TO PACK [X,Y] CORDINATES INTO A SINGLE 64 BIT LONG
    private static long packCordinates(int x, int y){
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }
    //HELPER FUNCTION TO UNPACK THE X CORDINATE FROM PACKED CORDINATES
    private static int unpackXCordinate(long xy) {
        return (int) (xy >>> 32);
    }
    //HELPER FUNCTION TO UNPACK THE Y CORDINATE FROM PACKED CORDINATES
    private static int unpackYCordinate(long xy) {
        return (int) (xy & 0xFFFFFFFFL);
    }
}
