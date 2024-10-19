package org.example.catalogservice.DTO;

import lombok.Data;

@Data
public class RestaurantGetResponseDTO {
    private Integer id;
    private String name;
    private String address;

    public RestaurantGetResponseDTO(Integer id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
