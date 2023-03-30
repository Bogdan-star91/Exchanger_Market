package com.exchanger.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleInvalidTopUpTypeException(Exception ex) {
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<?> handleUserNotFoundException(Exception ex) {
        return new ResponseEntity("USER NOT FOUND", HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = {UnsupportedCurrencyException.class})
    public ResponseEntity<?> handleCurrencyException(Exception ex) {
        return new ResponseEntity("CURRENCY DOES NOT SUPPORT", HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = {CurrencyExistException.class})
    public ResponseEntity<?> handleCurrencyExistException(Exception ex) {
        return new ResponseEntity("WALLET WITh THIS CURRENCY IS EXIST", HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = {NotUniqueDataException.class})
    public ResponseEntity<?> handleUniqueDataException(Exception ex) {
        return new ResponseEntity("You could not create user", HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = {NotEnoughtMoneyException.class})
    public ResponseEntity<?> handleNotEnoughtMoneyException(Exception ex) {
        return new ResponseEntity("net babla....", HttpStatus.BAD_REQUEST);
    }
}
