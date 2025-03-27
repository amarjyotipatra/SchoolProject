package com.example.schoolproject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StudentScoreDTO {
    private Long childId;
    private String childName;
    private Double score;
}