package com.example.schoolproject.service;

import com.example.schoolproject.dto.*;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.model.Score;
import com.example.schoolproject.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Cacheable(value = "scores", key = "#child.id")
    public List<ScoreDTO> findByChild(Child child) {
        return scoreRepository.findByChild(child)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "scores", key = "#classTeacherId")
    public List<ScoreDTO> findByClassTeacherId(Long classTeacherId) {
        return scoreRepository.findByClassTeacherId(classTeacherId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "averages")
    public List<SubjectAvgDTO> findAvgScorePerSubjectPerClass() {
        return scoreRepository.findAvgScorePerSubjectPerClass()
                .stream()
                .map(result -> new SubjectAvgDTO((String) result[0], (Double) result[1], (Long) result[2]))
                .collect(Collectors.toList());
    }

    @CachePut(value = "scores", key = "#result.childId")
    public ScoreDTO saveScore(ScoreDTO scoreDTO) {
        Score score = convertToEntity(scoreDTO);
        score = scoreRepository.save(score);
        return convertToDTO(score);
    }

    @Cacheable(value = "cumulativeAverages")
    public List<CumulativeAvgDTO> getCumulativeAverages() {
        List<CumulativeAvgDTO> averages = new ArrayList<>();

        // Subjects
        scoreRepository.findAvgScorePerSubject().forEach(result -> {
            CumulativeAvgDTO dto = new CumulativeAvgDTO();
            dto.setCategory("Subject");
            dto.setName((String) result[0]);
            dto.setAverage((Double) result[1]);
            averages.add(dto);
        });

        // Students
        scoreRepository.findAvgScorePerStudent().forEach(result -> {
            CumulativeAvgDTO dto = new CumulativeAvgDTO();
            dto.setCategory("Student");
            dto.setName((String) result[0]);
            dto.setAverage((Double) result[1]);
            averages.add(dto);
        });

        // Classes
        scoreRepository.findAvgScorePerClass().forEach(result -> {
            CumulativeAvgDTO dto = new CumulativeAvgDTO();
            dto.setCategory("Class");
            dto.setName("Class " + result[0]);
            dto.setAverage((Double) result[1]);
            averages.add(dto);
        });

        return averages;
    }

    @Cacheable(value = "top3Scores")
    public List<TopBottomScoreDTO> getTop3ScoresBySubjectAndClass(String subjectName, Long classTeacherId) {
        return scoreRepository.findTop3ScoresBySubjectAndClass(subjectName, classTeacherId)
                .stream()
                .map(result -> {
                    TopBottomScoreDTO dto = new TopBottomScoreDTO();
                    dto.setChildId((Long) result[0]);
                    dto.setChildName((String) result[1]);
                    dto.setScore((Double) result[2]);
                    dto.setSubjectName((String) result[3]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Cacheable(value = "bottom3Scores")
    public List<TopBottomScoreDTO> getBottom3ScoresBySubjectAndClass(String subjectName, Long classTeacherId) {
        return scoreRepository.findBottom3ScoresBySubjectAndClass(subjectName, classTeacherId)
                .stream()
                .map(result -> {
                    TopBottomScoreDTO dto = new TopBottomScoreDTO();
                    dto.setChildId((Long) result[0]);
                    dto.setChildName((String) result[1]);
                    dto.setScore((Double) result[2]);
                    dto.setSubjectName((String) result[3]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Cacheable(value = "studentsBySubject")
    public List<StudentScoreDTO> getStudentsBySubjectAndClassSorted(String subjectName, Long classTeacherId) {
        return scoreRepository.findStudentsBySubjectAndClass(subjectName, classTeacherId)
                .stream()
                .map(result -> {
                    StudentScoreDTO dto = new StudentScoreDTO();
                    dto.setChildId((Long) result[0]);
                    dto.setChildName((String) result[1]);
                    dto.setScore((Double) result[2]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ScoreDTO convertToDTO(Score score) {
        ScoreDTO dto = new ScoreDTO();
        dto.setId(score.getId());
        dto.setScore(score.getScore());
        dto.setChildId(score.getChild() != null ? score.getChild().getId() : null);
        dto.setSubjectId(score.getSubject() != null ? score.getSubject().getId() : null);
        return dto;
    }

    private Score convertToEntity(ScoreDTO dto) {
        Score score = new Score();
        score.setId(dto.getId());
        score.setScore(dto.getScore());
        return score;
    }
}