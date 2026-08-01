package com.sms.config;

import com.sms.entity.*;
import com.sms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           InstructorRepository instructorRepository,
                           CourseRepository courseRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() > 0) {
            return; // Data already initialized
        }

        // 1. Roles
        Role adminRole = roleRepository.save(new Role("ROLE_ADMIN"));
        Role instructorRole = roleRepository.save(new Role("ROLE_INSTRUCTOR"));
        Role studentRole = roleRepository.save(new Role("ROLE_STUDENT"));

        // 2. Default Admin User
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@sms.edu");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setEnabled(true);
        adminUser.setRoles(Collections.singleton(adminRole));
        userRepository.save(adminUser);

        // 3. Departments
        Department csDept = departmentRepository.save(new Department("CSE", "Computer Science & Engineering", "Department of Software Systems and AI", "Turing Hall"));
        Department eeDept = departmentRepository.save(new Department("EEE", "Electrical Engineering", "Department of Power & Microelectronics", "Tesla Building"));
        Department mbaDept = departmentRepository.save(new Department("MBA", "Business Administration", "Department of Finance & Management", "Economics Wing"));
        Department mathDept = departmentRepository.save(new Department("MATH", "Mathematics & Statistics", "Department of Pure and Applied Mathematics", "Newton Science Complex"));

        // 4. Instructor User & Entity
        User instUser = new User();
        instUser.setUsername("instructor");
        instUser.setEmail("dr.alan@sms.edu");
        instUser.setPassword(passwordEncoder.encode("instructor123"));
        instUser.setEnabled(true);
        instUser.setRoles(Collections.singleton(instructorRole));
        userRepository.save(instUser);

        Instructor profAlan = new Instructor("INS101", "Alan", "Turing", "dr.alan@sms.edu", "+1-555-0101", "Professor", csDept);
        profAlan.setUser(instUser);
        instructorRepository.save(profAlan);

        Instructor profGrace = new Instructor("INS102", "Grace", "Hopper", "dr.grace@sms.edu", "+1-555-0102", "Associate Professor", csDept);
        instructorRepository.save(profGrace);

        Instructor profNikola = new Instructor("INS103", "Nikola", "Tesla", "dr.nikola@sms.edu", "+1-555-0103", "Professor", eeDept);
        instructorRepository.save(profNikola);

        // 5. Courses
        Course cs101 = courseRepository.save(new Course("CS101", "Introduction to Programming & Java", "Core Java, OOP principles, and basic algorithm design.", 4, csDept, profAlan));
        Course cs202 = courseRepository.save(new Course("CS202", "Data Structures & Algorithms", "Linear and non-linear data structures, searching, sorting, and complexity analysis.", 4, csDept, profGrace));
        Course ee101 = courseRepository.save(new Course("EE101", "Circuit Theory & Analysis", "DC/AC circuit fundamentals, Kirchhoff's laws, and network theorems.", 3, eeDept, profNikola));
        Course mba501 = courseRepository.save(new Course("MBA501", "Strategic Corporate Management", "Leadership, organizational behavior, and modern market strategy.", 3, mbaDept, null));
        Course math301 = courseRepository.save(new Course("MATH301", "Linear Algebra & Differential Equations", "Vector spaces, matrices, eigenvalues, and system modeling.", 3, mathDept, null));
    }
}
