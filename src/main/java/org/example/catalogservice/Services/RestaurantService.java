package org.example.catalogservice.Services;

import jakarta.transaction.Transactional;
import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.Exceptions.*;
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

    public List<MenuItem> getAllMenuItemsByRestaurantId(Integer restaurantId) {
        Restaurant restaurant = findById(restaurantId);
        if (restaurant.getMenu().isEmpty()) {
            throw new MenuItemDoesNotExistException("no menu items found for this restaurant");
        }
        return restaurant.getMenu();
    }

    public MenuItem getSelectedMenuItemByRestaurantId(Integer restaurantId, Integer menuItemId) {
        Restaurant restaurant = findById(restaurantId);
        List<MenuItem> restaurantMenu = restaurant.getMenu();

        for (MenuItem menuItem : restaurantMenu) {
            if (menuItem.getId().equals(menuItemId)) {
                return menuItem;
            }
        }

        throw new RestaurantDoesNotOwnMenuItemException("Restaurant does not own the menu item with id: " + menuItemId);
    }

    public GETResponseDTO convertToDtoRestaurant(Restaurant restaurant) {
        GETResponseDTO dto = new GETResponseDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(Optional.of(restaurant.getAddress()));
        return dto;
    }

    public GETResponseDTO convertToDtoMenuItem(MenuItem menuItem) {
        GETResponseDTO dto = new GETResponseDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setPrice(Optional.of(menuItem.getPrice()));
        return dto;
    }
}

