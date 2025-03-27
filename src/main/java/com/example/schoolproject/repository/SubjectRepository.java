package com.example.schoolproject.repository;

import com.example.schoolproject.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Renamed method to match the field name 'subjectName' in the Subject class
    Optional<Subject> findBySubjectName(String subjectName);
}