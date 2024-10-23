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
    @PostMapping
    public ResponseEntity<Object> addRestaurant(@RequestBody RequestDTO requestDTO) {
        String response = restaurantService.addRestaurant(requestDTO.getName(), requestDTO.getAddress());
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.CREATED.value(), response));
    }

    //    GET all restaurants
    @GetMapping
    public ResponseEntity<Object> getAllRestaurant() {
        List<Restaurant> restaurants = restaurantService.findAllRestaurants();
        List<GETResponseDTO> response = restaurants.stream()
                .map(restaurantService::convertToDtoRestaurant)
                .toList();
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), response));
    }

    //    GET restaurant by id
    @GetMapping("/{restaurantId}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable Integer restaurantId) {
        Restaurant restaurant = restaurantService.findById(restaurantId);
        GETResponseDTO response = restaurantService.convertToDtoRestaurant(restaurant);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), response));
    }

    //    POST assign menu items to respective restaurant
    @PostMapping("/{restaurantId}/menu-items")
    public ResponseEntity<Object> assign(@PathVariable Integer restaurantId, @RequestParam String menuItemIds) {
        String response = restaurantService.assignMenuItemToRestaurant(restaurantId, menuItemIds);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), response));
    }

    //    GET menu items of respective restaurant
    @GetMapping("/{restaurantId}/menu-items")
    public ResponseEntity<Object> getAllMenuItems(@PathVariable Integer restaurantId) {
        List<MenuItem> response = restaurantService.getAllMenuItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), response));
    }

    @GetMapping("/{restaurantId}/menu-items/{menuItemId}")
    public ResponseEntity<Object> getMenuItems(@PathVariable Integer restaurantId, @PathVariable Integer menuItemId) {
        MenuItem menuItem = restaurantService.getSelectedMenuItemByRestaurantId(restaurantId, menuItemId);
        GETResponseDTO response = restaurantService.convertToDtoMenuItem(menuItem);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), response));
    }
}