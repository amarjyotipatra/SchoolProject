package com.example.schoolproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TopBottomScoreDTO implements Serializable {
    private Long childId;
    private String childName;
    private Double score;
    private String subjectName;
}