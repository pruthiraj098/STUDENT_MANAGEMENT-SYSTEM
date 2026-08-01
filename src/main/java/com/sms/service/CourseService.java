package com.sms.service;

import com.sms.dto.CourseDto;
import com.sms.entity.Course;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course getCourseById(Long id);
    Course getCourseByCode(String code);
    List<Course> getCoursesByDepartment(Long departmentId);
    List<Course> getCoursesByInstructor(Long instructorId);
    Course saveCourse(CourseDto courseDto);
    Course updateCourse(Long id, CourseDto courseDto);
    void deleteCourse(Long id);
    long getTotalCoursesCount();
}
