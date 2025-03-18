package com.example.schoolproject.repository;

import com.example.schoolproject.model.Principal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrincipalRepository extends JpaRepository<Principal, Long> {

    Optional<Principal> findByUserName(String userName);
}
