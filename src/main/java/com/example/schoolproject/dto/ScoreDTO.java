package com.example.schoolproject.dto;

import lombok.Data;

@Data
public class ScoreDTO {
    private Long id;
    private Double score;
    private Long childId;
    private String childName;
    private Long subjectId;
    private String subjectName;
}
