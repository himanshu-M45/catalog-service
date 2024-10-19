package org.example.catalogservice.Services;

import org.example.catalogservice.Exceptions.CannotCreateRestaurantException;
import org.example.catalogservice.Exceptions.RestaurantDetailsAlreadyAddedException;
import org.example.catalogservice.Exceptions.RestaurantDoesNotExistException;
import org.example.catalogservice.Models.Restaurant;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
}