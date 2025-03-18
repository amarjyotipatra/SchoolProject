package com.example.schoolproject.controller;

import com.example.schoolproject.dto.ClassTeacherDTO;
import com.example.schoolproject.dto.ScoreDTO;
import com.example.schoolproject.service.ClassTeacherService;
import com.example.schoolproject.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classTeachers")
public class ClassTeacherController {

    @Autowired
    private ClassTeacherService classTeacherService;

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/username/{userName}")
    public ResponseEntity<ClassTeacherDTO> getClassTeacherByUserName(@PathVariable String userName) {
        Optional<ClassTeacherDTO> classTeacherDTO = classTeacherService.findByUserName(userName);
        return classTeacherDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClassTeacherDTO> createClassTeacher(@RequestBody ClassTeacherDTO classTeacherDTO) {
        try {
            ClassTeacherDTO savedTeacher = classTeacherService.saveClassTeacher(classTeacherDTO);
            return ResponseEntity.ok(savedTeacher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/scores")
    public ResponseEntity<List<ScoreDTO>> getClassScores() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<ClassTeacherDTO> teacherDTO = classTeacherService.findByUserName(username);
        return teacherDTO.map(dto -> ResponseEntity.ok(scoreService.findByClassTeacherId(dto.getId())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}