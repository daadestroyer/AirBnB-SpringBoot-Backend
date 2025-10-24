package com.org.AirBnB.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoRoomsFoundException.class)
    public ResponseEntity<APIResponse> handleNoRoomsFoundException(NoRoomsFoundException ex){
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(HotelNotActiveException.class)
    public ResponseEntity<APIResponse> handleHotelNotActiveException(HotelNotActiveException ex){
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }
}
