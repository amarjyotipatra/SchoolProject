package com.example.schoolproject.repository;

import com.example.schoolproject.model.Child;
import com.example.schoolproject.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    @Query("SELECT s.subject.subjectName, AVG(s.score), s.child.classTeacher.id " +
            "FROM Score s " +
            "GROUP BY s.subject.subjectName, s.child.classTeacher.id")
    List<Object[]> findAvgScorePerSubjectPerClass();

    // Top 3 scores by subject and class (still returns List<Object[]> for now, as it needs multiple fields)
    @Query("SELECT s.child.id, s.child.name, s.score, s.subject.subjectName " +
            "FROM Score s " +
            "WHERE s.subject.subjectName = ?1 AND s.child.classTeacher.id = ?2 " +
            "ORDER BY s.score DESC LIMIT 3")
    List<Object[]> findTop3ScoresBySubjectAndClass(String subjectName, Long classTeacherId);

    // Bottom 3 scores by subject and class (still returns List<Object[]> for now)
    @Query("SELECT s.child.id, s.child.name, s.score, s.subject.subjectName " +
            "FROM Score s " +
            "WHERE s.subject.subjectName = ?1 AND s.child.classTeacher.id = ?2 " +
            "ORDER BY s.score ASC LIMIT 3")
    List<Object[]> findBottom3ScoresBySubjectAndClass(String subjectName, Long classTeacherId);

    // Corrected query to fetch student details and their scores for a specific subject and class teacher
    @Query("SELECT s.child.id, s.child.name, s.score " +
            "FROM Score s " +
            "WHERE s.subject.subjectName = ?1 AND s.child.classTeacher.id = ?2 " +
            "ORDER BY s.score DESC")
    List<Object[]> findStudentsBySubjectNameAndClassTeacherId(String subjectName, Long classTeacherId);

    // Added for Child functionality
    List<Score> findByChild(Child child);

    // Added for ClassTeacher functionality
    @Query("SELECT s FROM Score s WHERE s.child.classTeacher.id = ?1")
    List<Score> findByClassTeacherId(Long classTeacherId);

    @Query("SELECT AVG(s.score) FROM Score s WHERE s.subject.id = :subjectId")
    Double findAvgScorePerSubject(@Param("subjectId") Long subjectId);

    @Query("SELECT AVG(s.score) FROM Score s WHERE s.child.id = :studentId AND s.child.classTeacher.id = :classTeacherId")
    Double findAvgScorePerStudent(@Param("studentId") Long studentId, @Param("classTeacherId") Long classTeacherId);

    @Query("SELECT AVG(s.score) FROM Score s WHERE s.child.classTeacher.id = :classTeacherId")
    Double findAvgScorePerClass(@Param("classTeacherId") Long classTeacherId);

    List<Score> findByChildId(Long childId);

    // Find average score per subject 
    @Query("SELECT AVG(s.score) FROM Score s WHERE s.subject.id = :subjectId")
    Double findAvgScoreBySubjectId(@Param("subjectId") Long subjectId);

    // Find average score per subject per class
    @Query("SELECT AVG(s.score) FROM Score s WHERE s.subject.id = :subjectId AND s.child.classTeacher.id = :classTeacherId")
    Double findAvgScoreBySubjectIdAndClassTeacherId(@Param("subjectId") Long subjectId, 
                                                  @Param("classTeacherId") Long classTeacherId);

    // Find top 3 scores per subject per class
    @Query(value = "SELECT * FROM scores s " +
           "JOIN children c ON s.child_id = c.id " +
           "WHERE s.subject_id = :subjectId AND c.class_teacher_id = :classTeacherId " +
           "ORDER BY s.score DESC LIMIT 3", nativeQuery = true)
    List<Score> findTop3ScoresBySubjectIdAndClassTeacherId(@Param("subjectId") Long subjectId, 
                                                         @Param("classTeacherId") Long classTeacherId);

    // Find bottom 3 scores per subject per class
    @Query(value = "SELECT * FROM scores s " +
           "JOIN children c ON s.child_id = c.id " +
           "WHERE s.subject_id = :subjectId AND c.class_teacher_id = :classTeacherId " +
           "ORDER BY s.score ASC LIMIT 3", nativeQuery = true)
    List<Score> findBottom3ScoresBySubjectIdAndClassTeacherId(@Param("subjectId") Long subjectId, 
                                                            @Param("classTeacherId") Long classTeacherId);

    // Find scores by subject name and class teacher id
    @Query("SELECT s FROM Score s WHERE s.subject.name = :subjectName AND s.child.classTeacher.id = :classTeacherId")
    List<Score> findBySubjectNameAndClassTeacherId(@Param("subjectName") String subjectName, 
                                                 @Param("classTeacherId") Long classTeacherId);
}