package com.vgp.v1.service.handler;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.vgp.dto.Cubic;
import com.vgp.service.BinaryMasker;
import com.vgp.service.ContourTracing;
import com.vgp.service.CurveFitter;
import com.vgp.service.GaussianBlur;
import com.vgp.service.RDPSimplifier;
import com.vgp.service.ValueMapper;
import com.vgp.utils.ArrayUtils;
import com.vgp.v1.dto.VectorsRequest;

@Service 
public class VectorsServiceHandler {

    public String executePipeline(VectorsRequest request) throws IOException {
        BufferedImage preview = ImageIO.read(request.image().getInputStream());
        
        float[][] grayscale = ValueMapper.createGrayscaleMap(preview);
        int w = grayscale[0].length, h = grayscale.length;
        
        float[] normal = ArrayUtils.normalize(grayscale);
        float[] blurredNormal = GaussianBlur.blur(normal, w, h, request.blurStrength());
        float[][] blurred = ArrayUtils.denormalize(blurredNormal, w, h);

        float threshold = BinaryMasker.findThreshold(blurred)+request.maskThresholdWeight();
        boolean[][] mask = BinaryMasker.mask(blurred, threshold);

        List<List<Point2D.Double>> points = ContourTracing.trace(mask);
        List<List<Point2D.Double>> simplfied = RDPSimplifier.simplify(points, request.simplificationThreshold());

        List<List<Cubic>> cubics = CurveFitter.fit(simplfied, request.curveTolerance());

        return ArrayUtils.serializeCubicsToSVG(cubics, w, h);
    }

}
