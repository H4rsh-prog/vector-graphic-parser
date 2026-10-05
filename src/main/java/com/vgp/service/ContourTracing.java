package com.vgp.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContourTracing {
    public static List<List<java.awt.geom.Point2D.Double>> trace(boolean[][] mask){
        int h = mask.length;
        int w = mask[0].length;
        HashMap<Long, Long> segments = new HashMap<>();
        for(int y=0;y<h-1;y++) {
            for(int x=0;x<w-1;x++) {
                boolean TL = mask[y][x];
                boolean TR = mask[y][x+1];
                boolean BL = mask[y+1][x];
                boolean BR = mask[y+1][x+1];

                if(!TL && !TR && !BL && !BR) continue;

                // Doubling the grid context so midpoints can be represented as whole integers
                int Tx = 2*x+1, Ty = 2*y;
                int Rx = 2*x+2, Ry = 2*y+1;
                int Bx = 2*x+2, By = 2*y+2;
                int Lx = 2*x,   Ly = 2*y+1;

                //BL => L -> B
                if(BL) segments.put(packCordinates(Lx, Ly), packCordinates(Bx, By));
                //BR => B -> R
                if(BR) segments.put(packCordinates(Bx, By), packCordinates(Rx, Ry));
                //TR => R -> T
                if(TR) segments.put(packCordinates(Rx, Ry), packCordinates(Tx, Ty));
                //TL => T -> L
                if(TL) segments.put(packCordinates(Tx, Ty), packCordinates(Lx, Ly));
            }
        }

        return closeLoops(segments);
    }
    private static List<List<java.awt.geom.Point2D.Double>> closeLoops(HashMap<Long, Long> segments) {
        List<List<java.awt.geom.Point2D.Double>> contours = new ArrayList<>();
         return contours;
    }

    //HELPER FUNCTION TO PACK [X,Y] CORDINATES INTO A SINGLE 64 BIT LONG
    private static Long packCordinates(Integer x, Integer y){
        return ((x.longValue() & 0xFFFFFFFFL) << 32) | (y.longValue() & 0xFFFFFFFFL);
    }
    //HELPER FUNCTION TO UNPACK THE X CORDINATE FROM PACKED CORDINATES
    private static int unpackXCordinate(Long xy) {
        return (int) (xy >>> 32);
    }
    //HELPER FUNCTION TO UNPACK THE Y CORDINATE FROM PACKED CORDINATES
    private static int unpackYCordinate(Long xy) {
        return (int) (xy & 0xFFFFFFFFL);
    }
}
