package com.example.schoolproject.repository;

import com.example.schoolproject.model.Score;
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    // Find scores for a specific child
    List<Score> findByChildId(Long childId);

    // Explicit query to find average score for a subject in a specific class teacher's class
    @Query("SELECT AVG(s.score) FROM Score s JOIN s.child c WHERE s.subject.id = :subjectId AND c.classTeacher.id = :classTeacherId")
    Double findAvgScoreBySubjectIdAndClassTeacherId(@Param("subjectId") Long subjectId, @Param("classTeacherId") Long classTeacherId);

    // Explicit query for Top N scores using Pageable for portability
    @Query("SELECT s FROM Score s JOIN s.child c WHERE s.subject.id = :subjectId AND c.classTeacher.id = :classTeacherId ORDER BY s.score DESC")
    List<Score> findTopScoresBySubjectIdAndClassTeacherId(@Param("subjectId") Long subjectId, @Param("classTeacherId") Long classTeacherId, Pageable pageable);

    // Explicit query for Bottom N scores using Pageable for portability
    @Query("SELECT s FROM Score s JOIN s.child c WHERE s.subject.id = :subjectId AND c.classTeacher.id = :classTeacherId ORDER BY s.score ASC")
    List<Score> findBottomScoresBySubjectIdAndClassTeacherId(@Param("subjectId") Long subjectId, @Param("classTeacherId") Long classTeacherId, Pageable pageable);

    // Explicit query to find scores by subject name and class teacher ID
    @Query("SELECT s FROM Score s JOIN s.child c JOIN s.subject subj WHERE subj.subjectName = :subjectName AND c.classTeacher.id = :classTeacherId")
    List<Score> findBySubjectNameAndClassTeacherId(@Param("subjectName") String subjectName, @Param("classTeacherId") Long classTeacherId);

    // Explicit query to find all scores for children managed by a specific class teacher
    @Query("SELECT s FROM Score s JOIN s.child c WHERE c.classTeacher.id = :classTeacherId")
    List<Score> findByClassTeacherId(@Param("classTeacherId") Long classTeacherId);
}