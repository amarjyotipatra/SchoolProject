package com.example.schoolproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectAvgDTO {

    private String subjectName;
    private double avgScore;
    private Long classTeacherId;

    public SubjectAvgDTO() {
    }

    // Constructor matching the query
    public SubjectAvgDTO(String subjectName, Double avgScore, Long classTeacherId) {
        this.subjectName = subjectName;
        this.avgScore = avgScore;
        this.classTeacherId = classTeacherId;
    }
}
