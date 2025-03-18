package com.example.schoolproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreDTO {

    private Long id;
    private double score;
    private Long childId;
    private Long subjectId;
}
