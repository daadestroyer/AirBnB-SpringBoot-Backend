package com.org.AirBnB.exception.customexceptions;

public class HotelNotActiveException extends RuntimeException{
    public HotelNotActiveException(String message) {
        super(message);
    }
}
