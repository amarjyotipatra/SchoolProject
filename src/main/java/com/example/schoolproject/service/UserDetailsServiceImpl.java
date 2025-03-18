package com.example.schoolproject.service;

import com.example.schoolproject.model.User;
import com.example.schoolproject.repository.PrincipalRepository;
import com.example.schoolproject.repository.ClassTeacherRepository;
import com.example.schoolproject.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PrincipalRepository principalRepository;

    @Autowired
    private ClassTeacherRepository classTeacherRepository;

    @Autowired
    private ChildRepository childRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = principalRepository.findByUserName(username)
                .map(User.class::cast)
                .or(() -> classTeacherRepository.findByUserName(username).map(User.class::cast))
                .or(() -> childRepository.findByUserName(username).map(User.class::cast));

        return user.map(u -> org.springframework.security.core.userdetails.User
                        .withUsername(u.getUserName())
                        .password(u.getPassword())
                        .roles(u.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}