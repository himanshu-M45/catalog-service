package org.example.catalogservice.Services;

import org.example.catalogservice.Exceptions.*;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Models.Restaurant;
import org.example.catalogservice.Repositories.MenuItemRepository;
import org.example.catalogservice.Repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(menuItemRepository.findById(anyInt())).thenReturn(Optional.of(new MenuItem()));
    }

    @Test
    void testAddRestaurant() {
        String name = "Pizza Place";
        String address = "123 Main St";
        Restaurant restaurant = new Restaurant(name, address);

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        String response = restaurantService.addRestaurant(name, address);

        assertEquals("restaurant added successfully", response);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void testAddRestaurantWithInvalidName() {
        String name = "";
        String address = "123 Main St";

        Exception exception = assertThrows(CannotCreateRestaurantException.class, () -> {
            restaurantService.addRestaurant(name, address);
        });

        assertEquals("name and address cannot be null or empty", exception.getMessage());
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void testAddRestaurantWithInvalidAddress() {
        String name = "Pizza Place";
        String address = "";

        Exception exception = assertThrows(CannotCreateRestaurantException.class, () -> {
            restaurantService.addRestaurant(name, address);
        });

        assertEquals("name and address cannot be null or empty", exception.getMessage());
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void testAddDuplicateRestaurantThrowsException() {
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        Exception exception = assertThrows(RestaurantDetailsAlreadyAddedException.class, () -> {
            restaurantService.addRestaurant("Pizza Place", "123 Main St");
        });

        assertEquals("restaurant details already added", exception.getMessage());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void testFindByIdSuccess() {
        when(restaurantRepository.findById(anyInt())).thenReturn(Optional.of(new Restaurant()));

        Restaurant foundRestaurant = restaurantService.findById(1);

        assertNotNull(foundRestaurant);
        verify(restaurantRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        when(restaurantRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RestaurantDoesNotExistException.class, () -> {
            restaurantService.findById(420);
        });

        assertEquals("restaurant does not exist", exception.getMessage());
    }

    @Test
    void testFindAllRestaurantsSuccess() {
        Restaurant restaurant = new Restaurant("Pizza Place", "123 Main St");
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        List<Restaurant> restaurants = restaurantService.findAllRestaurants();

        assertNotNull(restaurants);
        assertEquals(1, restaurants.size());
        assertEquals("Pizza Place", restaurants.get(0).getName());
        verify(restaurantRepository, times(2)).findAll();
    }

    @Test
    void testFindAllRestaurantsNotFound() {
        when(restaurantRepository.findAll()).thenReturn(List.of());

        Exception exception = assertThrows(RestaurantDoesNotExistException.class, () -> {
            restaurantService.findAllRestaurants();
        });

        assertEquals("no restaurants found", exception.getMessage());
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    void testAssignMenuItemToRestaurantSuccess() {
        Restaurant restaurant = new Restaurant();
        MenuItem menuItem1 = new MenuItem("Pizza", 100);
        MenuItem menuItem2 = new MenuItem("Burger", 50);

        when(restaurantRepository.findById(anyInt())).thenReturn(Optional.of(restaurant));
        when(menuItemService.findAllById(anyList())).thenReturn(List.of(menuItem1, menuItem2));

        String response = restaurantService.assignMenuItemToRestaurant(1, "1,2");

        assertEquals("menu items assigned to restaurant successfully", response);
        verify(restaurantRepository, times(1)).findById(1);
        verify(menuItemService, times(1)).findAllById(List.of(1, 2));
    }

    @Test
    void testAssignMenuItemToRestaurantRestaurantNotFound() {
        when(restaurantRepository.findById(anyInt()))
                .thenThrow(new RestaurantDoesNotExistException("restaurant does not exist"));

        Exception exception = assertThrows(RestaurantDoesNotExistException.class, () -> {
            restaurantService.assignMenuItemToRestaurant(1, "1,2");
        });

        assertEquals("restaurant does not exist", exception.getMessage());
        verify(restaurantRepository, times(1)).findById(1);
        verify(menuItemService, never()).findAllById(anyList());
    }

    @Test
    void testAssignMenuItemToRestaurantMenuItemNotFound() {
        Restaurant restaurant = new Restaurant();
        when(restaurantRepository.findById(anyInt())).thenReturn(Optional.of(restaurant));
        when(menuItemService.findAllById(anyList()))
                .thenThrow(new MenuItemDoesNotExistException("one or more menu items do not exist"));

        Exception exception = assertThrows(MenuItemDoesNotExistException.class, () -> {
            restaurantService.assignMenuItemToRestaurant(1, "1,2");
        });

        assertEquals("one or more menu items do not exist", exception.getMessage());
        verify(restaurantRepository, times(1)).findById(1);
        verify(menuItemService, times(1)).findAllById(List.of(1, 2));
    }

    @Test
    void testAssignMenuItemToRestaurantMenuItemAlreadyAssigned() {
        Restaurant restaurant = new Restaurant();
        MenuItem menuItem = new MenuItem("Pizza", 100);
        restaurant.getMenu().add(menuItem);

        when(restaurantRepository.findById(anyInt())).thenReturn(Optional.of(restaurant));
        when(menuItemService.findAllById(anyList())).thenReturn(List.of(menuItem));

        Exception exception = assertThrows(MenuItemAlreadyAssignedException.class, () -> {
            restaurantService.assignMenuItemToRestaurant(1, "1");
        });

        assertEquals("menu item already assigned to restaurant", exception.getMessage());
        verify(restaurantRepository, times(1)).findById(1);
        verify(menuItemService, times(1)).findAllById(List.of(1));
    }

    @Test
    void testGetMenuItemsByRestaurantIdSuccess() {
        Restaurant restaurant = new Restaurant();
        MenuItem menuItem1 = new MenuItem("Pizza", 100);
        MenuItem menuItem2 = new MenuItem("Burger", 50);
        restaurant.getMenu().add(menuItem1);
        restaurant.getMenu().add(menuItem2);

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        List<MenuItem> result = restaurantService.getMenuItemsByRestaurantId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getName());
        assertEquals("Burger", result.get(1).getName());
        verify(restaurantRepository, times(1)).findById(1);
    }

    @Test
    void testGetMenuItemsByRestaurantIdRestaurantNotFound() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RestaurantDoesNotExistException.class, () -> {
            restaurantService.getMenuItemsByRestaurantId(1);
        });

        assertEquals("restaurant does not exist", exception.getMessage());
        verify(restaurantRepository, times(1)).findById(1);
    }

    @Test
    void testGetMenuItemsByRestaurantIdNoMenuItems() {
        Restaurant restaurant = new Restaurant();

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        Exception exception = assertThrows(MenuItemDoesNotExistException.class, () -> {
            restaurantService.getMenuItemsByRestaurantId(1);
        });

        assertEquals("no menu items found for this restaurant", exception.getMessage());
        verify(restaurantRepository, times(1)).findById(1);
    }

    @Test
    void testGetSelectedMenuItemsByIdSuccess() {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getId()).thenReturn(1);
        when(menuItem.getName()).thenReturn("Pizza");
        when(menuItem.getPrice()).thenReturn(100);
        Restaurant restaurant = new Restaurant();
        restaurant.getMenu().add(menuItem);

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        List<MenuItem> result = restaurantService.getSelectedMenuItemsById(1, "1");

        assertNotNull(result);
        assertEquals(1, result.get(0).getId());
        assertEquals("Pizza", result.get(0).getName());
        verify(restaurantRepository, times(1)).findById(1);
    }

    @Test
    void testGetSelectedMenuItemsByIdRestaurantNotFound() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RestaurantDoesNotExistException.class, () -> {
            restaurantService.getSelectedMenuItemsById(1, "1");
        });

        verify(restaurantRepository, times(1)).findById(1);
    }

    @Test
    void testGetSelectedMenuItemByIdMenuItemsNotFound() {
        Restaurant restaurant = new Restaurant();

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        assertThrows(MenuItemDoesNotExistException.class, () -> {
            restaurantService.getSelectedMenuItemsById(1, "1");
        });

        verify(restaurantRepository, times(1)).findById(1);
    }
}