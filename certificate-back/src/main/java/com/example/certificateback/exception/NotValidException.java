package com.example.certificateback.exception;

public class NotValidException extends RuntimeException{

    public NotValidException() {}

    public NotValidException(String message) {
        super(message);
    }
}
