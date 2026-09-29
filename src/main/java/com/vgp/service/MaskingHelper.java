package com.vgp.service;

public class MaskingHelper {
    public static boolean[][] maskBinary(float[][] value_graph, float threshold){
        int W = value_graph[0].length;
        int H = value_graph.length;
        boolean[][] mask = new boolean[H][W];
        for(int y=0;y<H;y++) {
            for(int x=0;x<W;x++) {
                mask[y][x] = (value_graph[y][x]>threshold);
            }
        }
        return mask;
    }
}
