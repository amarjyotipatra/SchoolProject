package com.example.schoolproject.repository;

import com.example.schoolproject.dto.SubjectAvgDTO;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByChild(Child child);

    List<Score> findByChildIn(List<Child> children);

    @Query("SELECT new com.example.schoolproject.dto.SubjectAvgDTO(s.subject.subjectName, AVG(s.score), s.child.classTeacher.id) " +
            "FROM Score s GROUP BY s.subject.subjectName, s.child.classTeacher.id")
    List<SubjectAvgDTO> findAvgScorePerSubjectPerClass();
}
