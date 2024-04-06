package ru.netology.cloudstorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.cloudstorage.dto.ExceptionResponse;
import ru.netology.cloudstorage.exception.InputDataException;
import ru.netology.cloudstorage.exception.InternalServerException;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(InputDataException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(InputDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(400, e.getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ExceptionResponse> handleInternalServerError(InternalServerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse(500, e.getMessage()));
    }
}
