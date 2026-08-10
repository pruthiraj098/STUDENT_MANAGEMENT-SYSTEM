package com.sms.controller;

import com.sms.dto.InstructorRequestDto;
import com.sms.dto.UserRegistrationDto;
import com.sms.service.DepartmentService;
import com.sms.service.InstructorService;
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
    private final InstructorService instructorService;
    private final DepartmentService departmentService;

    public AuthController(UserService userService,
                          InstructorService instructorService,
                          DepartmentService departmentService) {
        this.userService = userService;
        this.instructorService = instructorService;
        this.departmentService = departmentService;
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
        if (userService.existsByEmail(userDto.getEmail())) {
            result.rejectValue("email", null, "This email address is already registered. Please login instead.");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.saveUser(userDto);
        redirectAttributes.addFlashAttribute("successMsg",
                "Student account registered successfully! Please sign in with your email and password.");
        return "redirect:/login";
    }

    @GetMapping("/register/instructor")
    public String showInstructorRequestForm(Model model) {
        model.addAttribute("requestDto", new InstructorRequestDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "register-instructor";
    }

    @PostMapping("/register/instructor")
    public String submitInstructorRequest(@Valid @ModelAttribute("requestDto") InstructorRequestDto requestDto,
                                          BindingResult result,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        if (userService.existsByEmail(requestDto.getEmail())) {
            result.rejectValue("email", null, "An account with this email address already exists.");
        }

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register-instructor";
        }

        try {
            instructorService.submitInstructorRequest(requestDto);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Application submitted successfully! An administrator will review your request shortly.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", null, ex.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register-instructor";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}
