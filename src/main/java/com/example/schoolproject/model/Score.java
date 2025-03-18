package com.example.schoolproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double score;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
}
