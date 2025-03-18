package com.example.schoolproject.controller;

import com.example.schoolproject.dto.PrincipalDTO;
import com.example.schoolproject.service.PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/principals")
public class PrincipalController {

    @Autowired
    private PrincipalService principalService;

    @GetMapping("/username/{userName}")
    public ResponseEntity<PrincipalDTO> getPrincipalByUserName(@PathVariable String userName) {
        Optional<PrincipalDTO> principalDTO = principalService.findByUserName(userName);
        return principalDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PrincipalDTO> createPrincipal(@RequestBody PrincipalDTO principalDTO) {
        try {
            PrincipalDTO savedPrincipal = principalService.savePrincipal(principalDTO);
            return ResponseEntity.ok(savedPrincipal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}