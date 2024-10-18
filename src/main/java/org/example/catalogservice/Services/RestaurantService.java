package org.example.catalogservice.Services;

import org.example.catalogservice.Exceptions.RestaurantDetailsAlreadyAddedException;
import org.example.catalogservice.Models.Restaurant;
import org.example.catalogservice.Repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;;

    public String addRestaurant(String name, String address) {
        try {
            restaurantRepository.save(new Restaurant(name, address));
            return "restaurant added successfully";
        } catch (DataIntegrityViolationException e) {
            throw new RestaurantDetailsAlreadyAddedException("restaurant details already added");
        }
    }
}
