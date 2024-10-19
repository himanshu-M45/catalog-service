package org.example.catalogservice.Controllers;

import org.example.catalogservice.DTO.RequestDTO;
import org.example.catalogservice.DTO.ResponseDTO;
import org.example.catalogservice.DTO.RestaurantGetResponseDTO;
import org.example.catalogservice.Models.Restaurant;
import org.example.catalogservice.Services.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("")
    public ResponseEntity<Object> addRestaurant(@RequestBody RequestDTO requestDTO) {
        String response = restaurantService.addRestaurant(requestDTO.getName(), requestDTO.getAddress());
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.CREATED.value(), response));
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllRestaurant() {
        List<Restaurant> restaurants = restaurantService.findAllRestaurants();
        List<RestaurantGetResponseDTO> restaurantGetResponseDTOS = restaurants.stream()
                .map(restaurantService::convertToDto)
                .toList();
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), restaurantGetResponseDTOS));
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable Integer restaurantId) {
        Restaurant restaurant = restaurantService.findById(restaurantId);
        RestaurantGetResponseDTO restaurantGetResponseDTO = restaurantService.convertToDto(restaurant);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), restaurantGetResponseDTO));
    }
}