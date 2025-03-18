package com.example.schoolproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ScoreDTO implements Serializable {

    private Long id;
    private double score;
    private Long childId;
    private Long subjectId;
}
