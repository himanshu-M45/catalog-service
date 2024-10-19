package org.example.catalogservice.Exceptions;

public class MenuItemAlreadyAddedException extends RuntimeException {
    public MenuItemAlreadyAddedException(String message) {
        super(message);
    }
}
