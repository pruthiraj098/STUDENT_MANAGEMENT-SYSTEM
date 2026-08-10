package com.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/webjars/**"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/register"),
                    new AntPathRequestMatcher("/register/instructor"),
                    new AntPathRequestMatcher("/forgot-password"),
                    new AntPathRequestMatcher("/reset-password"),
                    new AntPathRequestMatcher("/access-denied"),
                    new AntPathRequestMatcher("/error")
                ).permitAll()
                .requestMatchers(
                    new AntPathRequestMatcher("/instructors/**"),
                    new AntPathRequestMatcher("/admin/**"),
                    new AntPathRequestMatcher("/students/new"),
                    new AntPathRequestMatcher("/students/edit/**"),
                    new AntPathRequestMatcher("/students/save"),
                    new AntPathRequestMatcher("/students/delete/**"),
                    new AntPathRequestMatcher("/students/*/activities/add"),
                    new AntPathRequestMatcher("/students/activities/delete/**"),
                    new AntPathRequestMatcher("/courses/new"),
                    new AntPathRequestMatcher("/courses/edit/**"),
                    new AntPathRequestMatcher("/courses/save"),
                    new AntPathRequestMatcher("/courses/delete/**"),
                    new AntPathRequestMatcher("/departments/new"),
                    new AntPathRequestMatcher("/departments/edit/**"),
                    new AntPathRequestMatcher("/departments/save"),
                    new AntPathRequestMatcher("/departments/delete/**"),
                    new AntPathRequestMatcher("/enrollments/assign"),
                    new AntPathRequestMatcher("/enrollments/save"),
                    new AntPathRequestMatcher("/enrollments/delete/**")
                ).hasRole("ADMIN")
                .requestMatchers(
                    new AntPathRequestMatcher("/students/*/update-attendance-lab"),
                    new AntPathRequestMatcher("/enrollments/grade"),
                    new AntPathRequestMatcher("/enrollments/update-assessment")
                ).hasAnyRole("ADMIN", "INSTRUCTOR")
                .requestMatchers(
                    new AntPathRequestMatcher("/dashboard"),
                    new AntPathRequestMatcher("/students"),
                    new AntPathRequestMatcher("/students/view/**"),
                    new AntPathRequestMatcher("/students/transcript/**"),
                    new AntPathRequestMatcher("/departments"),
                    new AntPathRequestMatcher("/courses"),
                    new AntPathRequestMatcher("/enrollments")
                ).hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}
