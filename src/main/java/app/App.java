package app;

public class App {
    public static void start(String PATH_RASTER, float BLUR_SIGMA, float SIMPLIFICATION_EPSILON, float CURVE_TOLERANCE){
        java.awt.image.BufferedImage preview = com.vgp.service.ImageReader.readImage(new java.io.File(PATH_RASTER));
        try {javax.imageio.ImageIO.write(preview, "png", new java.io.File("target/input.png"));} catch (Exception e) {e.printStackTrace();}
        float[][] grayscaleMap = com.vgp.service.ValueMapper.createGrayscaleMap(preview);
        writeOutputRaster("v1_grayscale", grayscaleMap);
        int w = grayscaleMap[0].length;
        int h = grayscaleMap.length;
        float[] normalizedMap = com.vgp.utils.ArrayUtils.normalize(grayscaleMap);
        float[] normalizedblurredMap = com.vgp.service.GaussianBlur.blur(normalizedMap, w, h, BLUR_SIGMA);
        float[][] blurredMap = com.vgp.utils.ArrayUtils.denormalize(normalizedblurredMap, w, h);
        writeOutputRaster("v2_blurred", blurredMap);
        boolean[][] mask = com.vgp.service.BinaryMasker.mask(blurredMap, com.vgp.service.BinaryMasker.findThreshold(blurredMap));
        writeOutputRaster("v3_mask", convertMaskToValues(mask));
        java.util.List<java.util.List<java.awt.geom.Point2D.Double>> contour = com.vgp.service.ContourTracing.trace(mask);
        writeOutputVector("v4_contour", com.vgp.utils.ArrayUtils.serializePolylineToSVG(contour, w, h));
        java.util.List<java.util.List<java.awt.geom.Point2D.Double>> simplifiedContour = com.vgp.service.RDPSimplifier.simplify(contour, SIMPLIFICATION_EPSILON);
        writeOutputVector("v5_simplified", com.vgp.utils.ArrayUtils.serializePolylineToSVG(simplifiedContour, w, h));
        java.util.List<java.util.List<com.vgp.dto.Cubic>> cubics = com.vgp.service.CurveFitter.fit(simplifiedContour, CURVE_TOLERANCE);
        writeOutputVector("output", com.vgp.utils.ArrayUtils.serializeCubicsToSVG(cubics, w, h));
    }
    private static void writeOutputRaster(String fileName, float[][] image){
        try {
            java.awt.image.BufferedImage preview = new java.awt.image.BufferedImage(image[0].length, image.length, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < image.length; y++) {
                for (int x = 0; x < image[0].length; x++) {
                    int gray = Math.round(image[y][x] * 255);
                    gray = Math.max(0, Math.min(255, gray));
                    preview.getRaster().setSample(x, y, 0, gray);
                }
            }
            javax.imageio.ImageIO.write(preview, "png", new java.io.File("target/"+fileName+".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void writeOutputVector(String fileName, String SVG) {
        try (java.io.FileWriter fw = new java.io.FileWriter(new java.io.File("target/"+fileName+".svg"))) {
            fw.write(SVG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static  float[][] convertMaskToValues(boolean[][] mask){
        float[][] image = new float[mask.length][mask[0].length];
        for (int y = 0; y < mask.length; y++) {
            for (int x = 0; x < mask[0].length; x++) {
                image[y][x] = (mask[y][x]?255:0);
            }
        }
        return image;
    }
}
