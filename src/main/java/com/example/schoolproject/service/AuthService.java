package com.example.schoolproject.service;

import com.example.schoolproject.dto.UserDTO;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.model.ClassTeacher;
import com.example.schoolproject.model.Principal;
import com.example.schoolproject.model.User;
import com.example.schoolproject.repository.ChildRepository;
import com.example.schoolproject.repository.ClassTeacherRepository;
import com.example.schoolproject.repository.PrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private ChildRepository childRepository;
    
    @Autowired
    private ClassTeacherRepository classTeacherRepository;
    
    @Autowired
    private PrincipalRepository principalRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserDTO authenticate(String userName, String password) {
        // Try to find user in any of the repositories
        Optional<Child> childOpt = childRepository.findByUserName(userName);
        if (childOpt.isPresent() && passwordEncoder.matches(password, childOpt.get().getPassword())) {
            return convertToUserDTO(childOpt.get());
        }
        
        Optional<ClassTeacher> teacherOpt = classTeacherRepository.findByUserName(userName);
        if (teacherOpt.isPresent() && passwordEncoder.matches(password, teacherOpt.get().getPassword())) {
            return convertToUserDTO(teacherOpt.get());
        }
        
        Optional<Principal> principalOpt = principalRepository.findByUserName(userName);
        if (principalOpt.isPresent() && passwordEncoder.matches(password, principalOpt.get().getPassword())) {
            return convertToUserDTO(principalOpt.get());
        }
        
        throw new BadCredentialsException("Invalid username or password");
    }
    
    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUserName(user.getUserName());
        dto.setRole(user.getRole());
        return dto;
    }
}
