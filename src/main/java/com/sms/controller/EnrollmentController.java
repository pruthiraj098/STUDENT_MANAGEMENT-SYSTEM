package com.sms.controller;

import com.sms.dto.EnrollmentDto;
import com.sms.entity.Enrollment;
import com.sms.service.CourseService;
import com.sms.service.EnrollmentService;
import com.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentController(EnrollmentService enrollmentService,
                                StudentService studentService,
                                CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping
    public String listEnrollments(Model model) {
        model.addAttribute("enrollments", enrollmentService.getAllEnrollments());
        return "enrollments/list";
    }

    @GetMapping("/assign")
    public String showEnrollmentForm(Model model) {
        model.addAttribute("enrollmentDto", new EnrollmentDto());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        return "enrollments/form";
    }

    @PostMapping("/save")
    public String saveEnrollment(@Valid @ModelAttribute("enrollmentDto") EnrollmentDto dto,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("courses", courseService.getAllCourses());
            return "enrollments/form";
        }

        try {
            enrollmentService.enrollStudent(dto);
            redirectAttributes.addFlashAttribute("successMsg", "Student enrolled in course successfully!");
        } catch (IllegalArgumentException ex) {
            result.rejectValue("courseId", null, ex.getMessage());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("courses", courseService.getAllCourses());
            return "enrollments/form";
        }

        return "redirect:/enrollments";
    }

    @PostMapping("/grade")
    public String recordGrade(@RequestParam("enrollmentId") Long enrollmentId,
                              @RequestParam("numericGrade") Double numericGrade,
                              RedirectAttributes redirectAttributes) {
        enrollmentService.recordGrade(enrollmentId, numericGrade);
        redirectAttributes.addFlashAttribute("successMsg", "Grade recorded and GPA updated successfully!");
        return "redirect:/enrollments";
    }

    @GetMapping("/delete/{id}")
    public String deleteEnrollment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        enrollmentService.deleteEnrollment(id);
        redirectAttributes.addFlashAttribute("successMsg", "Enrollment record removed!");
        return "redirect:/enrollments";
    }
}
