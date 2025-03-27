package com.example.schoolproject.service;

import com.example.schoolproject.dto.ChildDTO;
import com.example.schoolproject.model.Child;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.schoolproject.model.ClassTeacher;
import com.example.schoolproject.repository.ChildRepository;
import com.example.schoolproject.repository.ClassTeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException; // Import for unique constraint handling

import java.util.Optional;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ClassTeacherRepository classTeacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Cache lookup by ID
    @Cacheable(value = "childrenById", key = "#id")
    public Optional<ChildDTO> findById(Long id) { // Added missing method
        return childRepository.findById(id)
                .map(this::convertToDTO);
    }

    // Cache lookup by Username
    @Cacheable(value = "childrenByUsername", key = "#userName") // Separate cache for username lookups
    public Optional<ChildDTO> findByUserName(String userName) {
        return childRepository.findByUserName(userName)
                .map(this::convertToDTO);
    }

    // No caching here, just find the raw entity for internal use
    public Optional<Child> findChildByUserName(String userName) {
        return childRepository.findByUserName(userName);
    }

    @CachePut(value = "childrenById", key = "#result.id") // Update ID cache on save/update
    @CacheEvict(value = "childrenByUsername", key = "#result.userName") // Evict username cache on save/update
    public ChildDTO saveChild(ChildDTO childDTO) {
        if (childDTO == null) {
             throw new IllegalArgumentException("Child data cannot be null");
        }
        // Validate classTeacherId
        if (childDTO.getClassTeacherId() == null) {
            throw new IllegalArgumentException("ClassTeacher ID is required");
        }

        long studentCount = childRepository.countByClassTeacherId(childDTO.getClassTeacherId());
        if (studentCount >= 50) {
            throw new IllegalStateException("Maximum 50 students per ClassTeacher exceeded.");
        }

        ClassTeacher classTeacher = classTeacherRepository.findById(childDTO.getClassTeacherId())
            .orElseThrow(() -> new EntityNotFoundException("ClassTeacher with ID " + childDTO.getClassTeacherId() + " not found"));

        Child child;
        if (childDTO.getId() != null) {
            // Attempt to update existing child
            child = childRepository.findById(childDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Child not found with ID: " + childDTO.getId() + " for update."));
            // Update fields (consider if username/role should be updatable)
            child.setName(childDTO.getName());
            child.setUserName(childDTO.getUserName()); // Be cautious if username should be unique and immutable
        } else {
            // Create new child
            child = new Child();
            child.setName(childDTO.getName());
            child.setUserName(childDTO.getUserName());
            // Set password only for new child creation
            if (childDTO.getPassword() == null || childDTO.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password is required for new child creation");
            }
            child.setPassword(passwordEncoder.encode(childDTO.getPassword()));
        }

        child.setClassTeacher(classTeacher);
        // Ensure role is always set correctly
        child.setRole(com.example.schoolproject.model.User.Role.CHILD);

        try {
             child = childRepository.save(child);
        } catch (DataIntegrityViolationException e) {
             // Catch potential unique constraint violation (e.g., username)
             throw new DataIntegrityViolationException("Username '" + childDTO.getUserName() + "' already exists.", e);
        }
        return convertToDTO(child);
    }

    private ChildDTO convertToDTO(Child child) {
        ChildDTO dto = new ChildDTO();
        dto.setId(child.getId());
        dto.setName(child.getName());
        dto.setUserName(child.getUserName());
        // *** CRITICAL: Do NOT expose encoded password in DTO ***
        // dto.setPassword(child.getPassword());
        dto.setClassTeacherId(child.getClassTeacher() != null ? child.getClassTeacher().getId() : null);
        dto.setRole(child.getRole());
        return dto;
    }

    // Primarily for converting DTO to a *new* Entity, updates handled in saveChild
    private Child convertToEntity(ChildDTO dto) {
        Child child = new Child();
        // ID is handled in saveChild for update vs create logic
        // child.setId(dto.getId());
        child.setName(dto.getName());
        child.setUserName(dto.getUserName());
        // Password set during save operation after encoding
        // Role set during save or based on logic
        // ClassTeacher is set separately in save method
        return child;
    }
}