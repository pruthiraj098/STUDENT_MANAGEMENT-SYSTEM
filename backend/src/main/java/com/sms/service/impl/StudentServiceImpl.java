package com.sms.service.impl;

import com.sms.dto.StudentDto;
import com.sms.entity.Department;
import com.sms.entity.Student;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.StudentRepository;
import com.sms.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;

    public StudentServiceImpl(StudentRepository studentRepository, DepartmentRepository departmentRepository) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    @Override
    public Student getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentId", studentId));
    }

    @Override
    public Student getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId).orElse(null);
    }

    @Override
    @Transactional
    public Student saveStudent(StudentDto dto) {
        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", dto.getDepartmentId()));

        Student student = new Student();
        mapDtoToStudent(dto, student, dept);
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student updateStudent(Long id, StudentDto dto) {
        Student student = getStudentById(id);
        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", dto.getDepartmentId()));

        mapDtoToStudent(dto, student, dept);
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    @Override
    public Page<Student> getStudentsPaginatedAndFiltered(String keyword, Long deptId, String status, Pageable pageable) {
        return studentRepository.searchAndFilterStudents(keyword, deptId, status, pageable);
    }

    @Override
    public long getTotalStudentsCount() {
        return studentRepository.count();
    }

    @Override
    public long getStudentsCountByDepartment(Long deptId) {
        return studentRepository.countByDepartmentId(deptId);
    }

    @Override
    public double getSystemAverageGPA() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) return 0.0;
        double sumGpa = 0.0;
        int count = 0;
        for (Student s : students) {
            double gpa = s.calculateGPA();
            if (gpa > 0) {
                sumGpa += gpa;
                count++;
            }
        }
        return count == 0 ? 0.0 : Math.round((sumGpa / count) * 100.0) / 100.0;
    }

    private void mapDtoToStudent(StudentDto dto, Student student, Department dept) {
        student.setStudentId(dto.getStudentId());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setAddress(dto.getAddress());
        student.setAdmissionDate(dto.getAdmissionDate());
        student.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        student.setDepartment(dept);
    }
}
