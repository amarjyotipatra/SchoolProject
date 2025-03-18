package com.example.schoolproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ToString(exclude = { "classTeacher", "scores" })
public class Child extends User implements Serializable {

    @ManyToOne
    @JoinColumn(name = "classteacher_id")
    @JsonIgnore
    private ClassTeacher classTeacher;

    @OneToMany(mappedBy = "child")
    private List<Score> scores = new ArrayList<>();
    public Child() {
        setRole(Role.CHILD);
    }
}
