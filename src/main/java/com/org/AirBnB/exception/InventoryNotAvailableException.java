package com.org.AirBnB.exception;

public class InventoryNotAvailableException extends RuntimeException{
    public InventoryNotAvailableException(String message) {
        super(message);
    }
}
