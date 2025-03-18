package com.example.schoolproject.service;

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
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    @CachePut(value = "subjects", key = "#subject.id")
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
}