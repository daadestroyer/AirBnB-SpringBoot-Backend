package com.org.AirBnB.exception;

public class HotelNotActiveException extends RuntimeException{
    public HotelNotActiveException(String message) {
        super(message);
    }
}
