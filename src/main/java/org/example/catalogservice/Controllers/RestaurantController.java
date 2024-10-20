package org.example.catalogservice.Controllers;

import org.example.catalogservice.DTO.RequestDTO;
import org.example.catalogservice.DTO.ResponseDTO;
import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.Models.MenuItem;
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

    //    POST restaurant
    @PostMapping("")
    public ResponseEntity<Object> addRestaurant(@RequestBody RequestDTO requestDTO) {
        String response = restaurantService.addRestaurant(requestDTO.getName(), requestDTO.getAddress());
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.CREATED.value(), response));
    }

    //    GET all restaurants
    @GetMapping("")
    public ResponseEntity<Object> getAllRestaurant() {
        List<Restaurant> restaurants = restaurantService.findAllRestaurants();
        List<GETResponseDTO> GETResponseDTOS = restaurants.stream()
                .map(restaurantService::convertToDto)
                .toList();
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), GETResponseDTOS));
    }

    //    GET restaurant by id
    @GetMapping("/{restaurantId}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable Integer restaurantId) {
        Restaurant restaurant = restaurantService.findById(restaurantId);
        GETResponseDTO GETResponseDTO = restaurantService.convertToDto(restaurant);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), GETResponseDTO));
    }

    //    POST menu items to respective restaurant
    @PostMapping("/{restaurantId}/menu-items")
    public ResponseEntity<Object> assign(@PathVariable Integer restaurantId, @RequestParam String menuItemIds) {
        String response = restaurantService.assignMenuItemToRestaurant(restaurantId, menuItemIds);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), response));
    }

    //    GET all menu items of respective restaurant
    @GetMapping("/{restaurantId}/menu-items")
    public ResponseEntity<Object> getMenuItems(@PathVariable Integer restaurantId, @RequestParam(required = false) String menuItemIds) {
        if (menuItemIds != null) {
            List<MenuItem> GETResponseDTOS = restaurantService.getSelectedMenuItemsById(restaurantId, menuItemIds);
            return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), GETResponseDTOS));
        }
        List<MenuItem> GETResponseDTOS = restaurantService.getMenuItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), GETResponseDTOS));
    }
}