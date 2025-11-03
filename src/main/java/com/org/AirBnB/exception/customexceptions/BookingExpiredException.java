package com.org.AirBnB.exception.customexceptions;

public class BookingExpiredException extends RuntimeException{
    public BookingExpiredException(String message) {
        super(message);
    }
}
