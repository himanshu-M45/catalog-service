package org.example.catalogservice.Exceptions;

public class CannotCreateRestaurantException extends RuntimeException {
  public CannotCreateRestaurantException(String message) {
    super(message);
  }
}
