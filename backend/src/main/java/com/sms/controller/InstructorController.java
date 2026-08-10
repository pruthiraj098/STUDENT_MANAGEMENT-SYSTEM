package com.sms.controller;

import com.sms.dto.InstructorDto;
import com.sms.entity.Instructor;
import com.sms.service.DepartmentService;
import com.sms.service.InstructorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/instructors")
@PreAuthorize("hasRole('ADMIN')")
public class InstructorController {

    private final InstructorService instructorService;
    private final DepartmentService departmentService;

    public InstructorController(InstructorService instructorService, DepartmentService departmentService) {
        this.instructorService = instructorService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String listInstructors(Model model) {
        model.addAttribute("instructors", instructorService.getAllInstructors());
        model.addAttribute("pendingRequests", instructorService.getPendingRequests());
        return "instructors/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("instructorDto", new InstructorDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isEdit", false);
        return "instructors/form";
    }

    @PostMapping("/save")
    public String saveInstructor(@Valid @ModelAttribute("instructorDto") InstructorDto dto,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isEdit", dto.getId() != null);
            return "instructors/form";
        }

        try {
            if (dto.getId() == null) {
                instructorService.saveInstructor(dto);
                redirectAttributes.addFlashAttribute("successMsg", "Instructor account created successfully!");
            } else {
                instructorService.updateInstructor(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("successMsg", "Instructor details updated successfully!");
            }
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", null, ex.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isEdit", dto.getId() != null);
            return "instructors/form";
        }

        return "redirect:/instructors";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Instructor instructor = instructorService.getInstructorById(id);
        InstructorDto dto = new InstructorDto();
        dto.setId(instructor.getId());
        dto.setStaffId(instructor.getStaffId());
        dto.setFirstName(instructor.getFirstName());
        dto.setLastName(instructor.getLastName());
        dto.setEmail(instructor.getEmail());
        dto.setPhone(instructor.getPhone());
        dto.setDesignation(instructor.getDesignation());
        dto.setStatus(instructor.getStatus());
        if (instructor.getDepartment() != null) {
            dto.setDepartmentId(instructor.getDepartment().getId());
        }

        model.addAttribute("instructorDto", dto);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isEdit", true);
        return "instructors/form";
    }

    @GetMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        instructorService.toggleInstructorStatus(id);
        redirectAttributes.addFlashAttribute("successMsg", "Instructor account status updated!");
        return "redirect:/instructors";
    }

    @GetMapping("/delete/{id}")
    public String deleteInstructor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        instructorService.deleteInstructor(id);
        redirectAttributes.addFlashAttribute("successMsg", "Instructor record removed successfully!");
        return "redirect:/instructors";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable("id") Long id,
                                @RequestParam("newPassword") String newPassword,
                                RedirectAttributes redirectAttributes) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            redirectAttributes.addFlashAttribute("errorMsg", "Password must be at least 6 characters long.");
            return "redirect:/instructors";
        }

        try {
            instructorService.resetInstructorPassword(id, newPassword.trim());
            redirectAttributes.addFlashAttribute("successMsg", "Instructor password updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }

        return "redirect:/instructors";
    }

    @PostMapping("/requests/approve/{requestId}")
    public String approveRequest(@PathVariable("requestId") Long requestId, RedirectAttributes redirectAttributes) {
        try {
            instructorService.approveInstructorRequest(requestId);
            redirectAttributes.addFlashAttribute("successMsg", "Instructor request approved and account created successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:/instructors";
    }

    @PostMapping("/requests/reject/{requestId}")
    public String rejectRequest(@PathVariable("requestId") Long requestId,
                                @RequestParam(value = "reason", required = false) String reason,
                                RedirectAttributes redirectAttributes) {
        try {
            instructorService.rejectInstructorRequest(requestId, reason);
            redirectAttributes.addFlashAttribute("successMsg", "Instructor request rejected.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:/instructors";
    }
}
