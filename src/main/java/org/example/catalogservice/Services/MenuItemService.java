package org.example.catalogservice.Services;

import jakarta.transaction.Transactional;
import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.Exceptions.MenuItemAlreadyAddedException;
import org.example.catalogservice.Exceptions.MenuItemAlreadyAssignedException;
import org.example.catalogservice.Exceptions.MenuItemDoesNotExistException;
import org.example.catalogservice.Exceptions.RestaurantDoesNotExistException;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Models.Restaurant;
import org.example.catalogservice.Repositories.MenuItemRepository;
import org.example.catalogservice.Repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public String addMenuItem(String name, int price) {
        try {
            menuItemRepository.save(new MenuItem(name, price));
            return "menu item added successfully";
        } catch (DataIntegrityViolationException e) {
            throw new MenuItemAlreadyAddedException("menu item already added");
        }
    }

    public MenuItem findById(Integer id) {
        MenuItem menuItem = menuItemRepository.findById(id).orElse(null);
        if (menuItem != null) {
            return menuItem;
        }
        throw new MenuItemDoesNotExistException("menu item does not exist");
    }

    public List<MenuItem> findAllMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        if (menuItems.isEmpty()) {
            throw new MenuItemDoesNotExistException("no menu items found");
        }
        return menuItemRepository.findAll();
    }

    @Transactional
    public String assignMenuItemToRestaurant(int restaurantId, String menuItemIds) {
        Restaurant restaurant = restaurantService.findById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantDoesNotExistException("restaurant does not exist");
        }

        List<Integer> menuItemIdList = Arrays.stream(menuItemIds.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIdList);
        if (menuItems.size() != menuItemIdList.size()) {
            throw new MenuItemDoesNotExistException("one or more menu items do not exist");
        }

        for (MenuItem menuItem : menuItems) {
            if (restaurant.getMenu().contains(menuItem)) {
                throw new MenuItemAlreadyAssignedException("menu item already assigned to restaurant");
            }
        }

        restaurant.getMenu().addAll(menuItems);
        restaurantRepository.save(restaurant);

        return "menu items assigned to restaurant successfully";
    }

    public GETResponseDTO convertToDto(MenuItem menuItem) {
        GETResponseDTO dto = new GETResponseDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setPrice(Optional.of(menuItem.getPrice()));
        return dto;
    }
}
