package com.example.schoolproject.controller;

import com.example.schoolproject.dto.*;
import com.example.schoolproject.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;

    @GetMapping("/child/{childId}")
    @PreAuthorize("hasRole('CHILD') or hasRole('CLASS_TEACHER') or hasRole('PRINCIPAL')")
    public ResponseEntity<List<ScoreDTO>> getScoresByChildId(@PathVariable Long childId) {
        List<ScoreDTO> scores = scoreService.findByChildId(childId);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/averages")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<SubjectAvgDTO>> getAvgScoresPerSubjectPerClass() {
        List<SubjectAvgDTO> averages = scoreService.findAvgScorePerSubjectPerClass();
        return ResponseEntity.ok(averages);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('CLASS_TEACHER')")
    public ResponseEntity<ScoreDTO> createScore(@RequestBody ScoreDTO scoreDTO) {
        ScoreDTO savedScore = scoreService.saveScore(scoreDTO);
        return ResponseEntity.ok(savedScore);
    }

    @GetMapping("/cumulative-averages")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<CumulativeAvgDTO>> getCumulativeAverages() {
        List<CumulativeAvgDTO> averages = scoreService.getCumulativeAverages();
        return ResponseEntity.ok(averages);
    }
    
    @GetMapping("/top3/{subjectName}/{classTeacherId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<TopBottomScoreDTO>> getTop3Scores(
            @PathVariable String subjectName, 
            @PathVariable Long classTeacherId) {
        List<TopBottomScoreDTO> topScores = scoreService.findTop3ScoresBySubjectAndClass(subjectName, classTeacherId);
        return ResponseEntity.ok(topScores);
    }
    
    @GetMapping("/bottom3/{subjectName}/{classTeacherId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<TopBottomScoreDTO>> getBottom3Scores(
            @PathVariable String subjectName, 
            @PathVariable Long classTeacherId) {
        List<TopBottomScoreDTO> bottomScores = scoreService.findBottom3ScoresBySubjectAndClass(subjectName, classTeacherId);
        return ResponseEntity.ok(bottomScores);
    }

    @GetMapping("/students/{subjectName}/{classTeacherId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<StudentScoreDTO>> getStudentsSorted(
            @PathVariable String subjectName, 
            @PathVariable Long classTeacherId) {
        List<StudentScoreDTO> students = scoreService.getStudentsBySubjectAndClassSorted(subjectName, classTeacherId);
        return ResponseEntity.ok(students);
    }
}