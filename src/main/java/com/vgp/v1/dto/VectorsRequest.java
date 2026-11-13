package com.vgp.v1.dto;

import org.springframework.web.multipart.MultipartFile;

public record VectorsRequest(
    MultipartFile image,
    float blurStrength,
    float maskThresholdWeight,
    float simplificationThreshold,
    float curveTolerance
) {
}
