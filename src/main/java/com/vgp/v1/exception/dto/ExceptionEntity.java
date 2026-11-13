package com.vgp.v1.exception.dto;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExceptionEntity extends RuntimeException{
    @Getter private final int errorCode;
    @Getter private final HttpStatusCode statusCode;
    @Getter private final String errorMessage;
}