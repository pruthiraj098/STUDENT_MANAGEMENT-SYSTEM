package com.sms.service;

import com.sms.dto.UserRegistrationDto;
import com.sms.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User saveUser(UserRegistrationDto registrationDto);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAllUsers();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
