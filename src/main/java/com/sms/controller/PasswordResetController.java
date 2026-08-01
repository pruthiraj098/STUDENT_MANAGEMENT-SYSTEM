package com.sms.controller;

import com.sms.entity.PasswordResetToken;
import com.sms.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if ("invalid".equals(error)) {
            model.addAttribute("errorMsg", "The password reset link is invalid or has expired. Please request a new link.");
        }
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        HttpServletRequest request,
                                        Model model) {
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("errorMsg", "Please enter a valid email address.");
            return "forgot-password";
        }

        PasswordResetToken token = passwordResetService.createPasswordResetToken(email);
        if (token == null) {
            model.addAttribute("errorMsg", "No registered account found for email or username: " + email);
            return "forgot-password";
        }

        // Construct reset link URL
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String resetUrl = baseUrl + "/reset-password?token=" + token.getToken();

        model.addAttribute("successMsg", "Password reset link generated successfully!");
        model.addAttribute("resetUrl", resetUrl);
        model.addAttribute("email", token.getUser().getEmail());
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!passwordResetService.validateToken(token)) {
            return "redirect:/forgot-password?error=invalid";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {

        if (!passwordResetService.validateToken(token)) {
            return "redirect:/forgot-password?error=invalid";
        }

        if (password == null || password.length() < 6) {
            model.addAttribute("errorMsg", "Password must be at least 6 characters long.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMsg", "Passwords do not match. Please re-enter.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        boolean success = passwordResetService.resetPassword(token, password);
        if (success) {
            redirectAttributes.addFlashAttribute("resetSuccessMsg", "Your password has been reset successfully! Please sign in with your new password.");
            return "redirect:/login";
        } else {
            model.addAttribute("errorMsg", "Failed to reset password. Token may have expired.");
            model.addAttribute("token", token);
            return "reset-password";
        }
    }
}
