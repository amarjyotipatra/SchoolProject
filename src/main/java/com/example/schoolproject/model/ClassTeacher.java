package com.example.schoolproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = { "children" })
public class ClassTeacher extends User implements Serializable {

    @OneToMany(mappedBy = "classTeacher")
    private List<Child> children = new ArrayList<>();
    public ClassTeacher() {
        setRole(Role.CLASS_TEACHER);
    }
}
