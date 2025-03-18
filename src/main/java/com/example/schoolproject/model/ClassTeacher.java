package com.example.schoolproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
public class ClassTeacher extends User implements Serializable {

    @OneToMany(mappedBy = "classTeacher")
    private List<Child> children;
}
