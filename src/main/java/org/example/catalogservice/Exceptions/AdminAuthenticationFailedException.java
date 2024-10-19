package org.example.catalogservice.Exceptions;

public class AdminAuthenticationFailedException extends RuntimeException {
  public AdminAuthenticationFailedException(String message) {
    super(message);
  }
}
