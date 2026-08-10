package com.sms.service.impl;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServiceImplTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private InstructorRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InstructorServiceImpl instructorService;

    @Test
    void submitInstructorRequest_ShouldSavePendingRequest() {
        InstructorRequestDto dto = new InstructorRequestDto();
        dto.setFirstName("Grace");
        dto.setLastName("Hopper");
        dto.setEmail("grace@sms.edu");
        dto.setPhone("+1-555-0100");
        dto.setDesignation("Professor");
        dto.setQualification("Ph.D.");

        when(userRepository.existsByEmail("grace@sms.edu")).thenReturn(false);
        when(requestRepository.existsByEmailAndStatus("grace@sms.edu", "PENDING")).thenReturn(false);
        when(requestRepository.save(any(InstructorRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InstructorRequest result = instructorService.submitInstructorRequest(dto);

        assertNotNull(result);
        assertEquals("Grace Hopper", result.getFullName());
        assertEquals("PENDING", result.getStatus());
        verify(requestRepository, times(1)).save(any(InstructorRequest.class));
    }

    @Test
    void approveInstructorRequest_ShouldCreateInstructorAccountAndSetStatusApproved() {
        InstructorRequest request = new InstructorRequest("Grace", "Hopper", "grace@sms.edu", "+1-555-0100", "Professor", "Ph.D.", null);
        request.setId(10L);

        when(requestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(userRepository.existsByEmail("grace@sms.edu")).thenReturn(false);
        when(userRepository.existsByUsername("grace")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_INSTRUCTOR")).thenReturn(Optional.of(new Role("ROLE_INSTRUCTOR")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(instructorRepository.save(any(Instructor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Instructor approvedInstructor = instructorService.approveInstructorRequest(10L);

        assertNotNull(approvedInstructor);
        assertEquals("APPROVED", request.getStatus());
        assertEquals("Grace Hopper", approvedInstructor.getFullName());
        assertEquals("ACTIVE", approvedInstructor.getStatus());
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }
}
