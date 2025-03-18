package com.example.schoolproject.service;

import com.example.schoolproject.dto.PrincipalDTO;
import com.example.schoolproject.model.Principal;
import com.example.schoolproject.repository.PrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrincipalService {

    @Autowired
    private PrincipalRepository principalRepository;

    public Optional<PrincipalDTO> findByUserName(String userName) {
        return principalRepository.findByUserName(userName)
                .map(this::convertToDTO);
    }

    public PrincipalDTO savePrincipal(PrincipalDTO principalDTO) {
        Principal principal = convertToEntity(principalDTO);
        principal = principalRepository.save(principal);
        return convertToDTO(principal);
    }

    private PrincipalDTO convertToDTO(Principal principal) {
        PrincipalDTO dto = new PrincipalDTO();
        dto.setId(principal.getId());
        dto.setName(principal.getName());
        dto.setRole(principal.getRole());
        return dto;
    }

    private Principal convertToEntity(PrincipalDTO dto) {
        Principal principal = new Principal();
        principal.setId(dto.getId());
        principal.setName(dto.getName());
        principal.setRole(dto.getRole());
        return principal;
    }
}
