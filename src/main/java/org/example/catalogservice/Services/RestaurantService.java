package org.example.catalogservice.Services;

import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.Exceptions.RestaurantDetailsAlreadyAddedException;
import org.example.catalogservice.Exceptions.RestaurantDoesNotExistException;
import org.example.catalogservice.Models.Restaurant;
import org.example.catalogservice.Repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    public String addRestaurant(String name, String address) {
        try {
            restaurantRepository.save(new Restaurant(name, address));
            return "restaurant added successfully";
        } catch (DataIntegrityViolationException e) {
            throw new RestaurantDetailsAlreadyAddedException("restaurant details already added");
        }
    }

    public Restaurant findById(Integer id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        if (restaurant != null) {
            return restaurant;
        }
        throw new RestaurantDoesNotExistException("restaurant does not exist");
    }

    public List<Restaurant> findAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        if (restaurants.isEmpty()) {
            throw new RestaurantDoesNotExistException("no restaurants found");
        }
        return restaurantRepository.findAll();
    }

    public GETResponseDTO convertToDto(Restaurant restaurant) {
        GETResponseDTO dto = new GETResponseDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(Optional.ofNullable(restaurant.getAddress()));
        return dto;
    }
}

