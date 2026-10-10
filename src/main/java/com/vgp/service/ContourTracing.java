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

        HashMap<Long, List<Long>> adjacency = new HashMap<>();
        for(int y=0;y<=h;y++) {
            for(int x=0;x<=w;x++) {
                boolean TL = mask[y][x];
                boolean TR = mask[y][x+1];
                boolean BR = mask[y+1][x+1];
                boolean BL = mask[y+1][x];

                int cellCase = (TL?8:0) | (TR?4:0) | (BR?2:0) | (BL?1:0) ;

                // Doubling the grid context so midpoints can be represented as whole integers
                // Then packing them into a single 64 bit coordinate
                long T = packCordinates(2*x+1, 2*y);
                long R = packCordinates(2*x+2, 2*y+1);
                long B = packCordinates(2*x+1, 2*y+2);
                long L = packCordinates(2*x, 2*y+1);

                switch (cellCase) {
                    case 1 -> addSegment(adjacency, L, B);   // BL
                    case 2 -> addSegment(adjacency, B, R);  // BR
                    case 3 -> addSegment(adjacency, L, R);    // BL, BR
                    case 4 -> addSegment(adjacency, R, T);     // TR
                    case 5 -> {
                        // TL and BR foreground: connect around the two
                        // background corners, keeping foreground 8-connected.
                        addSegment(adjacency, T, R);
                        addSegment(adjacency, B, L);
                    }
                    case 6 -> addSegment(adjacency, T, B);    // TR, BR
                    case 7 -> addSegment(adjacency, T, L);      // All but BL
                    case 8 -> addSegment(adjacency, T, L);      // TL
                    case 9 -> addSegment(adjacency, T, B);    // TL, BL
                    case 10 -> {
                        // TR and BL foreground: connect around the two
                        // background corners, keeping foreground 8-connected.
                        addSegment(adjacency, T, L);
                        addSegment(adjacency, R, B);
                    }
                    case 11 -> addSegment(adjacency, R, T);    // All but TR
                    case 12 -> addSegment(adjacency, L, R);   // TL, TR
                    case 13 -> addSegment(adjacency, B, R); // All but BR
                    case 14 -> addSegment(adjacency, B, L);  // All but TL
                    default -> {
                        // Cases 0 and 15 have no foreground/background edge.
                    }
                }
            }
        }

        return closeLoops(adjacency);
    }
    private static void addSegment(HashMap<Long, List<Long>> adjacency, long from, long to) {
        adjacency.computeIfAbsent(from, absent -> new ArrayList<>(2)).add(to);
        adjacency.computeIfAbsent(to, absent -> new ArrayList<>(2)).add(from);
    }
    private static List<List<java.awt.geom.Point2D.Double>> closeLoops(HashMap<Long, List<Long>> adjacency) {
        List<List<java.awt.geom.Point2D.Double>> contours = new ArrayList<>();
        while(!adjacency.isEmpty()) {
            Long start = adjacency.keySet().iterator().next();
            Long cur = start;
            List<java.awt.geom.Point2D.Double> contour_current = new ArrayList<>();
            boolean closed = false;

            while(true) {
                int cx = unpackXCordinate(cur);
                int cy = unpackYCordinate(cur);
                contour_current.add(new java.awt.geom.Point2D.Double((cx / 2.0) - 1.0, (cy / 2.0) - 1.0));
                
                List<Long> neighbors = adjacency.get(cur);
                if(neighbors==null || neighbors.isEmpty()) {
                    break;
                }

                long next = neighbors.get(0);
                removeSegment(adjacency, cur, next);
                cur = next;

                if (cur.equals(start)) {
                    closed = true;
                    break;
                }
            }

            if(closed && contour_current.size() >= 3) {
                contours.add(contour_current);
            }
        }
        return contours;
    }
    private static void removeSegment(HashMap<Long, List<Long>> adjacency, long from, long to) {
        removeNeighbor(adjacency, from, to);
        removeNeighbor(adjacency, to, from);
    }
    private static void removeNeighbor(HashMap<Long, List<Long>> adjacency, long from, long to) {
        List<Long> neighbors = adjacency.get(from);
        if(neighbors == null) return;

        neighbors.remove(Long.valueOf(to));
        if(neighbors.isEmpty()) {
            adjacency.remove(from);
        }
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
