package com.example.schoolproject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SubjectAvgDTO {
    private Long subjectId;
    private String subjectName;
    private Long classTeacherId;
    private String classTeacherName;
    private Double avgScore;
}
