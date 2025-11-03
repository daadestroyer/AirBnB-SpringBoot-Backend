package com.org.AirBnB.exception.customexceptions;

public class NoRoomsFoundException extends RuntimeException{
    public NoRoomsFoundException(String message) {
        super(message);
    }
}
