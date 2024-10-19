package org.example.catalogservice.Exceptions;

import org.example.catalogservice.DTO.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CannotCreateRestaurantException.class)
    public ResponseEntity<ResponseDTO<String>> handleCannotCreateRestaurantException(CannotCreateRestaurantException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(RestaurantDetailsAlreadyAddedException.class)
    public ResponseEntity<ResponseDTO<String>> handleRestaurantDetailsAlreadyAddedException(RestaurantDetailsAlreadyAddedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(RestaurantDoesNotExistException.class)
    public ResponseEntity<ResponseDTO<String>> handleRestaurantDoesNotExistException(RestaurantDoesNotExistException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(CannotCreateMenuItemException.class)
    public ResponseEntity<ResponseDTO<String>> handleCannotCreateMenuItemException(CannotCreateMenuItemException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(MenuItemAlreadyAddedException.class)
    public ResponseEntity<ResponseDTO<String>> handleMenuItemAlreadyAddedException(MenuItemAlreadyAddedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(MenuItemDoesNotExistException.class)
    public ResponseEntity<ResponseDTO<String>> handleMenuItemDoesNotExistException(MenuItemDoesNotExistException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }
}
