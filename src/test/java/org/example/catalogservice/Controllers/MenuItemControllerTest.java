package org.example.catalogservice.Controllers;

import org.example.catalogservice.Exceptions.CannotCreateRestaurantException;
import org.example.catalogservice.Exceptions.CustomExceptionHandler;
import org.example.catalogservice.Exceptions.RestaurantDetailsAlreadyAddedException;
import org.example.catalogservice.Services.MenuItemService;
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

class MenuItemControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private MenuItemController menuItemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(menuItemController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    void tesAddMenuItemSuccess() throws Exception {
        when(menuItemService.addMenuItem(anyString(), anyInt())).thenReturn("menu item added successfully");

        mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Margherita Pizza\", \"price\": \"80\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data").value("menu item added successfully"));

        verify(menuItemService, times(1)).addMenuItem("Margherita Pizza", 80);
    }

    @Test
    void testAddAlreadyAddedMenuItemBadRequestThrowsException() throws Exception {
        when(menuItemService.addMenuItem(anyString(), anyInt()))
                .thenThrow(new RestaurantDetailsAlreadyAddedException("menu item already added"));

        mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Margherita Pizza\", \"price\": \"80\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.data").value("menu item already added"));

        verify(menuItemService, times(1)).addMenuItem("Margherita Pizza", 80);
    }

    @Test
    void testAddMenuItemWithoutNameBadRequest() throws Exception {
        when(menuItemService.addMenuItem(anyString(), anyInt()))
                .thenThrow(new CannotCreateRestaurantException("name cannot be null or empty and price cannot be less than or equal to 0"));

        mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"price\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.data")
                        .value("name cannot be null or empty and price cannot be less than or equal to 0"));

        verify(menuItemService, times(1)).addMenuItem("", 0);
    }
}