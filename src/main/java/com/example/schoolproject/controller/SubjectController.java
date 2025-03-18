package com.example.schoolproject.controller;

import com.example.schoolproject.model.Subject;
import com.example.schoolproject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        Optional<Subject> subject = subjectService.findById(id);
        return subject.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        try {
            Subject savedSubject = subjectService.saveSubject(subject);
            return ResponseEntity.ok(savedSubject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}