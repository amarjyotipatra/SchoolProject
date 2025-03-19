package com.example.schoolproject.controller;

import com.example.schoolproject.dto.ChildDTO;
import com.example.schoolproject.dto.ClassTeacherDTO;
import com.example.schoolproject.dto.ScoreDTO;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.model.ClassTeacher;
import com.example.schoolproject.service.ChildService;
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
@RequestMapping("/api/children")
public class ChildController {

    @Autowired
    private ChildService childService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ClassTeacherService classTeacherService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHILD','CLASS_TEACHER','PRINCIPAL')")
    public ResponseEntity<ChildDTO> getChildById(@PathVariable Long id) {
        Optional<ChildDTO> childDTO = childService.findById(id);
        return childDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{userName}")
    @PreAuthorize("hasAnyRole('CHILD','CLASS_TEACHER','PRINCIPAL')")
    public ResponseEntity<ChildDTO> getChildByUserName(@PathVariable String userName) {
        Optional<ChildDTO> childDTO = childService.findByUserName(userName);
        return childDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createChild(@RequestBody ChildDTO childDTO) {
        try {
            ChildDTO savedChild = childService.saveChild(childDTO);
            return ResponseEntity.ok(savedChild);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/scores")
    public ResponseEntity<List<ScoreDTO>> getChildScores(@RequestParam String userName) {
        Optional<Child> child = childService.findChildByUserName(userName);
        return child.map(c -> ResponseEntity.ok(scoreService.findByChild(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/classTeacher")
    @PreAuthorize("hasRole('CHILD')")
    public ResponseEntity<ClassTeacherDTO> getClassTeacherDetails(@RequestParam String userName) {
        Optional<Child> child = childService.findChildByUserName(userName);
        if (child.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ClassTeacher teacher = child.get().getClassTeacher();
        if (teacher == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(classTeacherService.findById(teacher.getId()).orElseThrow());
    }
}