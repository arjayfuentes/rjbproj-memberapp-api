package com.rjproj.memberapp.handler;

import com.rjproj.memberapp.exception.OrganizationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<String> handle(OrganizationNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        var errors = new HashMap<String, String>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorResponse(errors));
    }


//    @ExceptionHandler(MembershipNotFoundException.class)
//    public ResponseEntity<String> handle(MembershipNotFoundException exp) {
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(exp.getMsg());
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MembershipNotFoundException exp) {
//        var errors = new HashMap<String, String>();
//        exp.getBindingResult().getAllErrors()
//                .forEach(error -> {
//                    var fieldName = ((FieldError) error).getField();
//                    var errorMessage = error.getDefaultMessage();
//                    errors.put(fieldName, errorMessage);
//                });
//
//        return ResponseEntity
//                .status(BAD_REQUEST)
//                .body(new ErrorResponse(errors));
//    }
}
