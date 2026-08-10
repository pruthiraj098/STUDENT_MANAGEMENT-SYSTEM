package com.sms.controller;

import com.sms.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listAdminUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/create-admin")
    public String showCreateAdminForm() {
        return "admin/create-admin";
    }

    @PostMapping("/create-admin")
    public String createAdmin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              @RequestParam("confirmPassword") String confirmPassword,
                              @RequestParam(value = "username", required = false) String username,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (email == null || email.trim().isBlank()) {
            model.addAttribute("errorMsg", "Email address is required.");
            return "admin/create-admin";
        }

        if (userService.existsByEmail(email.trim())) {
            model.addAttribute("errorMsg", "An account with this email address already exists.");
            return "admin/create-admin";
        }

        if (password == null || password.trim().length() < 6) {
            model.addAttribute("errorMsg", "Password must be at least 6 characters long.");
            return "admin/create-admin";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMsg", "Passwords do not match.");
            return "admin/create-admin";
        }

        userService.createAdminUser(email.trim(), password.trim(), username);
        redirectAttributes.addFlashAttribute("successMsg", "New Administrator account created successfully!");
        return "redirect:/dashboard";
    }
}
