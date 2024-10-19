package org.example.catalogservice.Controllers;

import org.example.catalogservice.DTO.GETResponseDTO;
import org.example.catalogservice.DTO.RequestDTO;
import org.example.catalogservice.DTO.ResponseDTO;
import org.example.catalogservice.Models.MenuItem;
import org.example.catalogservice.Models.Restaurant;
import org.example.catalogservice.Services.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-items")
public class MenuItemController {
    @Autowired
    private MenuItemService menuItemService;

    @PostMapping("")
    public ResponseEntity<Object> addMenuItem(@RequestBody RequestDTO requestDTO) {
        String response = menuItemService.addMenuItem(requestDTO.getName(), requestDTO.getPrice());
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.CREATED.value(), response));
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllMenuItems() {
        List<MenuItem> menuItem = menuItemService.findAllMenuItems();
        List<GETResponseDTO> GETResponseDTOS = menuItem.stream()
                .map(menuItemService::convertToDto)
                .toList();
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), GETResponseDTOS));
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<Object> getMenuItemById(@PathVariable Integer menuItemId) {
        MenuItem menuItem = menuItemService.findById(menuItemId);
        GETResponseDTO GETResponseDTO = menuItemService.convertToDto(menuItem);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), GETResponseDTO));
    }
}
