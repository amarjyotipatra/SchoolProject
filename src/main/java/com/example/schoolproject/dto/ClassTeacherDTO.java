package com.example.schoolproject.dto;

import com.example.schoolproject.model.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ClassTeacherDTO implements Serializable {

    private Long id;
    private String name;
    private User.Role role;
}
