package com.vgp.v1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.vgp.v1.exception.dto.ExceptionEntity;


public enum ExceptionEnum {
    FAILED_PIPELINE_EXECUTION(0, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.");

    private final ExceptionEntity exception;

    private ExceptionEnum(int errorCode, HttpStatusCode statusCode, String errorMessage) {
        this.exception = new ExceptionEntity(errorCode, statusCode, errorMessage);
    }

    public void throwException() {
        throw this.exception;
    }
}
