package com.sms.controller;

import com.sms.dto.UserRegistrationDto;
import com.sms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                                       BindingResult result,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        // Username is auto-generated from email — only check email uniqueness
        if (userService.existsByEmail(userDto.getEmail())) {
            result.rejectValue("email", null, "This email address is already registered. Please login instead.");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.saveUser(userDto);
        redirectAttributes.addFlashAttribute("successMsg",
                "Account created successfully! Please login with your email and password.");
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}
