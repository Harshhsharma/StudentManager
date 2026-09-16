package com.example.springdemo.exception;

public class EnrollmentLimitException extends RuntimeException {

    public EnrollmentLimitException(String message) {
        super(message);
    }
}
