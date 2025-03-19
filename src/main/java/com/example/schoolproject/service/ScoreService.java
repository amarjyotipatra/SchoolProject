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
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private ClassTeacherRepository classTeacherRepository;
    
    // Find scores by child id
    @Cacheable(value = "scores", key = "#childId")
    public List<ScoreDTO> findByChildId(Long childId) {
        if (!childRepository.existsById(childId)) {
            throw new EntityNotFoundException("Child not found with ID: " + childId);
        }
        
        return scoreRepository.findByChildId(childId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Save a new score
    @CachePut(value = "scores", key = "#result.childId")
    public ScoreDTO saveScore(ScoreDTO scoreDTO) {
        if (scoreDTO == null) {
            throw new IllegalArgumentException("ScoreDTO cannot be null");
        }
        if (scoreDTO.getScore() == null) {
            throw new IllegalArgumentException("Score cannot be null");
        }
        if (scoreDTO.getScore() < 0 || scoreDTO.getScore() > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        if (scoreDTO.getChildId() == null || scoreDTO.getSubjectId() == null) {
            throw new IllegalArgumentException("Child ID and Subject ID are required");
        }

        Optional<Child> child = childRepository.findById(scoreDTO.getChildId());
        Optional<Subject> subject = subjectRepository.findById(scoreDTO.getSubjectId());

        if (child.isEmpty()) {
            throw new EntityNotFoundException("Child not found with ID: " + scoreDTO.getChildId());
        }
        if (subject.isEmpty()) {
            throw new EntityNotFoundException("Subject not found with ID: " + scoreDTO.getSubjectId());
        }

        Score score = new Score();
        score.setScore(scoreDTO.getScore());
        score.setChild(child.get());
        score.setSubject(subject.get());
        score = scoreRepository.save(score);
        return convertToDTO(score);
    }
    
    // Get average scores per subject per class
    public List<SubjectAvgDTO> findAvgScorePerSubjectPerClass() {
        List<SubjectAvgDTO> result = new ArrayList<>();
        List<Subject> subjects = subjectRepository.findAll();
        
        for (Subject subject : subjects) {
            List<com.example.schoolproject.model.ClassTeacher> classTeachers = classTeacherRepository.findAll();
            for (com.example.schoolproject.model.ClassTeacher classTeacher : classTeachers) {
                Double avgScore = scoreRepository.findAvgScoreBySubjectIdAndClassTeacherId(
                    subject.getId(), classTeacher.getId());
                
                if (avgScore != null) {
                    SubjectAvgDTO dto = new SubjectAvgDTO();
                    dto.setSubjectId(subject.getId());
                    dto.setSubjectName(subject.getName());
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
        Subject subject = subjectRepository.findByName(subjectName)
            .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + subjectName));
            
        if (!classTeacherRepository.existsById(classTeacherId)) {
            throw new EntityNotFoundException("Class teacher not found with ID: " + classTeacherId);
        }
        
        List<Score> scores = scoreRepository.findTop3ScoresBySubjectIdAndClassTeacherId(
            subject.getId(), classTeacherId);
            
        return scores.stream().map(score -> {
            TopBottomScoreDTO dto = new TopBottomScoreDTO();
            dto.setId(score.getId());
            dto.setScore(score.getScore());
            dto.setChildId(score.getChild().getId());
            dto.setChildName(score.getChild().getName());
            dto.setSubjectId(score.getSubject().getId());
            dto.setSubjectName(score.getSubject().getName());
            return dto;
        }).collect(Collectors.toList());
    }
    
    // Get bottom 3 scores per subject per class
    public List<TopBottomScoreDTO> findBottom3ScoresBySubjectAndClass(String subjectName, Long classTeacherId) {
        Subject subject = subjectRepository.findByName(subjectName)
            .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + subjectName));
            
        if (!classTeacherRepository.existsById(classTeacherId)) {
            throw new EntityNotFoundException("Class teacher not found with ID: " + classTeacherId);
        }
        
        List<Score> scores = scoreRepository.findBottom3ScoresBySubjectIdAndClassTeacherId(
            subject.getId(), classTeacherId);
            
        return scores.stream().map(score -> {
            TopBottomScoreDTO dto = new TopBottomScoreDTO();
            dto.setId(score.getId());
            dto.setScore(score.getScore());
            dto.setChildId(score.getChild().getId());
            dto.setChildName(score.getChild().getName());
            dto.setSubjectId(score.getSubject().getId());
            dto.setSubjectName(score.getSubject().getName());
            return dto;
        }).collect(Collectors.toList());
    }
    
    // Get students sorted by score in a particular subject
    public List<StudentScoreDTO> getStudentsBySubjectAndClassSorted(String subjectName, Long classTeacherId) {
        if (!classTeacherRepository.existsById(classTeacherId)) {
            throw new EntityNotFoundException("Class teacher not found with ID: " + classTeacherId);
        }
        
        List<Score> scores = scoreRepository.findBySubjectNameAndClassTeacherId(subjectName, classTeacherId);
        
        return scores.stream()
            .sorted(Comparator.comparing(Score::getScore).reversed())
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
    public List<CumulativeAvgDTO> getCumulativeAverages() {
        List<CumulativeAvgDTO> result = new ArrayList<>();
        List<com.example.schoolproject.model.ClassTeacher> classTeachers = classTeacherRepository.findAll();
        
        for (com.example.schoolproject.model.ClassTeacher teacher : classTeachers) {
            CumulativeAvgDTO dto = new CumulativeAvgDTO();
            dto.setClassTeacherId(teacher.getId());
            dto.setClassName(teacher.getName());
            
            // Calculate average across all subjects and students in this class
            List<Score> classScores = scoreRepository.findByClassTeacherId(teacher.getId());
            if (!classScores.isEmpty()) {
                double avgScore = classScores.stream()
                    .mapToDouble(Score::getScore)
                    .average()
                    .orElse(0.0);
                dto.setAvgScore(avgScore);
                result.add(dto);
            }
        }
        
        return result;
    }
    
    // Find scores by ClassTeacher ID
    public List<ScoreDTO> findByClassTeacherId(Long classTeacherId) {
        // Check if the class teacher exists
        if (!classTeacherRepository.existsById(classTeacherId)) {
            throw new EntityNotFoundException("Class teacher not found with ID: " + classTeacherId);
        }
        
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
        dto.setSubjectName(score.getSubject().getName());
        return dto;
    }
}