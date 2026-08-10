package com.sms.controller;

import com.sms.dto.StudentActivityDto;
import com.sms.dto.StudentDto;
import com.sms.entity.Department;
import com.sms.entity.Enrollment;
import com.sms.entity.Student;
import com.sms.entity.StudentActivity;
import com.sms.service.DepartmentService;
import com.sms.service.EnrollmentService;
import com.sms.service.StudentActivityService;
import com.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;
    private final EnrollmentService enrollmentService;
    private final StudentActivityService activityService;

    public StudentController(StudentService studentService,
                             DepartmentService departmentService,
                             EnrollmentService enrollmentService,
                             StudentActivityService activityService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
        this.enrollmentService = enrollmentService;
        this.activityService = activityService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public String listStudents(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "deptId", required = false) Long deptId,
                               @RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "8") int size,
                               @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                               @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                               Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Student> studentPage = studentService.getStudentsPaginatedAndFiltered(keyword, deptId, status, pageable);

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        model.addAttribute("keyword", keyword);
        model.addAttribute("deptId", deptId);
        model.addAttribute("status", status);
        model.addAttribute("departments", departmentService.getAllDepartments());

        return "students/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("studentDto", new StudentDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isEdit", false);
        return "students/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveStudent(@Valid @ModelAttribute("studentDto") StudentDto studentDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (studentDto.getId() == null && studentService.getAllStudents().stream().anyMatch(s -> s.getStudentId().equalsIgnoreCase(studentDto.getStudentId()))) {
            result.rejectValue("studentId", null, "Student ID already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isEdit", studentDto.getId() != null);
            return "students/form";
        }

        if (studentDto.getId() == null) {
            studentService.saveStudent(studentDto);
            redirectAttributes.addFlashAttribute("successMsg", "Student registered successfully!");
        } else {
            studentService.updateStudent(studentDto.getId(), studentDto);
            redirectAttributes.addFlashAttribute("successMsg", "Student details updated successfully!");
        }

        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudentById(id);
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setStudentId(student.getStudentId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setGender(student.getGender());
        dto.setAddress(student.getAddress());
        dto.setAdmissionDate(student.getAdmissionDate());
        dto.setStatus(student.getStatus());
        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
        }

        model.addAttribute("studentDto", dto);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isEdit", true);
        return "students/form";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteStudent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMsg", "Student record deleted successfully!");
        return "redirect:/students";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public String viewStudent(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudentById(id);
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(id);
        List<StudentActivity> activities = activityService.getActivitiesByStudentId(id);

        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("activities", activities);
        model.addAttribute("activityDto", new StudentActivityDto());
        model.addAttribute("gpa", student.calculateGPA());
        return "students/view";
    }

    @PostMapping("/{studentId}/activities/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addActivity(@PathVariable("studentId") Long studentId,
                              @Valid @ModelAttribute("activityDto") StudentActivityDto activityDto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Please fill in valid activity details!");
            return "redirect:/students/view/" + studentId;
        }

        activityService.addActivity(studentId, activityDto);
        redirectAttributes.addFlashAttribute("successMsg", "Student activity added successfully!");
        return "redirect:/students/view/" + studentId;
    }

    @GetMapping("/activities/delete/{activityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteActivity(@PathVariable("activityId") Long activityId,
                                 @RequestParam("studentId") Long studentId,
                                 RedirectAttributes redirectAttributes) {
        activityService.deleteActivity(activityId);
        redirectAttributes.addFlashAttribute("successMsg", "Student activity deleted successfully!");
        return "redirect:/students/view/" + studentId;
    }

    @GetMapping("/transcript/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public String viewTranscript(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudentById(id);
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(id);

        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("gpa", student.calculateGPA());
        return "students/transcript";
    }

    @PostMapping("/{id}/update-attendance-lab")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public String updateStudentAttendanceAndLab(@PathVariable("id") Long studentId,
                                                @RequestParam(value = "enrollmentId", required = false) Long enrollmentId,
                                                @RequestParam(value = "attendancePercentage", required = false) Double attendancePercentage,
                                                @RequestParam(value = "labStatus", required = false) String labStatus,
                                                @RequestParam(value = "labSubmissionDetails", required = false) String labSubmissionDetails,
                                                @RequestParam(value = "practicalMarks", required = false) Double practicalMarks,
                                                @RequestParam(value = "numericGrade", required = false) Double numericGrade,
                                                @RequestParam(value = "redirectSource", required = false) String redirectSource,
                                                RedirectAttributes redirectAttributes) {

        String targetRedirect = "list".equalsIgnoreCase(redirectSource) ? "redirect:/students" : "redirect:/students/view/" + studentId;

        if (enrollmentId == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Please select a valid course enrollment to modify attendance or lab records.");
            return targetRedirect;
        }

        try {
            enrollmentService.updateAssessment(enrollmentId, numericGrade, attendancePercentage, labStatus, labSubmissionDetails, practicalMarks);
            redirectAttributes.addFlashAttribute("successMsg", "Student attendance, lab records, and practical marks updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "Could not update assessment: " + ex.getMessage());
        }

        return targetRedirect;
    }
}
