package org.example.catalogservice.Controllers;

import org.example.catalogservice.DTO.RequestDTO;
import org.example.catalogservice.DTO.ResponseDTO;
import org.example.catalogservice.Services.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
