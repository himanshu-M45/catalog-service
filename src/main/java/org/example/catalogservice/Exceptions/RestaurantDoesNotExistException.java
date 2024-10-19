package org.example.catalogservice.Exceptions;

public class RestaurantDoesNotExistException extends RuntimeException {
    public RestaurantDoesNotExistException(String message) {
        super(message);
    }
}
