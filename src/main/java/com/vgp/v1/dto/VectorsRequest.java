package com.vgp.v1.dto;

public record VectorsRequest(
    String Base64EncodedImage,
    float blurSigma,
    float maskThresholdWeight,
    float simplificationEpsilon,
    float curveTolerance
) {
}
