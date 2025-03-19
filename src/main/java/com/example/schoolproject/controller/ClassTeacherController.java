package com.example.schoolproject.controller;

import com.example.schoolproject.dto.ClassTeacherDTO;
import com.example.schoolproject.dto.ScoreDTO;
import com.example.schoolproject.service.ClassTeacherService;
import com.example.schoolproject.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLASS_TEACHER','PRINCIPAL')")
    public ResponseEntity<ClassTeacherDTO> getClassTeacherById(@PathVariable Long id) {
        Optional<ClassTeacherDTO> classTeacherDTO = classTeacherService.findById(id);
        return classTeacherDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/username/{userName}")
    @PreAuthorize("hasAnyRole('CLASS_TEACHER','PRINCIPAL','CHILD')")
    public ResponseEntity<ClassTeacherDTO> getClassTeacherByUserName(@PathVariable String userName) {
        Optional<ClassTeacherDTO> classTeacherDTO = classTeacherService.findByUserName(userName);
        return classTeacherDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createClassTeacher(@RequestBody ClassTeacherDTO classTeacherDTO) {
        try {
            ClassTeacherDTO savedTeacher = classTeacherService.saveClassTeacher(classTeacherDTO);
            return ResponseEntity.ok(savedTeacher);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
    
    @GetMapping("/scores")
    @PreAuthorize("hasRole('CLASS_TEACHER')")
    public ResponseEntity<List<ScoreDTO>> getClassScores(@RequestParam String userName) {
        Optional<ClassTeacherDTO> teacherDTO = classTeacherService.findByUserName(userName);
        return teacherDTO.map(dto -> ResponseEntity.ok(scoreService.findByClassTeacherId(dto.getId())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}