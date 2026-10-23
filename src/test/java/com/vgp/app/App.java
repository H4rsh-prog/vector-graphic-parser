package com.vgp.app;

public class App {
    public static void start(String PATH_RASTER, String PATH_VECTOR, float BLUR_SIGMA, float MASK_THRESHOLD, float SIMPLIFICATION_EPSILON){
        java.awt.image.BufferedImage bufferedImage = com.vgp.service.ImageReader.readImage(new java.io.File(PATH_RASTER));
        float[][] grayscaleMap = com.vgp.service.ValueMapper.createGrayscaleMap(bufferedImage);
        int w = grayscaleMap[0].length;
        int h = grayscaleMap.length;
        float[] normalizedMap = com.vgp.utils.ArrayUtils.normalize(grayscaleMap);
        float[] normalizedblurredMap = com.vgp.service.GaussianBlur.blur(normalizedMap, w, h, BLUR_SIGMA);
        float[][] blurredMap = com.vgp.utils.ArrayUtils.denormalize(normalizedblurredMap, w, h);
        boolean[][] mask = com.vgp.service.BinaryMasker.mask(blurredMap, MASK_THRESHOLD);
        java.util.List<java.util.List<java.awt.geom.Point2D.Double>> contour = com.vgp.service.ContourTracing.trace(mask);
        java.util.List<java.util.List<java.awt.geom.Point2D.Double>> simplifiedContour = com.vgp.service.RDPSimplifier.simplify(contour, SIMPLIFICATION_EPSILON);
        // TODO : Schneider's Algorithm for Bezier Curve Fitting
        
    }
}
