package org.example.catalogservice.Exceptions;

public class MenuItemDoesNotExistException extends RuntimeException {
    public MenuItemDoesNotExistException(String message) {
        super(message);
    }
}
