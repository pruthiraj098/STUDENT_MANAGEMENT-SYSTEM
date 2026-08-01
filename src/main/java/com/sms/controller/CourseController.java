package com.sms.controller;

import com.sms.dto.CourseDto;
import com.sms.entity.Course;
import com.sms.service.CourseService;
import com.sms.service.DepartmentService;
import com.sms.repository.InstructorRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final DepartmentService departmentService;
    private final InstructorRepository instructorRepository;

    public CourseController(CourseService courseService,
                            DepartmentService departmentService,
                            InstructorRepository instructorRepository) {
        this.courseService = courseService;
        this.departmentService = departmentService;
        this.instructorRepository = instructorRepository;
    }

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("courseDto", new CourseDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("instructors", instructorRepository.findAll());
        model.addAttribute("isEdit", false);
        return "courses/form";
    }

    @PostMapping("/save")
    public String saveCourse(@Valid @ModelAttribute("courseDto") CourseDto courseDto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("instructors", instructorRepository.findAll());
            model.addAttribute("isEdit", courseDto.getId() != null);
            return "courses/form";
        }

        if (courseDto.getId() == null) {
            courseService.saveCourse(courseDto);
            redirectAttributes.addFlashAttribute("successMsg", "Course added successfully!");
        } else {
            courseService.updateCourse(courseDto.getId(), courseDto);
            redirectAttributes.addFlashAttribute("successMsg", "Course updated successfully!");
        }

        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Course course = courseService.getCourseById(id);
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setCode(course.getCode());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCredits(course.getCredits());
        if (course.getDepartment() != null) dto.setDepartmentId(course.getDepartment().getId());
        if (course.getInstructor() != null) dto.setInstructorId(course.getInstructor().getId());

        model.addAttribute("courseDto", dto);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("instructors", instructorRepository.findAll());
        model.addAttribute("isEdit", true);
        return "courses/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        courseService.deleteCourse(id);
        redirectAttributes.addFlashAttribute("successMsg", "Course deleted successfully!");
        return "redirect:/courses";
    }
}
