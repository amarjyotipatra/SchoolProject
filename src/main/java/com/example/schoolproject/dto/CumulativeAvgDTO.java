package com.example.schoolproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CumulativeAvgDTO implements Serializable {
    private String category;
    private String name;
    private Double average;
}