package org.example.catalogservice.Exceptions;

public class RestaurantDoesNotOwnMenuItemException extends RuntimeException {
    public RestaurantDoesNotOwnMenuItemException(String message) {
        super(message);
    }
}
