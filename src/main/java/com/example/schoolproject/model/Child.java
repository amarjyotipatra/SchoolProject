package com.example.schoolproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
public class Child extends User implements Serializable {

    @ManyToOne
    @JoinColumn(name = "classteacher_id")
    @JsonIgnore
    private ClassTeacher classTeacher;

    @OneToMany(mappedBy = "child")
    private List<Score> scores;
}
