package com.example.schoolproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StudentScoreDTO implements Serializable {
    private Long childId;
    private String childName;
    private Double score;
}