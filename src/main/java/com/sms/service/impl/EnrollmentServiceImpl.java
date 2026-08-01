package com.sms.service.impl;

import com.sms.dto.EnrollmentDto;
import com.sms.entity.Course;
import com.sms.entity.Enrollment;
import com.sms.entity.Student;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.CourseRepository;
import com.sms.repository.EnrollmentRepository;
import com.sms.repository.StudentRepository;
import com.sms.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Override
    @Transactional
    public Enrollment enrollStudent(EnrollmentDto dto) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", dto.getStudentId()));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", dto.getCourseId()));

        Enrollment enrollment = new Enrollment(student, course, LocalDate.now());
        if (dto.getNumericGrade() != null) {
            enrollment.updateGrade(dto.getNumericGrade());
        }
        if (dto.getStatus() != null) {
            enrollment.setStatus(dto.getStatus());
        }
        if (dto.getSemester() != null) {
            enrollment.setSemester(dto.getSemester());
        }
        if (dto.getAttendancePercentage() != null) {
            enrollment.setAttendancePercentage(dto.getAttendancePercentage());
        }
        if (dto.getLabStatus() != null) {
            enrollment.setLabStatus(dto.getLabStatus());
        }
        if (dto.getLabSubmissionDetails() != null) {
            enrollment.setLabSubmissionDetails(dto.getLabSubmissionDetails());
        }

        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public Enrollment recordGrade(Long enrollmentId, Double numericGrade) {
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        enrollment.updateGrade(numericGrade);
        if (numericGrade != null) {
            enrollment.setStatus("COMPLETED");
        }
        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public long getTotalEnrollmentsCount() {
        return enrollmentRepository.count();
    }
}
