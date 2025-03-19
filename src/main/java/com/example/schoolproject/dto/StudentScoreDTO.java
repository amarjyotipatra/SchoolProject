package com.example.schoolproject.dto;

import lombok.Data;

@Data
public class StudentScoreDTO {
    private Long childId;
    private String childName;
    private Double score;
}