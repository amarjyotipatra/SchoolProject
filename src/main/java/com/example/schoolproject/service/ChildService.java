package com.example.schoolproject.service;

import com.example.schoolproject.dto.ChildDTO;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    public Optional<ChildDTO> findByUserName(String userName) {
        return childRepository.findByUserName(userName)
                .map(this::convertToDTO);
    }

    public ChildDTO saveChild(ChildDTO childDTO) {
        Child child = convertToEntity(childDTO);
        child = childRepository.save(child);
        return convertToDTO(child);
    }

    private ChildDTO convertToDTO(Child child) {
        ChildDTO dto = new ChildDTO();
        dto.setId(child.getId());
        dto.setName(child.getName());
        dto.setClassTeacherId(child.getClassTeacher() != null ? child.getClassTeacher().getId() : null);
        dto.setRole(child.getRole());
        return dto;
    }

    private Child convertToEntity(ChildDTO dto) {
        Child child = new Child();
        child.setId(dto.getId());
        child.setName(dto.getName());
        child.setRole(dto.getRole());
        return child;
    }
}