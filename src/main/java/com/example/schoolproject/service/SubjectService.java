package com.example.schoolproject.service;

import com.example.schoolproject.dto.SubjectDTO;
import com.example.schoolproject.model.Subject;
import com.example.schoolproject.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Cacheable(value = "subjects", key = "#id")
    public Optional<SubjectDTO> findById(Long id) {
        return subjectRepository.findById(id)
                .map(this::convertToDTO);
    }

    @CachePut(value = "subjects", key = "#result.id")
    public SubjectDTO saveSubject(SubjectDTO subjectDTO) {
        Subject subject = convertToEntity(subjectDTO);
        subject = subjectRepository.save(subject);
        return convertToDTO(subject);
    }

    private SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        return dto;
    }

    private Subject convertToEntity(SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setId(dto.getId());
        subject.setSubjectName(dto.getSubjectName());
        return subject;
    }
}