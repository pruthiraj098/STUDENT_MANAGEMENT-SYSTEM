package com.sms.service.impl;

import com.sms.dto.UserRegistrationDto;
import com.sms.entity.Role;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.repository.RoleRepository;
import com.sms.repository.StudentRepository;
import com.sms.repository.UserRepository;
import com.sms.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User saveUser(UserRegistrationDto registrationDto) {
        String cleanEmail = registrationDto.getEmail().trim().toLowerCase();
        String baseUsername = cleanEmail.split("@")[0];
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix++;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(cleanEmail);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEnabled(true);

        // Public registration MUST ALWAYS assign ROLE_STUDENT regardless of any input
        final String roleName = "ROLE_STUDENT";

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        user.setRoles(Collections.singleton(role));
        User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setStudentId("STU" + (1000 + savedUser.getId()));

        String firstName = (registrationDto.getFirstName() != null && !registrationDto.getFirstName().isBlank())
                ? registrationDto.getFirstName().trim()
                : Character.toUpperCase(baseUsername.charAt(0)) + (baseUsername.length() > 1 ? baseUsername.substring(1) : "");
        String lastName = (registrationDto.getLastName() != null && !registrationDto.getLastName().isBlank())
                ? registrationDto.getLastName().trim()
                : "Student";

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(cleanEmail);
        if (registrationDto.getPhone() != null) {
            student.setPhone(registrationDto.getPhone().trim());
        }
        student.setAdmissionDate(LocalDate.now());
        student.setStatus("ACTIVE");
        student.setUser(savedUser);
        studentRepository.save(student);

        return savedUser;
    }

    @Override
    @Transactional
    public User createAdminUser(String email, String password, String username) {
        String cleanEmail = email.trim().toLowerCase();
        String finalUsername = (username != null && !username.isBlank()) ? username.trim() : cleanEmail.split("@")[0];

        int suffix = 1;
        String tempUser = finalUsername;
        while (userRepository.existsByUsername(tempUser)) {
            tempUser = finalUsername + suffix++;
        }
        finalUsername = tempUser;

        User user = new User();
        user.setUsername(finalUsername);
        user.setEmail(cleanEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        user.setRoles(Collections.singleton(adminRole));
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) return null;
        return userRepository.findByUsernameIgnoreCase(username.trim())
                .orElseGet(() -> userRepository.findByUsername(username.trim()).orElse(null));
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) return null;
        return userRepository.findByEmailIgnoreCase(email.trim())
                .orElseGet(() -> userRepository.findByEmail(email.trim()).orElse(null));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null) return false;
        return userRepository.existsByUsername(username.trim());
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return userRepository.existsByEmailIgnoreCase(email.trim()) || userRepository.existsByEmail(email.trim());
    }

    /**
     * Spring Security calls this with whatever value was submitted in the
     * login form's "username" field. We look up by email (exact or case-insensitive)
     * and fall back to username for full compatibility.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
            throw new UsernameNotFoundException("No username or email provided.");
        }
        String cleanInput = emailOrUsername.trim();

        User user = userRepository.findByEmailIgnoreCase(cleanInput)
                .or(() -> userRepository.findByEmail(cleanInput))
                .or(() -> userRepository.findByUsernameIgnoreCase(cleanInput))
                .or(() -> userRepository.findByUsername(cleanInput))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No account found for: " + emailOrUsername));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
