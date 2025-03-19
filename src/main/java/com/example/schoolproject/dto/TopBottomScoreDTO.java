package com.example.schoolproject.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TopBottomScoreDTO implements Serializable {
    private Long id;
    private Double score;
    private Long childId;
    private String childName;
    private Long subjectId;
    private String subjectName;
}