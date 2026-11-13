package com.vgp.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vgp.v1.exception.dto.ExceptionEntity;

@RestControllerAdvice
@RequestMapping("v1/vectors")
public class VectorsControllerAdvice {
    
    @ExceptionHandler(ExceptionEntity.class)
    public ResponseEntity<ExceptionResponse> handleExceptionEntity(ExceptionEntity e){
        return ResponseEntity.status(e.getStatusCode()).body(new ExceptionResponse(e.getErrorCode(), e.getErrorMessage()));
    } 
    public record ExceptionResponse(
        int errorCode,
        String errorMessage  
    ){}
}
