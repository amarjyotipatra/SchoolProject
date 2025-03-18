package com.example.schoolproject.service;

import com.example.schoolproject.dto.ScoreDTO;
import com.example.schoolproject.dto.SubjectAvgDTO;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.model.Score;
import com.example.schoolproject.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public List<ScoreDTO> findByChild(Child child) {
        return scoreRepository.findByChild(child)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ScoreDTO> findByChildren(List<Child> children) {
        return scoreRepository.findByChildIn(children)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SubjectAvgDTO> findAvgScorePerSubjectPerClass() {
        return scoreRepository.findAvgScorePerSubjectPerClass();
    }

    public ScoreDTO saveScore(ScoreDTO scoreDTO) {
        Score score = convertToEntity(scoreDTO);
        score = scoreRepository.save(score);
        return convertToDTO(score);
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