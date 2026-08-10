package com.sms.repository;

import com.sms.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByUserId(Long userId);
    Boolean existsByStudentId(String studentId);
    Boolean existsByEmail(String email);
    
    List<Student> findByDepartmentId(Long departmentId);
    List<Student> findByStatus(String status);

    @Query("SELECT s FROM Student s WHERE " +
           "(:keyword IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.studentId) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:deptId IS NULL OR s.department.id = :deptId) " +
           "AND (:status IS NULL OR :status = '' OR s.status = :status)")
    Page<Student> searchAndFilterStudents(
        @Param("keyword") String keyword,
        @Param("deptId") Long deptId,
        @Param("status") String status,
        Pageable pageable
    );

    @Query("SELECT COUNT(s) FROM Student s WHERE s.department.id = :deptId")
    long countByDepartmentId(@Param("deptId") Long deptId);
}
