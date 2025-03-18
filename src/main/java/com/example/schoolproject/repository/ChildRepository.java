package com.example.schoolproject.repository;

import com.example.schoolproject.model.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    Optional<Child> findByUserName(String userName);
    long countByClassTeacherId(Long classTeacherId);
}
