package com.example.schoolproject.repository;

import com.example.schoolproject.model.ClassTeacher;
import com.example.schoolproject.model.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    Optional<Child> findByUserName(String userName);
    
    @Query("SELECT COUNT(c) FROM Child c WHERE c.classTeacher.id = :classTeacherId")
    long countByClassTeacherId(@Param("classTeacherId") Long classTeacherId);
    List<Child> findByClassTeacher(ClassTeacher classTeacher);
}
