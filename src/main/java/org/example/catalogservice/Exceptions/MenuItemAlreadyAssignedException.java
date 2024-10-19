package org.example.catalogservice.Exceptions;

public class MenuItemAlreadyAssignedException extends RuntimeException {
    public MenuItemAlreadyAssignedException(String message) {
        super(message);
    }
}
