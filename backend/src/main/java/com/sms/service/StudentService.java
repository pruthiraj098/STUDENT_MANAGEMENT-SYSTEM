package com.sms.service;

import com.sms.dto.StudentDto;
import com.sms.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student getStudentById(Long id);
    Student getStudentByStudentId(String studentId);
    Student getStudentByUserId(Long userId);
    Student saveStudent(StudentDto studentDto);
    Student updateStudent(Long id, StudentDto studentDto);
    void deleteStudent(Long id);
    
    Page<Student> getStudentsPaginatedAndFiltered(String keyword, Long deptId, String status, Pageable pageable);
    
    long getTotalStudentsCount();
    long getStudentsCountByDepartment(Long deptId);
    double getSystemAverageGPA();
}
