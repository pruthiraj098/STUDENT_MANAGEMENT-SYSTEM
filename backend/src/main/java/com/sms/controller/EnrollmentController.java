package com.sms.controller;

import com.sms.dto.EnrollmentDto;
import com.sms.entity.Enrollment;
import com.sms.service.CourseService;
import com.sms.service.EnrollmentService;
import com.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public String listEnrollments(Model model) {
        model.addAttribute("enrollments", enrollmentService.getAllEnrollments());
        return "enrollments/list";
    }

    @GetMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEnrollmentForm(Model model) {
        model.addAttribute("enrollmentDto", new EnrollmentDto());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        return "enrollments/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public String recordGrade(@RequestParam(value = "enrollmentId", required = false) Long enrollmentId,
                              @RequestParam(value = "numericGrade", required = false) Double numericGrade,
                              RedirectAttributes redirectAttributes) {
        if (enrollmentId == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Enrollment record is required.");
            return "redirect:/enrollments";
        }
        try {
            enrollmentService.recordGrade(enrollmentId, numericGrade);
            redirectAttributes.addFlashAttribute("successMsg", "Grade recorded and GPA updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "Could not record grade: " + ex.getMessage());
        }
        return "redirect:/enrollments";
    }

    @PostMapping("/update-assessment")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public String updateAssessment(@RequestParam(value = "enrollmentId", required = false) Long enrollmentId,
                                   @RequestParam(value = "numericGrade", required = false) Double numericGrade,
                                   @RequestParam(value = "attendancePercentage", required = false) Double attendancePercentage,
                                   @RequestParam(value = "labStatus", required = false) String labStatus,
                                   @RequestParam(value = "labSubmissionDetails", required = false) String labSubmissionDetails,
                                   @RequestParam(value = "practicalMarks", required = false) Double practicalMarks,
                                   RedirectAttributes redirectAttributes) {
        if (enrollmentId == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Enrollment record is required.");
            return "redirect:/enrollments";
        }
        try {
            enrollmentService.updateAssessment(enrollmentId, numericGrade, attendancePercentage, labStatus, labSubmissionDetails, practicalMarks);
            redirectAttributes.addFlashAttribute("successMsg", "Attendance, lab records, and practical marks updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "Could not update assessment: " + ex.getMessage());
        }
        return "redirect:/enrollments";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEnrollment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        enrollmentService.deleteEnrollment(id);
        redirectAttributes.addFlashAttribute("successMsg", "Enrollment record removed!");
        return "redirect:/enrollments";
    }
}
