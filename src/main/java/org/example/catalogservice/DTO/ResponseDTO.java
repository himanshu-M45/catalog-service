package org.example.catalogservice.DTO;

import lombok.Data;

@Data
public class ResponseDTO<T> {
    private int statusCode;
    private final T data;

    public ResponseDTO(int statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    }

}
