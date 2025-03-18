package com.example.schoolproject.controller;

import com.example.schoolproject.dto.ClassTeacherDTO;
import com.example.schoolproject.service.ClassTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/classTeachers")
public class ClassTeacherController {

    @Autowired
    private ClassTeacherService classTeacherService;

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
}