package org.example.catalogservice.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Optional;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GETResponseDTO {
    private Integer id;
    private String name;
    private Optional<String> address = Optional.empty();
    private Optional<Integer> price = Optional.empty();
}
