package com.example.springdemo.exception;

import com.example.springdemo.responseStructure.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

 @ExceptionHandler(ResourceNotFoundException.class)
 ResponseEntity<ResponseStructure<String>> handleNotFound(ResourceNotFoundException ex){
     ResponseStructure<String> response = new ResponseStructure<>(404,ex.getMessage(),null);
     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
 }

    @ExceptionHandler(DuplicateResourceException.class)
    ResponseEntity<ResponseStructure<String>> handleDuplicate(DuplicateResourceException ex){
        ResponseStructure<String> response = new ResponseStructure<>(409,ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ResponseStructure<String>> handleGeneric(Exception ex){
        ResponseStructure<String> response = new ResponseStructure<>(500,"something went wrong",null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(InvalidGenderException.class)
    ResponseEntity<ResponseStructure<String>> handleInvalidException(InvalidGenderException ex){
        ResponseStructure<String> response = new ResponseStructure<>(400 , ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EnrollmentLimitException.class)
    public ResponseEntity<ResponseStructure<String>> handleEnrollmentLimit(
            EnrollmentLimitException ex) {

        ResponseStructure<String> response =
                new ResponseStructure<>(
                        400,
                        ex.getMessage(),
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }
}
