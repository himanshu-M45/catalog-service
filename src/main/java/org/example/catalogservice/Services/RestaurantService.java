package org.example.catalogservice.Services;

import jakarta.transaction.Transactional;
import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.Exceptions.MenuItemAlreadyAssignedException;
import org.example.catalogservice.Exceptions.MenuItemDoesNotExistException;
import org.example.catalogservice.Exceptions.RestaurantDetailsAlreadyAddedException;
import org.example.catalogservice.Exceptions.RestaurantDoesNotExistException;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Models.Restaurant;
import org.example.catalogservice.Repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private MenuItemService menuItemService;

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

    public List<MenuItem> getMenuItemsByRestaurantId(Integer restaurantId) {
        Restaurant restaurant = findById(restaurantId);
        return restaurant.getMenu();
    }

    @Transactional
    public String assignMenuItemToRestaurant(int restaurantId, String menuItemIds) {
        Restaurant restaurant = findById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantDoesNotExistException("restaurant does not exist");
        }

        List<Integer> menuItemIdList = Arrays.stream(menuItemIds.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<MenuItem> menuItems = menuItemService.findAllById(menuItemIdList);

        for (MenuItem menuItem : menuItems) {
            if (restaurant.getMenu().contains(menuItem)) {
                throw new MenuItemAlreadyAssignedException("menu item already assigned to restaurant");
            }
        }

        restaurant.getMenu().addAll(menuItems);
        restaurantRepository.save(restaurant);

        return "menu items assigned to restaurant successfully";
    }

    public GETResponseDTO convertToDto(Restaurant restaurant) {
        GETResponseDTO dto = new GETResponseDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(Optional.ofNullable(restaurant.getAddress()));
        return dto;
    }
}

