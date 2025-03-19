package com.example.schoolproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubjectDTO implements Serializable {

    private Long id;
    private String subjectName;
}