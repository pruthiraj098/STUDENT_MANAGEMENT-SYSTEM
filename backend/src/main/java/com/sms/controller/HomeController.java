package com.sms.controller;

import com.sms.entity.Department;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.service.CourseService;
import com.sms.service.DepartmentService;
import com.sms.service.EnrollmentService;
import com.sms.service.InstructorService;
import com.sms.service.StudentService;
import com.sms.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final DepartmentService departmentService;
    private final EnrollmentService enrollmentService;
    private final InstructorService instructorService;
    private final UserService userService;

    public HomeController(StudentService studentService,
                          CourseService courseService,
                          DepartmentService departmentService,
                          EnrollmentService enrollmentService,
                          InstructorService instructorService,
                          UserService userService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.departmentService = departmentService;
        this.enrollmentService = enrollmentService;
        this.instructorService = instructorService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalStudents", studentService.getTotalStudentsCount());
        model.addAttribute("totalInstructors", instructorService.getTotalInstructorsCount());
        model.addAttribute("totalCourses", courseService.getTotalCoursesCount());
        model.addAttribute("totalDepartments", departmentService.getTotalDepartmentsCount());
        model.addAttribute("avgGpa", studentService.getSystemAverageGPA());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalStudents", studentService.getTotalStudentsCount());
        model.addAttribute("totalInstructors", instructorService.getTotalInstructorsCount());
        model.addAttribute("totalCourses", courseService.getTotalCoursesCount());
        model.addAttribute("totalDepartments", departmentService.getTotalDepartmentsCount());
        model.addAttribute("totalEnrollments", enrollmentService.getTotalEnrollmentsCount());
        model.addAttribute("avgGpa", studentService.getSystemAverageGPA());
        model.addAttribute("totalParents", Math.max(15000L, studentService.getTotalStudentsCount() * 2));
        model.addAttribute("totalEarnings", "$30,000");

        // Admin-specific counts & pending application requests
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("pendingRequests", instructorService.getPendingRequests());
            model.addAttribute("pendingRequestsCount", instructorService.getPendingRequests().size());
        }

        // Student-specific profile & data
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            Student student = studentService.getStudentByUserId(currentUser != null ? currentUser.getId() : null);
            if (student != null) {
                model.addAttribute("studentProfile", student);
                model.addAttribute("myEnrollments", enrollmentService.getEnrollmentsByStudent(student.getId()));
                model.addAttribute("myGpa", student.calculateGPA());
            }
        }

        // Recent students list
        List<Student> recentStudents = studentService.getAllStudents();
        if (recentStudents.size() > 5) {
            recentStudents = recentStudents.subList(recentStudents.size() - 5, recentStudents.size());
        }
        model.addAttribute("recentStudents", recentStudents);

        return "dashboard";
    }

    @GetMapping("/api/analytics/departments")
    @ResponseBody
    public Map<String, Object> getDepartmentAnalytics() {
        List<Department> departments = departmentService.getAllDepartments();
        List<String> labels = departments.stream().map(Department::getCode).collect(Collectors.toList());
        List<Long> counts = departments.stream()
                .map(d -> studentService.getStudentsCountByDepartment(d.getId()))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", counts);
        return response;
    }
}
