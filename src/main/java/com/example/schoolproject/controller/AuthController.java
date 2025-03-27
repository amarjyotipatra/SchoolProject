package com.example.schoolproject.controller;

import com.example.schoolproject.dto.LoginDTO;
import com.example.schoolproject.dto.UserDTO;
import com.example.schoolproject.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginDTO loginDTO) {
        UserDTO userDTO = authService.authenticate(loginDTO.getUserName(), loginDTO.getPassword());
        return ResponseEntity.ok(userDTO);
    }
}
