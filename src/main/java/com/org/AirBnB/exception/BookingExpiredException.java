package com.org.AirBnB.exception;

public class BookingExpiredException extends RuntimeException{
    public BookingExpiredException(String message) {
        super(message);
    }
}
