package com.example.schoolproject.controller;

import com.example.schoolproject.dto.*;
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

    @GetMapping("/cumulative-averages")
    public ResponseEntity<List<CumulativeAvgDTO>> getCumulativeAverages() {
        List<CumulativeAvgDTO> averages = scoreService.getCumulativeAverages();
        return ResponseEntity.ok(averages);
    }

    @GetMapping("/top3/{subjectName}/{classTeacherId}")
    public ResponseEntity<List<TopBottomScoreDTO>> getTop3Scores(@PathVariable String subjectName, @PathVariable Long classTeacherId) {
        List<TopBottomScoreDTO> topScores = scoreService.getTop3ScoresBySubjectAndClass(subjectName, classTeacherId);
        return ResponseEntity.ok(topScores);
    }

    @GetMapping("/bottom3/{subjectName}/{classTeacherId}")
    public ResponseEntity<List<TopBottomScoreDTO>> getBottom3Scores(@PathVariable String subjectName, @PathVariable Long classTeacherId) {
        List<TopBottomScoreDTO> bottomScores = scoreService.getBottom3ScoresBySubjectAndClass(subjectName, classTeacherId);
        return ResponseEntity.ok(bottomScores);
    }

    @GetMapping("/students/{subjectName}/{classTeacherId}")
    public ResponseEntity<List<StudentScoreDTO>> getStudentsSorted(@PathVariable String subjectName, @PathVariable Long classTeacherId) {
        List<StudentScoreDTO> students = scoreService.getStudentsBySubjectAndClassSorted(subjectName, classTeacherId);
        return ResponseEntity.ok(students);
    }
}