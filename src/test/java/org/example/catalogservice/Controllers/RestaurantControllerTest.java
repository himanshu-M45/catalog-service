package org.example.catalogservice.Controllers;

import org.example.catalogservice.Exceptions.CannotCreateRestaurantException;
import org.example.catalogservice.Exceptions.CustomExceptionHandler;
import org.example.catalogservice.Exceptions.RestaurantDetailsAlreadyAddedException;
import org.example.catalogservice.Services.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestaurantControllerTest {
    private MockMvc mockMvc;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    void testAddRestaurantSuccess() throws Exception {
        when(restaurantService.addRestaurant(anyString(), anyString())).thenReturn("restaurant added successfully");

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pizza Place\", \"address\": \"123 Main St\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data").value("restaurant added successfully"));

        verify(restaurantService, times(1)).addRestaurant("Pizza Place", "123 Main St");
    }

    @Test
    void testAddAlreadyAddedRestaurantBadRequestThrowsException() throws Exception {
        when(restaurantService.addRestaurant(anyString(), anyString()))
                .thenThrow(new RestaurantDetailsAlreadyAddedException("restaurant details already added"));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pizza Place\", \"address\": \"123 Main St\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.data").value("restaurant details already added"));

        verify(restaurantService, times(1)).addRestaurant("Pizza Place", "123 Main St");
    }

    @Test
    void testAddRestaurantWithoutNameBadRequest() throws Exception {
        when(restaurantService.addRestaurant(anyString(), anyString()))
                .thenThrow(new CannotCreateRestaurantException("name and address cannot be null or empty"));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"address\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.data").value("name and address cannot be null or empty"));

        verify(restaurantService, times(1)).addRestaurant("", "");
    }

}