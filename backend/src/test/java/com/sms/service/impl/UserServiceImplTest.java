package com.sms.service.impl;

import com.sms.dto.UserRegistrationDto;
import com.sms.entity.Role;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.repository.RoleRepository;
import com.sms.repository.StudentRepository;
import com.sms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Role studentRole;

    @BeforeEach
    void setUp() {
        studentRole = new Role("ROLE_STUDENT");
    }

    @Test
    void saveUser_ShouldForceRoleStudent_EvenIfCustomRoleProvided() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("malicious@sms.edu");
        dto.setPassword("password123");
        dto.setFirstName("Attacker");

        when(userRepository.existsByUsername("malicious")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User savedUser = userService.saveUser(dto);

        assertNotNull(savedUser);
        assertEquals("malicious@sms.edu", savedUser.getEmail());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_STUDENT")));
        verify(roleRepository, times(1)).findByName("ROLE_STUDENT");
        verify(roleRepository, never()).findByName("ROLE_ADMIN");
        verify(studentRepository, times(1)).save(any(Student.class));
    }
}
