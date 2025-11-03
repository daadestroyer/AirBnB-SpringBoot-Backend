package com.org.AirBnB.exception.customexceptions;

public class BookingAtIllegalState extends RuntimeException{
    public BookingAtIllegalState(String message) {
        super(message);
    }
}
