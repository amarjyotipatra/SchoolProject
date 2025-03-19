package com.example.schoolproject.dto;

import lombok.Data;

@Data
public class CumulativeAvgDTO {
    private Long classTeacherId;
    private String className;
    private Double avgScore;
}