package com.vgp.service;

public class GaussianBlur {
    private static float[][] buildKernel(double sigma, int r){
        int size = (2*r)+1;
        float kernel[] = new float[size];
        double twoSigmaSq = 2*(sigma*sigma);
        double sum = 0.0;
        for(int i=-r;i<=r;i++) {
            double value = Math.exp(-(i*i)/twoSigmaSq); // exp(-(i^-r...i^r)/2.0*(Sigma^2))
            kernel[i+r] = (float) value;
            sum += value;
        }
        //Normalize the kernel so that the sum == 1
        for(int i=0;i<size;i++) {
            kernel[i] /= sum;
        }
    }
}