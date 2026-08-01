package com.sms.repository;

import com.sms.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByStaffId(String staffId);
    Optional<Instructor> findByEmail(String email);
    Optional<Instructor> findByUserId(Long userId);
    List<Instructor> findByDepartmentId(Long departmentId);
}
