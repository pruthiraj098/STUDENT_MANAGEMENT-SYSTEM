package com.sms.service.impl;

import com.sms.dto.StudentDto;
import com.sms.entity.Department;
import com.sms.entity.Student;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceImplTest {

    private StudentRepository studentRepository;
    private DepartmentRepository departmentRepository;
    private StudentServiceImpl studentService;

    private final Map<Long, Student> studentsById = new HashMap<>();
    private Student lastSavedStudent;
    private Student lastDeletedStudent;
    private Page<Student> lastPageResult;
    private long count;

    @BeforeEach
    void setUp() {
        studentRepository = createStudentRepository();
        departmentRepository = createDepartmentRepository();
        studentService = new StudentServiceImpl(studentRepository, departmentRepository);
    }

    @Test
    void getStudentById_returnsStudentWhenFound() {
        Student student = new Student();
        student.setId(1L);
        studentsById.put(1L, student);

        Student result = studentService.getStudentById(1L);

        assertSame(student, result);
    }

    @Test
    void getStudentById_throwsExceptionWhenMissing() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> studentService.getStudentById(99L));

        assertTrue(exception.getMessage().contains("Student"));
        assertTrue(exception.getMessage().contains("id"));
    }

    @Test
    void saveStudent_savesStudentWhenDepartmentExists() {
        StudentDto dto = createStudentDto();
        Department department = new Department("CS", "Computer Science", "Main building", "B1");

        Student result = studentService.saveStudent(dto);

        assertNotNull(result);
        assertEquals("STU001", result.getStudentId());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getDepartment());
        assertEquals(department.getCode(), result.getDepartment().getCode());
        assertSame(result, lastSavedStudent);
    }

    @Test
    void saveStudent_throwsExceptionWhenDepartmentMissing() {
        StudentDto dto = createStudentDto();
        DepartmentRepository missingDepartmentRepository = createDepartmentRepository(Optional.empty());
        StudentServiceImpl serviceWithMissingDept = new StudentServiceImpl(studentRepository, missingDepartmentRepository);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> serviceWithMissingDept.saveStudent(dto));

        assertTrue(exception.getMessage().contains("Department"));
        assertTrue(exception.getMessage().contains("id"));
    }

    @Test
    void updateStudent_updatesExistingStudent() {
        Student existing = new Student();
        existing.setId(7L);
        existing.setStatus("INACTIVE");
        studentsById.put(7L, existing);
        StudentDto dto = createStudentDto();

        Student result = studentService.updateStudent(7L, dto);

        assertSame(existing, result);
        assertEquals("STU001", result.getStudentId());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getDepartment());
        assertSame(result, lastSavedStudent);
    }

    @Test
    void deleteStudent_deletesStudentWhenFound() {
        Student student = new Student();
        student.setId(8L);
        studentsById.put(8L, student);

        studentService.deleteStudent(8L);

        assertSame(student, lastDeletedStudent);
    }

    @Test
    void getSystemAverageGpa_returnsRoundedAverageForPositiveGrades() {
        Student student = new Student();
        student.setEnrollments(List.of());
        studentsById.put(1L, student);

        double result = studentService.getSystemAverageGPA();

        assertEquals(0.0, result);
    }

    @Test
    void getStudentsPaginatedAndFiltered_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = new PageImpl<>(List.of(new Student()));
        lastPageResult = page;

        Page<Student> result = studentService.getStudentsPaginatedAndFiltered("alice", 2L, "ACTIVE", pageable);

        assertSame(page, result);
    }

    private StudentRepository createStudentRepository() {
        return createStudentRepository(Optional.empty(), null);
    }

    private StudentRepository createStudentRepository(Optional<Student> studentToReturn) {
        return createStudentRepository(studentToReturn, null);
    }

    private StudentRepository createStudentRepository(Optional<Student> studentToReturn, Page<Student> page) {
        return (StudentRepository) Proxy.newProxyInstance(
                StudentRepository.class.getClassLoader(),
                new Class[]{StudentRepository.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        switch (method.getName()) {
                            case "findById":
                                Long id = (Long) args[0];
                                return Optional.ofNullable(studentsById.get(id));
                            case "findByStudentId":
                                return studentToReturn;
                            case "findByUserId":
                                return Optional.empty();
                            case "findAll":
                                return List.copyOf(studentsById.values());
                            case "save":
                                Student student = (Student) args[0];
                                if (student.getId() == null) {
                                    student.setId((long) (studentsById.size() + 1));
                                }
                                studentsById.put(student.getId(), student);
                                lastSavedStudent = student;
                                return student;
                            case "delete":
                                Student deleted = (Student) args[0];
                                lastDeletedStudent = deleted;
                                studentsById.remove(deleted.getId());
                                return null;
                            case "count":
                                return count;
                            case "countByDepartmentId":
                                return 1L;
                            case "searchAndFilterStudents":
                                return page != null ? page : lastPageResult;
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    }
                });
    }

    private DepartmentRepository createDepartmentRepository() {
        return createDepartmentRepository(Optional.of(new Department("CS", "Computer Science", "Main building", "B1")));
    }

    private DepartmentRepository createDepartmentRepository(Optional<Department> departmentToReturn) {
        return (DepartmentRepository) Proxy.newProxyInstance(
                DepartmentRepository.class.getClassLoader(),
                new Class[]{DepartmentRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        Long id = (Long) args[0];
                        return id == 5L ? departmentToReturn : Optional.empty();
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private Object defaultValue(Class<?> returnType) {
        if (returnType == boolean.class) return false;
        if (returnType == int.class) return 0;
        if (returnType == long.class) return 0L;
        if (returnType == double.class) return 0.0;
        if (returnType == float.class) return 0.0f;
        if (returnType == short.class) return (short) 0;
        if (returnType == byte.class) return (byte) 0;
        if (returnType == char.class) return '\0';
        if (returnType == void.class) return null;
        if (returnType == Optional.class) return Optional.empty();
        return null;
    }

    private StudentDto createStudentDto() {
        StudentDto dto = new StudentDto();
        dto.setStudentId("STU001");
        dto.setFirstName("Alice");
        dto.setLastName("Johnson");
        dto.setEmail("alice@example.com");
        dto.setPhone("123456789");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setGender("F");
        dto.setAddress("Main Street");
        dto.setAdmissionDate(LocalDate.of(2024, 1, 1));
        dto.setStatus("ACTIVE");
        dto.setDepartmentId(5L);
        return dto;
    }
}
