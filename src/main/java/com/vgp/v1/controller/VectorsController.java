package com.vgp.v1.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vgp.v1.dto.VectorsRequest;
import com.vgp.v1.service.interfaces.VectorsService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/vectors")
@AllArgsConstructor
public class VectorsController {
    private final VectorsService vectorsService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createVectors(
        @RequestParam("image") MultipartFile image,
        @RequestParam("blur_strength") float blurStrength,
        @RequestParam(name = "binary_mask_weight", required = false, defaultValue = "0f") float maskThresholdWeight,
        @RequestParam("simplfication_threshold") float simplificationThreshold,
        @RequestParam("curve_tolerance") float curveTolerance
    ){
        return vectorsService.createVectors(new VectorsRequest(image, blurStrength, maskThresholdWeight, simplificationThreshold, curveTolerance));
    }
}
