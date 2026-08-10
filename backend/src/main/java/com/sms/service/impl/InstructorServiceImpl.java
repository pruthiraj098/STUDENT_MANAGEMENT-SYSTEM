package com.sms.service.impl;

import com.sms.dto.InstructorDto;
import com.sms.dto.InstructorRequestDto;
import com.sms.entity.Department;
import com.sms.entity.Instructor;
import com.sms.entity.InstructorRequest;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.InstructorRepository;
import com.sms.repository.InstructorRequestRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.UserRepository;
import com.sms.service.InstructorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final InstructorRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public InstructorServiceImpl(InstructorRepository instructorRepository,
                                 InstructorRequestRepository requestRepository,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 DepartmentRepository departmentRepository,
                                 PasswordEncoder passwordEncoder) {
        this.instructorRepository = instructorRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    @Override
    public Instructor getInstructorById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + id));
    }

    @Override
    @Transactional
    public Instructor saveInstructor(InstructorDto dto) {
        String cleanEmail = dto.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(cleanEmail) || instructorRepository.findByEmail(cleanEmail).isPresent()) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        String baseUsername = cleanEmail.split("@")[0];
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix++;
        }

        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isBlank()) ? dto.getPassword() : "instructor123";

        User user = new User();
        user.setUsername(username);
        user.setEmail(cleanEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled("ACTIVE".equalsIgnoreCase(dto.getStatus()));

        Role instructorRole = roleRepository.findByName("ROLE_INSTRUCTOR")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_INSTRUCTOR")));
        user.setRoles(Collections.singleton(instructorRole));

        User savedUser = userRepository.save(user);

        Instructor instructor = new Instructor();
        instructor.setStaffId("INS" + (100 + instructorRepository.count() + 1));
        instructor.setFirstName(dto.getFirstName().trim());
        instructor.setLastName(dto.getLastName().trim());
        instructor.setEmail(cleanEmail);
        instructor.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        instructor.setDesignation(dto.getDesignation() != null ? dto.getDesignation().trim() : "Lecturer");
        instructor.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
            instructor.setDepartment(dept);
        }

        instructor.setUser(savedUser);
        return instructorRepository.save(instructor);
    }

    @Override
    @Transactional
    public Instructor updateInstructor(Long id, InstructorDto dto) {
        Instructor instructor = getInstructorById(id);
        String cleanEmail = dto.getEmail().trim().toLowerCase();

        if (!instructor.getEmail().equalsIgnoreCase(cleanEmail) &&
            (userRepository.existsByEmail(cleanEmail) || instructorRepository.findByEmail(cleanEmail).isPresent())) {
            throw new IllegalArgumentException("Email address is already in use by another account.");
        }

        instructor.setFirstName(dto.getFirstName().trim());
        instructor.setLastName(dto.getLastName().trim());
        instructor.setEmail(cleanEmail);
        instructor.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        instructor.setDesignation(dto.getDesignation() != null ? dto.getDesignation().trim() : instructor.getDesignation());
        if (dto.getStatus() != null) {
            instructor.setStatus(dto.getStatus());
        }

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
            instructor.setDepartment(dept);
        } else {
            instructor.setDepartment(null);
        }

        if (instructor.getUser() != null) {
            User user = instructor.getUser();
            user.setEmail(cleanEmail);
            user.setEnabled("ACTIVE".equalsIgnoreCase(instructor.getStatus()));
            userRepository.save(user);
        }

        return instructorRepository.save(instructor);
    }

    @Override
    @Transactional
    public void deleteInstructor(Long id) {
        Instructor instructor = getInstructorById(id);
        User user = instructor.getUser();
        instructorRepository.delete(instructor);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Override
    @Transactional
    public void toggleInstructorStatus(Long id) {
        Instructor instructor = getInstructorById(id);
        boolean currentActive = "ACTIVE".equalsIgnoreCase(instructor.getStatus());
        String newStatus = currentActive ? "INACTIVE" : "ACTIVE";
        instructor.setStatus(newStatus);

        if (instructor.getUser() != null) {
            instructor.getUser().setEnabled(!currentActive);
            userRepository.save(instructor.getUser());
        }
        instructorRepository.save(instructor);
    }

    @Override
    @Transactional
    public void resetInstructorPassword(Long id, String newPassword) {
        Instructor instructor = getInstructorById(id);
        if (instructor.getUser() == null) {
            throw new IllegalStateException("Instructor does not have an associated login user account.");
        }
        User user = instructor.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public long getTotalInstructorsCount() {
        return instructorRepository.count();
    }

    @Override
    @Transactional
    public InstructorRequest submitInstructorRequest(InstructorRequestDto requestDto) {
        String cleanEmail = requestDto.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(cleanEmail) || instructorRepository.findByEmail(cleanEmail).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (requestRepository.existsByEmailAndStatus(cleanEmail, "PENDING")) {
            throw new IllegalArgumentException("An application with this email address is already pending review.");
        }

        Department dept = null;
        if (requestDto.getDepartmentId() != null) {
            dept = departmentRepository.findById(requestDto.getDepartmentId()).orElse(null);
        }

        InstructorRequest request = new InstructorRequest(
                requestDto.getFirstName().trim(),
                requestDto.getLastName().trim(),
                cleanEmail,
                requestDto.getPhone() != null ? requestDto.getPhone().trim() : null,
                requestDto.getDesignation() != null ? requestDto.getDesignation().trim() : "Lecturer",
                requestDto.getQualification() != null ? requestDto.getQualification().trim() : null,
                dept
        );

        return requestRepository.save(request);
    }

    @Override
    public List<InstructorRequest> getPendingRequests() {
        return requestRepository.findByStatusOrderByAppliedAtDesc("PENDING");
    }

    @Override
    public List<InstructorRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    @Transactional
    public Instructor approveInstructorRequest(Long requestId) {
        InstructorRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor request not found with ID: " + requestId));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException("Only pending requests can be approved.");
        }

        request.setStatus("APPROVED");
        request.setProcessedAt(LocalDateTime.now());
        requestRepository.save(request);

        // Convert request into Instructor account
        InstructorDto dto = new InstructorDto();
        dto.setFirstName(request.getFirstName());
        dto.setLastName(request.getLastName());
        dto.setEmail(request.getEmail());
        dto.setPhone(request.getPhone());
        dto.setDesignation(request.getDesignation());
        if (request.getDepartment() != null) {
            dto.setDepartmentId(request.getDepartment().getId());
        }
        dto.setStatus("ACTIVE");
        dto.setPassword("instructor123"); // Default temporary password

        return saveInstructor(dto);
    }

    @Override
    @Transactional
    public void rejectInstructorRequest(Long requestId, String reason) {
        InstructorRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor request not found with ID: " + requestId));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException("Only pending requests can be rejected.");
        }

        request.setStatus("REJECTED");
        request.setProcessedAt(LocalDateTime.now());
        request.setRejectionReason(reason != null ? reason.trim() : "Application rejected by Administrator");
        requestRepository.save(request);
    }
}
