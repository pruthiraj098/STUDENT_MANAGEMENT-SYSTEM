package com.sms.repository;

import com.sms.entity.InstructorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRequestRepository extends JpaRepository<InstructorRequest, Long> {
    List<InstructorRequest> findByStatusOrderByAppliedAtDesc(String status);
    Optional<InstructorRequest> findByEmail(String email);
    boolean existsByEmailAndStatus(String email, String status);
}
