package org.example.catalogservice.Exceptions;

public class RestaurantDetailsAlreadyAddedException extends RuntimeException {
    public RestaurantDetailsAlreadyAddedException(String message) {
        super(message);
    }
}
