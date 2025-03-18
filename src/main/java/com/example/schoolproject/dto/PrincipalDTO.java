package com.example.schoolproject.dto;

import com.example.schoolproject.model.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PrincipalDTO implements Serializable {

    private Long id;
    private String name;
    private String userName;
    private String password;
    private User.Role role;
}
