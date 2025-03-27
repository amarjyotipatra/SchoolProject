package com.example.schoolproject.controller;

import com.example.schoolproject.dto.ChildDTO;
import com.example.schoolproject.dto.ClassTeacherDTO;
import com.example.schoolproject.dto.ScoreDTO;
import com.example.schoolproject.model.Child;
import com.example.schoolproject.model.ClassTeacher;
import com.example.schoolproject.model.User; // Import User for PrincipalDetails
import com.example.schoolproject.service.ChildService;
import com.example.schoolproject.service.ClassTeacherService;
import com.example.schoolproject.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/children")
public class ChildController {

    @Autowired
    private ChildService childService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ClassTeacherService classTeacherService;

    // Helper method to get authenticated username
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) { // Basic Auth might return username as String
             return (String) principal;
        }
        // Handle other principal types if necessary
        return null; // Or throw an exception if username is always expected
    }

     // Helper method to get authenticated user ID (assuming UserDetails holds it or you have a custom principal)
    private Long getAuthenticatedUserId() {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
             return null;
        }
        Object principal = authentication.getPrincipal();
        // This assumes your UserDetailsService loads a custom principal that has getId()
        // or you adapt it based on how you store the ID in the security context
        /*
        if (principal instanceof YourCustomUserDetails) {
            return ((YourCustomUserDetails) principal).getId();
        }
        */
        // Placeholder: If ID isn't easily accessible, rely on username checks or adjust Principal details
        return null;
    }


    @GetMapping("/{id}")
    // Allow PRINCIPAL/CLASS_TEACHER to get any child by ID.
    // CHILD can only get their own details *if* ID is reliably available in principal.
    // If not, CHILD should use the /username/{username} endpoint.
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('CLASS_TEACHER')") // Simplified for now, Child access by ID needs reliable principal ID
    // @PreAuthorize("hasRole('PRINCIPAL') or hasRole('CLASS_TEACHER') or (hasRole('CHILD') and #id == principal.id)") // Ideal if principal.id works
    public ResponseEntity<ChildDTO> getChildById(@PathVariable Long id) {
        // Add check: Teacher can only see children in their class
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLASS_TEACHER"))) {
            String teacherUsername = getAuthenticatedUsername();
            Optional<ChildDTO> childOpt = childService.findById(id);
            if (childOpt.isPresent()) {
                Optional<ClassTeacherDTO> teacherOpt = classTeacherService.findById(childOpt.get().getClassTeacherId());
                if (teacherOpt.isEmpty() || !teacherOpt.get().getUserName().equals(teacherUsername)) {
                    throw new AccessDeniedException("Class Teacher can only view details of children in their own class.");
                }
            } else {
                 return ResponseEntity.notFound().build(); // Child not found anyway
            }
        }

        Optional<ChildDTO> childDTO = childService.findById(id);
        return childDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{userName}")
    // Allow PRINCIPAL/CLASS_TEACHER to get any child by username, CHILD can only get their own.
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('CLASS_TEACHER') or (hasRole('CHILD') and #userName == authentication.principal.username)")
    public ResponseEntity<ChildDTO> getChildByUserName(@PathVariable String userName) {
         // Add check: Teacher can only see children in their class
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLASS_TEACHER"))) {
            String teacherUsername = getAuthenticatedUsername();
            Optional<Child> childOpt = childService.findChildByUserName(userName); // Fetch entity to check teacher
            if (childOpt.isPresent()) {
                ClassTeacher teacher = childOpt.get().getClassTeacher();
                if (teacher == null || !teacher.getUserName().equals(teacherUsername)) {
                    throw new AccessDeniedException("Class Teacher can only view details of children in their own class.");
                }
            } else {
                 return ResponseEntity.notFound().build(); // Child not found anyway
            }
        }

        Optional<ChildDTO> childDTO = childService.findByUserName(userName);
        return childDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL')") // Only Principal should create new children
    public ResponseEntity<?> createChild(@RequestBody ChildDTO childDTO) {
        try {
            ChildDTO savedChild = childService.saveChild(childDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedChild); // Use 201 Created
        } catch (DataIntegrityViolationException e) {
            // Use the message from the service layer exception
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception
            // log.error("Error creating child: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during child creation.");
        }
    }

    // Child gets their own scores. Teacher/Principal can get scores for a child by username.
    @GetMapping("/scores")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('CLASS_TEACHER') or (hasRole('CHILD') and #userName == authentication.principal.username)")
    public ResponseEntity<?> getChildScores(@RequestParam String userName) {
        Optional<Child> childOpt = childService.findChildByUserName(userName);
        if (childOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Additional check: Teacher can only see scores of children in their class
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLASS_TEACHER"))) {
            String teacherUsername = getAuthenticatedUsername();
            ClassTeacher teacher = childOpt.get().getClassTeacher();
            if (teacher == null || !teacher.getUserName().equals(teacherUsername)) {
                 // Return forbidden instead of throwing exception to be caught by global handler
                 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Class Teacher can only view scores of children in their own class.");
            }
        }

        // Check if the user is a child requesting their own scores
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CHILD")) && !userName.equals(getAuthenticatedUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Child can only view their own scores.");
        }


        List<ScoreDTO> scores = scoreService.findByChildId(childOpt.get().getId());
        return ResponseEntity.ok(scores);
    }

    // Child gets their own teacher details
    @GetMapping("/classTeacher")
    @PreAuthorize("hasRole('CHILD') and #userName == authentication.principal.username")
    public ResponseEntity<?> getClassTeacherDetails(@RequestParam String userName) {
         // Double check authorization although PreAuthorize should handle it
        if (!userName.equals(getAuthenticatedUsername())) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Child can only view their own class teacher details.");
        }

        Optional<Child> childOpt = childService.findChildByUserName(userName);
        if (childOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Child child = childOpt.get();
        ClassTeacher teacher = child.getClassTeacher();
        if (teacher == null) {
            // Return a specific response indicating no teacher assigned
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Class Teacher assigned to this child.");
        }

        // Fetch DTO to avoid exposing sensitive info like password
        return classTeacherService.findById(teacher.getId())
               .map(ResponseEntity::ok)
               .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
}