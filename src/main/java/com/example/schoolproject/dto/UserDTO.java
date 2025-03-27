package com.example.schoolproject.dto;

import com.example.schoolproject.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String name;
    private String userName;
    private User.Role role;
}
