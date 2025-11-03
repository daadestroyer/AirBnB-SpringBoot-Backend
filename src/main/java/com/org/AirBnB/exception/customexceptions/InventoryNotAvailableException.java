package com.org.AirBnB.exception.customexceptions;

public class InventoryNotAvailableException extends RuntimeException{
    public InventoryNotAvailableException(String message) {
        super(message);
    }
}
