package com.example.schoolproject.controller;

import com.example.schoolproject.dto.ScoreDTO;
import com.example.schoolproject.dto.SubjectAvgDTO;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/child/{childId}")
    public ResponseEntity<List<ScoreDTO>> getScoresByChild(@PathVariable Long childId, @RequestParam Child child) {
        List<ScoreDTO> scores = scoreService.findByChild(child);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/children")
    public ResponseEntity<List<ScoreDTO>> getScoresByChildren(@RequestParam List<Child> children) {
        List<ScoreDTO> scores = scoreService.findByChildren(children);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/averages")
    public ResponseEntity<List<SubjectAvgDTO>> getAvgScoresPerSubjectPerClass() {
        List<SubjectAvgDTO> averages = scoreService.findAvgScorePerSubjectPerClass();
        return ResponseEntity.ok(averages);
    }

    @PostMapping
    public ResponseEntity<ScoreDTO> createScore(@RequestBody ScoreDTO scoreDTO) {
        try {
            ScoreDTO savedScore = scoreService.saveScore(scoreDTO);
            return ResponseEntity.ok(savedScore);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}