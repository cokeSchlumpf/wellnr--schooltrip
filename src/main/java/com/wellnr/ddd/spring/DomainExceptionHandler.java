package com.wellnr.ddd.spring;

import com.wellnr.ddd.DomainException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleDomainException(DomainException exception) {
        var problemDetail = ProblemDetail.forStatus(exception.getStatus());
        problemDetail.setTitle(exception.getSummary());
        problemDetail.setDetail(exception.getMessage());

        return ResponseEntity.of(problemDetail).build();
    }

}
