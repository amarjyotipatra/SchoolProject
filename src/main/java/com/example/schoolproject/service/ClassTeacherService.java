package com.example.schoolproject.service;

import com.example.schoolproject.dto.ClassTeacherDTO;
import com.example.schoolproject.model.ClassTeacher;
import com.example.schoolproject.repository.ClassTeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClassTeacherService {

    @Autowired
    private ClassTeacherRepository classTeacherRepository;

    public Optional<ClassTeacherDTO> findByUserName(String userName) {
        return classTeacherRepository.findByUserName(userName)
                .map(this::convertToDTO);
    }

    public ClassTeacherDTO saveClassTeacher(ClassTeacherDTO classTeacherDTO) {
        ClassTeacher classTeacher = convertToEntity(classTeacherDTO);
        classTeacher = classTeacherRepository.save(classTeacher);
        return convertToDTO(classTeacher);
    }

    private ClassTeacherDTO convertToDTO(ClassTeacher classTeacher) {
        ClassTeacherDTO dto = new ClassTeacherDTO();
        dto.setId(classTeacher.getId());
        dto.setName(classTeacher.getName());
        dto.setRole(classTeacher.getRole());
        return dto;
    }

    private ClassTeacher convertToEntity(ClassTeacherDTO dto) {
        ClassTeacher classTeacher = new ClassTeacher();
        classTeacher.setId(dto.getId());
        classTeacher.setName(dto.getName());
        classTeacher.setRole(dto.getRole());
        return classTeacher;
    }
}