package com.example.schoolproject.dto;

import com.example.schoolproject.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildDTO {

    private Long id;
    private String name;
    private Long classTeacherId;
    private User.Role role;
}
