package com.vgp.service;

public class BinaryMasker {
    public static boolean[][] mask(float[][] value_graph, float threshold){
        int W = value_graph[0].length;
        int H = value_graph.length;
        boolean[][] mask = new boolean[H][W];
        for(int y=0;y<H;y++) {
            for(int x=0;x<W;x++) {
                mask[y][x] = (value_graph[y][x]<threshold);
            }
        }
        return mask;
    }
    public static float findThreshold(float[][] value_graph){
        int W = value_graph[0].length;
        int H = value_graph.length;
        int totalPixels = W * H;
        
        int[] histogram = new int[256];
        for(int y=0; y<H; y++) {
            for(int x=0; x<W; x++) {
                int value = (int) (value_graph[y][x] * 255f);
                value = Math.max(0, Math.min(255, value));
                histogram[value]++;
            }
        }

        int sumFx = 0;
        for(int i=0; i<256; i++) {
            sumFx += i*histogram[i];
        }

        double sumBackground = 0;
        int weightBackground = 0;
        double maxVariance = 0;
        int bestThreshold = 0;

        for(int i=0; i<256; i++) {
            
            weightBackground += histogram[i];
            if(weightBackground==0) continue;

            int weightForeground = totalPixels - weightBackground;
            if(weightForeground==0) break;

            sumBackground += (double) (i*histogram[i]);

            double meanBackground = sumBackground / weightBackground;
            double meanForeground = (sumFx - sumBackground) / weightForeground;

            double variance = ((double) weightBackground) * ((double) weightForeground) * Math.pow((meanBackground-meanForeground), 2);

            if(variance>maxVariance) {
                maxVariance = variance;
                bestThreshold = i;
            }
        }

        return bestThreshold/255f;
    }
}
