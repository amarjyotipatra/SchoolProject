package com.example.schoolproject.repository;

import com.example.schoolproject.model.ClassTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Long> {

    Optional<ClassTeacher> findByUserName(String userName);
}
