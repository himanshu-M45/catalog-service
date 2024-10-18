package org.example.catalogservice.Exceptions;

public class CannotCreateMenuItemException extends RuntimeException {
  public CannotCreateMenuItemException(String message) {
    super(message);
  }
}
