package com.org.AirBnB.exception;

import com.org.AirBnB.exception.customexceptions.*;
import io.jsonwebtoken.JwtException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoRoomsFoundException.class)
    public ResponseEntity<APIResponse> handleNoRoomsFoundException(NoRoomsFoundException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HotelNotActiveException.class)
    public ResponseEntity<APIResponse> handleHotelNotActiveException(HotelNotActiveException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InventoryNotAvailableException.class)
    public ResponseEntity<APIResponse> handleInventoryNotAvailableException(InventoryNotAvailableException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingExpiredException.class)
    public ResponseEntity<APIResponse> handleBookingExpiredException(BookingExpiredException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingAtIllegalState.class)
    public ResponseEntity<?> handleBookingAtIllegalState(BookingAtIllegalState ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<?> handleUnAuthorizedException(UnAuthorizedException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
