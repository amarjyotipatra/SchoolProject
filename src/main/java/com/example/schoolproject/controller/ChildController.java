package com.example.schoolproject.controller;

import com.example.schoolproject.dto.ChildDTO;
import com.example.schoolproject.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/children")
public class ChildController {

    @Autowired
    private ChildService childService;

    @GetMapping("/username/{userName}")
    public ResponseEntity<ChildDTO> getChildByUserName(@PathVariable String userName) {
        Optional<ChildDTO> childDTO = childService.findByUserName(userName);
        return childDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChildDTO> createChild(@RequestBody ChildDTO childDTO) {
        try {
            ChildDTO savedChild = childService.saveChild(childDTO);
            return ResponseEntity.ok(savedChild);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}