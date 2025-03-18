package com.example.schoolproject.controller;

import com.example.schoolproject.dto.CumulativeAvgDTO;
import com.example.schoolproject.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(Authentication auth, Model model) {
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        model.addAttribute("role", role);

        if ("PRINCIPAL".equals(role)) {
            List<CumulativeAvgDTO> averages = scoreService.getCumulativeAverages();
            model.addAttribute("cumulativeAverages", averages);
            return "principal-dashboard";
        } else if ("CLASS_TEACHER".equals(role)) {
            return "teacher-dashboard";
        } else if ("CHILD".equals(role)) {
            return "child-dashboard";
        }
        return "redirect:/login";
    }
}