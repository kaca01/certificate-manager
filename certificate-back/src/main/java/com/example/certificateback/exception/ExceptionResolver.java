package com.example.certificateback.exception;

import com.example.certificateback.dto.ErrorDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionResolver {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(BadRequestException exception) {
        return new ResponseEntity<>(new ErrorDTO(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundException(NotFoundException exception) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(exception.getMessage(), headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<?> notValidException(NotValidException exception) {
        return new ResponseEntity<>(new ErrorDTO(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongUserException.class)
    public ResponseEntity<?> wrongUserException(WrongUserException exception) {
        return new ResponseEntity<>(new ErrorDTO(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
