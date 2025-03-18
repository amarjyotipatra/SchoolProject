package com.example.schoolproject.dto;

import com.example.schoolproject.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrincipalDTO {

    private Long id;
    private String name;
    private User.Role role;
}
