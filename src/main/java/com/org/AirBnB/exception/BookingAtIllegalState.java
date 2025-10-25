package com.org.AirBnB.exception;

public class BookingAtIllegalState extends RuntimeException{
    public BookingAtIllegalState(String message) {
        super(message);
    }
}
