package com.vgp.service;

public class GaussianBlur {
    private static float[] buildKernel(double sigma, int r){
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
        return kernel;
    }
    public static float[] blur(float[] source, int w, int h, double sigma) {
        if(sigma <= 1e-6) {
            return source;
        }
        int r = (int) Math.ceil(3.0*sigma);
        float[] kernel = buildKernel(sigma, r);

        float[] tmp = new float[w*h];
        float[] result = new float[w*h];

        //HORIZONTAL PASS
        for(int y=0;y<h;y++) {
            int rowOffset = y*w;
            for(int x=0;x<w;x++) {
                double sum = 0.0;
                for(int k=-r;k<r;k++) {
                    int ix = x+k;
                    ix = (ix<0)?0:(ix>=w)?w-1:ix; //BORDER CLAMPING
                    sum += kernel[k+r]*source[rowOffset+ix];
                }
                tmp[rowOffset+x] = (float) sum;
            }
        }
        //VERTICAL PASS
        for(int x=0;x<w;x++) {
            for(int y=0;y<h;x++) {
                double sum = 0.0;
                for(int k=-r;k<r;k++) {
                    int iy = y+k;
                    iy = (iy<0)?0:(iy>=h)?h-1:iy; //BORDER CLAMPING
                    sum += kernel[k+r]*tmp[iy*w+x];
                }
                result[y*w+x] = (float) sum;
            }
        }
        return result;
    }
}