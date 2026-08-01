package com.sms.service;

import com.sms.dto.EnrollmentDto;
import com.sms.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {
    List<Enrollment> getAllEnrollments();
    Enrollment getEnrollmentById(Long id);
    List<Enrollment> getEnrollmentsByStudent(Long studentId);
    List<Enrollment> getEnrollmentsByCourse(Long courseId);
    Enrollment enrollStudent(EnrollmentDto enrollmentDto);
    Enrollment recordGrade(Long enrollmentId, Double numericGrade);
    void deleteEnrollment(Long id);
    long getTotalEnrollmentsCount();
}
