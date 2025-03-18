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

    @Query("SELECT s.subject.subjectName, AVG(s.score), c.classTeacher.id " +
            "FROM Score s JOIN s.child c GROUP BY s.subject.subjectName, c.classTeacher.id")
    List<Object[]> findAvgScorePerSubjectPerClass();

    @Query("SELECT s.subject.subjectName, AVG(s.score) FROM Score s GROUP BY s.subject.subjectName")
    List<Object[]> findAvgScorePerSubject();

    @Query("SELECT c.name, AVG(s.score) FROM Score s JOIN s.child c GROUP BY c.name")
    List<Object[]> findAvgScorePerStudent();

    @Query("SELECT c.classTeacher.id, AVG(s.score) FROM Score s JOIN s.child c GROUP BY c.classTeacher.id")
    List<Object[]> findAvgScorePerClass();

    @Query("SELECT s.child.id, c.name, s.score, s.subject.subjectName " +
            "FROM Score s JOIN s.child c " +
            "WHERE s.subject.subjectName = ?1 AND c.classTeacher.id = ?2 " +
            "ORDER BY s.score DESC LIMIT 3")
    List<Object[]> findTop3ScoresBySubjectAndClass(String subjectName, Long classTeacherId);

    @Query("SELECT s.child.id, c.name, s.score, s.subject.subjectName " +
            "FROM Score s JOIN s.child c " +
            "WHERE s.subject.subjectName = ?1 AND c.classTeacher.id = ?2 " +
            "ORDER BY s.score ASC LIMIT 3")
    List<Object[]> findBottom3ScoresBySubjectAndClass(String subjectName, Long classTeacherId);

    @Query("SELECT s.child.id, c.name, s.score " +
            "FROM Score s JOIN s.child c " +
            "WHERE s.subject.subjectName = ?1 AND c.classTeacher.id = ?2 " +
            "ORDER BY s.score DESC")
    List<Object[]> findStudentsBySubjectAndClass(String subjectName, Long classTeacherId);

    // Added for Child functionality
    List<Score> findByChild(Child child);

    // Added for ClassTeacher functionality
    @Query("SELECT s FROM Score s WHERE s.child.classTeacher.id = ?1")
    List<Score> findByClassTeacherId(Long classTeacherId);
}