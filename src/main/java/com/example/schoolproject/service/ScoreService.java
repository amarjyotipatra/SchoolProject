package com.example.schoolproject.service;

import com.example.schoolproject.dto.*;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.model.Score;
import com.example.schoolproject.model.Subject;
import com.example.schoolproject.repository.ChildRepository;
import com.example.schoolproject.repository.ClassTeacherRepository;
import com.example.schoolproject.repository.ScoreRepository;
import com.example.schoolproject.repository.SubjectRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest; // Import PageRequest
import org.springframework.data.domain.Pageable;   // Import Pageable
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    // ... (other autowired fields remain the same) ...
    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassTeacherRepository classTeacherRepository;


    // Find scores by child id
    // Cache key should likely be unique, maybe related to child username if that's unique? Or use childId directly.
    @Cacheable(value = "scoresByChild", key = "#childId")
    public List<ScoreDTO> findByChildId(Long childId) {
        if (!childRepository.existsById(childId)) {
            throw new EntityNotFoundException("Child not found with ID: " + childId);
        }

        return scoreRepository.findByChildId(childId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Save a new score
    // Caching strategy needs review: What identifies a unique score to update/evict? Composite key?
    // @CachePut(value = "scores", key = "#result.id") // Simple ID might be ok if scores are immutable once created
    @CacheEvict(value = {"scoresByChild", "scoresByTeacher", "cumulativeAveragesCache", "subjectAvgCache"}, allEntries = true) // Evict related caches on change
    public ScoreDTO saveScore(ScoreDTO scoreDTO) {
        if (scoreDTO == null) {
            throw new IllegalArgumentException("ScoreDTO cannot be null");
        }
        if (scoreDTO.getScore() == null) {
            throw new IllegalArgumentException("Score value cannot be null");
        }
        if (scoreDTO.getScore() < 0 || scoreDTO.getScore() > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        if (scoreDTO.getChildId() == null || scoreDTO.getSubjectId() == null) {
            throw new IllegalArgumentException("Child ID and Subject ID are required");
        }

        Child child = childRepository.findById(scoreDTO.getChildId())
             .orElseThrow(() -> new EntityNotFoundException("Child not found with ID: " + scoreDTO.getChildId()));

        Subject subject = subjectRepository.findById(scoreDTO.getSubjectId())
             .orElseThrow(() -> new EntityNotFoundException("Subject not found with ID: " + scoreDTO.getSubjectId()));

        // Optional: Check if a score for this child and subject already exists if they should be unique
        // Optional<Score> existingScore = scoreRepository.findByChildIdAndSubjectId(scoreDTO.getChildId(), scoreDTO.getSubjectId());
        // if(existingScore.isPresent()) { throw new IllegalStateException("Score already exists for this child and subject."); }

        Score score = new Score();
        score.setScore(scoreDTO.getScore());
        score.setChild(child);
        score.setSubject(subject);
        score = scoreRepository.save(score);
        return convertToDTO(score);
    }

    // Get average scores per subject per class
    @Cacheable(value = "subjectAvgCache")
    public List<SubjectAvgDTO> findAvgScorePerSubjectPerClass() {
        List<SubjectAvgDTO> result = new ArrayList<>();
        List<Subject> subjects = subjectRepository.findAll();
        List<com.example.schoolproject.model.ClassTeacher> classTeachers = classTeacherRepository.findAll(); // Fetch all teachers once

        for (Subject subject : subjects) {
            for (com.example.schoolproject.model.ClassTeacher classTeacher : classTeachers) {
                // Use the explicit @Query method from the repository
                Double avgScore = scoreRepository.findAvgScoreBySubjectIdAndClassTeacherId(
                    subject.getId(), classTeacher.getId());

                if (avgScore != null) {
                    SubjectAvgDTO dto = new SubjectAvgDTO();
                    dto.setSubjectId(subject.getId());
                    dto.setSubjectName(subject.getSubjectName()); // Use getSubjectName()
                    dto.setClassTeacherId(classTeacher.getId());
                    dto.setClassTeacherName(classTeacher.getName());
                    dto.setAvgScore(avgScore);
                    result.add(dto);
                }
            }
        }

        return result;
    }

    // Get top 3 scores per subject per class
    public List<TopBottomScoreDTO> findTop3ScoresBySubjectAndClass(String subjectName, Long classTeacherId) {
        Subject subject = subjectRepository.findBySubjectName(subjectName) // Use findBySubjectName
            .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + subjectName));

        if (!classTeacherRepository.existsById(classTeacherId)) {
            throw new EntityNotFoundException("Class teacher not found with ID: " + classTeacherId);
        }

        // Use Pageable to limit results
        Pageable top3 = PageRequest.of(0, 3);
        List<Score> scores = scoreRepository.findTopScoresBySubjectIdAndClassTeacherId(
            subject.getId(), classTeacherId, top3); // Pass Pageable

        return scores.stream().map(score -> {
            TopBottomScoreDTO dto = new TopBottomScoreDTO();
            dto.setId(score.getId());
            dto.setScore(score.getScore());
            dto.setChildId(score.getChild().getId());
            dto.setChildName(score.getChild().getName());
            dto.setSubjectId(score.getSubject().getId());
            dto.setSubjectName(score.getSubject().getSubjectName()); // Use getSubjectName()
            return dto;
        }).collect(Collectors.toList());
    }

    // Get bottom 3 scores per subject per class
    public List<TopBottomScoreDTO> findBottom3ScoresBySubjectAndClass(String subjectName, Long classTeacherId) {
        Subject subject = subjectRepository.findBySubjectName(subjectName) // Use findBySubjectName
            .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + subjectName));

        if (!classTeacherRepository.existsById(classTeacherId)) {
            throw new EntityNotFoundException("Class teacher not found with ID: " + classTeacherId);
        }

        // Use Pageable to limit results
        Pageable bottom3 = PageRequest.of(0, 3);
        List<Score> scores = scoreRepository.findBottomScoresBySubjectIdAndClassTeacherId(
            subject.getId(), classTeacherId, bottom3); // Pass Pageable

        return scores.stream().map(score -> {
            TopBottomScoreDTO dto = new TopBottomScoreDTO();
            dto.setId(score.getId());
            dto.setScore(score.getScore());
            dto.setChildId(score.getChild().getId());
            dto.setChildName(score.getChild().getName());
            dto.setSubjectId(score.getSubject().getId());
            dto.setSubjectName(score.getSubject().getSubjectName()); // Use getSubjectName()
            return dto;
        }).collect(Collectors.toList());
    }

    // Get students sorted by score in a particular subject and class
    public List<StudentScoreDTO> getStudentsBySubjectAndClassSorted(String subjectName, Long classTeacherId) {
        // Validate subject exists (optional but good practice)
         Subject subject = subjectRepository.findBySubjectName(subjectName)
            .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + subjectName));

        if (!classTeacherRepository.existsById(classTeacherId)) {
            throw new EntityNotFoundException("Class teacher not found with ID: " + classTeacherId);
        }

        // Use the explicit @Query method
        List<Score> scores = scoreRepository.findBySubjectNameAndClassTeacherId(subjectName, classTeacherId);

        return scores.stream()
            .sorted(Comparator.comparing(Score::getScore).reversed()) // Sort descending
            .map(score -> {
                StudentScoreDTO dto = new StudentScoreDTO();
                dto.setChildId(score.getChild().getId());
                dto.setChildName(score.getChild().getName());
                dto.setScore(score.getScore());
                return dto;
            })
            .collect(Collectors.toList());
    }

    // Get cumulative averages across subjects for chart data
    @Cacheable(value = "cumulativeAveragesCache")
    public List<CumulativeAvgDTO> getCumulativeAverages() {
        List<CumulativeAvgDTO> result = new ArrayList<>();
        List<com.example.schoolproject.model.ClassTeacher> classTeachers = classTeacherRepository.findAll();

        for (com.example.schoolproject.model.ClassTeacher teacher : classTeachers) {
            // Use the explicit @Query method
            List<Score> classScores = scoreRepository.findByClassTeacherId(teacher.getId());

            if (!classScores.isEmpty()) {
                double avgScore = classScores.stream()
                    .mapToDouble(Score::getScore)
                    .average()
                    .orElse(0.0); // Handle case where stream is empty after filtering (shouldn't happen here)

                CumulativeAvgDTO dto = new CumulativeAvgDTO();
                dto.setClassTeacherId(teacher.getId());
                dto.setClassName(teacher.getName()); // Assuming ClassTeacher has a name relevant for class name
                dto.setAvgScore(avgScore);
                result.add(dto);
            } else {
                 // Optionally add an entry with 0 average if the class exists but has no scores
                 CumulativeAvgDTO dto = new CumulativeAvgDTO();
                 dto.setClassTeacherId(teacher.getId());
                 dto.setClassName(teacher.getName());
                 dto.setAvgScore(0.0);
                 result.add(dto);
            }
        }

        return result;
    }

    // Get data formatted for a simple chart (e.g., Chart.js)
    public Map<String, Object> getCumulativeAverageChartData() {
        Map<String, Object> chartData = new HashMap<>();
        List<CumulativeAvgDTO> cumulativeAvgs = getCumulativeAverages(); // Use the cached method

        // Extract labels (class names) and data (average scores)
        List<String> labels = cumulativeAvgs.stream()
                                            .map(CumulativeAvgDTO::getClassName)
                                            .collect(Collectors.toList());
        List<Double> data = cumulativeAvgs.stream()
                                          .map(CumulativeAvgDTO::getAvgScore)
                                          .collect(Collectors.toList());

        chartData.put("labels", labels);
        chartData.put("data", data);

        return chartData;
    }

    // Find scores by ClassTeacher ID
    @Cacheable(value = "scoresByTeacher", key = "#classTeacherId")
    public List<ScoreDTO> findByClassTeacherId(Long classTeacherId) {
        // Check if the class teacher exists
        if (!classTeacherRepository.existsById(classTeacherId)) {
            throw new EntityNotFoundException("Class teacher not found with ID: " + classTeacherId);
        }

        // Use the explicit @Query method
        return scoreRepository.findByClassTeacherId(classTeacherId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert Score entity to ScoreDTO
    private ScoreDTO convertToDTO(Score score) {
        ScoreDTO dto = new ScoreDTO();
        dto.setId(score.getId());
        dto.setScore(score.getScore());
        dto.setChildId(score.getChild().getId());
        dto.setChildName(score.getChild().getName());
        dto.setSubjectId(score.getSubject().getId());
        dto.setSubjectName(score.getSubject().getSubjectName()); // Use getSubjectName()
        return dto;
    }

     // Find scores by Child entity (used internally by ChildController potentially)
     // Consider if this needs caching separately
    public List<ScoreDTO> findByChild(Child child) {
        if (child == null) {
             throw new IllegalArgumentException("Child cannot be null");
        }
        return scoreRepository.findByChildId(child.getId()).stream()
                 .map(this::convertToDTO)
                 .collect(Collectors.toList());
    }
}