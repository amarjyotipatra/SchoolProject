package com.example.schoolproject.controller;

import com.example.schoolproject.dto.ChildDTO;
import com.example.schoolproject.dto.ClassTeacherDTO;
import com.example.schoolproject.dto.ScoreDTO;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.service.ChildService;
import com.example.schoolproject.service.ClassTeacherService;
import com.example.schoolproject.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/children")
public class ChildController {

    @Autowired
    private ChildService childService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ClassTeacherService classTeacherService;

    @GetMapping("/username/{userName}")
    public ResponseEntity<ChildDTO> getChildByUserName(@PathVariable String userName) {
        Optional<ChildDTO> childDTO = childService.findByUserName(userName);
        return childDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChildDTO> createChild(@RequestBody ChildDTO childDTO) {
        try {
            ChildDTO savedChild = childService.saveChild(childDTO);
            return ResponseEntity.ok(savedChild);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/scores")
    public ResponseEntity<List<ScoreDTO>> getChildScores() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Child> child = childService.findChildByUserName(username);
        return child.map(c -> ResponseEntity.ok(scoreService.findByChild(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/classTeacher")
    public ResponseEntity<ClassTeacherDTO> getClassTeacherDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<ChildDTO> childDTO = childService.findByUserName(username);
        return childDTO.flatMap(dto -> classTeacherService.findById(dto.getClassTeacherId())) // Still an error
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}