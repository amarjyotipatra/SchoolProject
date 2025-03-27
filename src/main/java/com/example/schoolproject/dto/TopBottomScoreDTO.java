package com.example.schoolproject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class TopBottomScoreDTO implements Serializable {
    private Long id;
    private Double score;
    private Long childId;
    private String childName;
    private Long subjectId;
    private String subjectName;
}