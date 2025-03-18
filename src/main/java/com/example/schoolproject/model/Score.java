package com.example.schoolproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = { "child", "subject" })
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double score;

    @ManyToOne
    @JoinColumn(name = "child_id")
    @JsonIgnore
    private Child child;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
}
