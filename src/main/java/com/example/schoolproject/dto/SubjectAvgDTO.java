package com.example.schoolproject.dto;

import lombok.Data;

@Data
public class SubjectAvgDTO {
    private Long subjectId;
    private String subjectName;
    private Long classTeacherId;
    private String classTeacherName;
    private Double avgScore;
}
