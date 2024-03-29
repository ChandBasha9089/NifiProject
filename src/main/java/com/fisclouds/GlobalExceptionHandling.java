package com.fisclouds;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandling {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?>  handleAllExceptions(Exception error)
    {


        return  ResponseEntity.internalServerError().body(error.getMessage());
    }
}
