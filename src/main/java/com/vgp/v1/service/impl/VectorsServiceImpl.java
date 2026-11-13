package com.vgp.v1.service.impl;

import org.springframework.stereotype.Service;

import com.vgp.v1.dto.VectorsRequest;
import com.vgp.v1.exception.ExceptionEnum;
import com.vgp.v1.service.handler.VectorsServiceHandler;
import com.vgp.v1.service.interfaces.VectorsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class VectorsServiceImpl implements VectorsService {
    private final VectorsServiceHandler vectorsServiceHandler;

    @Override
    public String createVectors(VectorsRequest request) {
        String response = "Something went wrong.";
        try {
            response = vectorsServiceHandler.executePipeline(request);
        } catch (Exception e) {
            ExceptionEnum.FAILED_PIPELINE_EXECUTION.throwException();
        }
        return response;
    }

}
