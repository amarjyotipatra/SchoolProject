package com.example.schoolproject.service;

import com.example.schoolproject.dto.ChildDTO;
import com.example.schoolproject.model.Child;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.schoolproject.model.ClassTeacher;
import com.example.schoolproject.repository.ChildRepository;
import com.example.schoolproject.repository.ClassTeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ClassTeacherRepository classTeacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Cacheable(value = "children", key = "#userName")
    public Optional<ChildDTO> findByUserName(String userName) {
        return childRepository.findByUserName(userName)
                .map(this::convertToDTO);
    }

    @Cacheable(value = "children", key = "#userName")
    public Optional<Child> findChildByUserName(String userName) {
        return childRepository.findByUserName(userName);
    }

    @CachePut(value = "children", key = "#result.userName")
    public ChildDTO saveChild(ChildDTO childDTO) {
        // Validate classTeacherId
        if (childDTO.getClassTeacherId() == null) {
            throw new IllegalArgumentException("ClassTeacher ID is required");
        }

        long studentCount = childRepository.countByClassTeacherId(childDTO.getClassTeacherId());
        if (studentCount >= 50) {
            throw new IllegalStateException("Maximum 50 students per ClassTeacher exceeded.");
        }

        Optional<ClassTeacher> classTeacher = classTeacherRepository.findById(childDTO.getClassTeacherId());
        if (classTeacher.isEmpty()) {
            throw new IllegalArgumentException("ClassTeacher with ID " + childDTO.getClassTeacherId() + " not found");
        }

        Child child = convertToEntity(childDTO);
        // Encode password
        child.setPassword(passwordEncoder.encode(childDTO.getPassword()));
        child.setClassTeacher(classTeacher.get()); 
        child = childRepository.save(child);
        return convertToDTO(child);
    }

    private ChildDTO convertToDTO(Child child) {
        ChildDTO dto = new ChildDTO();
        dto.setId(child.getId());
        dto.setName(child.getName());
        dto.setUserName(child.getUserName());
        dto.setPassword(child.getPassword());
        dto.setClassTeacherId(child.getClassTeacher() != null ? child.getClassTeacher().getId() : null);
        dto.setRole(child.getRole());
        return dto;
    }

    private Child convertToEntity(ChildDTO dto) {
        Child child = new Child();
        child.setId(dto.getId());
        child.setName(dto.getName());
        child.setUserName(dto.getUserName());
        child.setPassword(dto.getPassword());
        child.setRole(dto.getRole());
        return child;
    }
}