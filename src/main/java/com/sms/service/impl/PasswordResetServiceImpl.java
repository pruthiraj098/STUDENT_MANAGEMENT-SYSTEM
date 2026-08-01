package com.sms.service.impl;

import com.sms.entity.PasswordResetToken;
import com.sms.entity.User;
import com.sms.repository.PasswordResetTokenRepository;
import com.sms.repository.UserRepository;
import com.sms.service.PasswordResetService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetTokenRepository tokenRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public PasswordResetToken createPasswordResetToken(String emailOrUsername) {
        if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
            return null;
        }

        String cleanInput = emailOrUsername.trim();
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(cleanInput)
                .or(() -> userRepository.findByEmail(cleanInput))
                .or(() -> userRepository.findByUsernameIgnoreCase(cleanInput))
                .or(() -> userRepository.findByUsername(cleanInput));

        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();

        // Delete any existing reset token for this user
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String tokenString = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(60); // Valid for 1 hour

        PasswordResetToken token = new PasswordResetToken(tokenString, user, expiry);
        return tokenRepository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token.trim());
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        return !resetToken.isExpired();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByToken(String token) {
        if (!validateToken(token)) {
            return null;
        }

        return tokenRepository.findByToken(token.trim())
                .map(PasswordResetToken::getUser)
                .orElse(null);
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        if (!validateToken(token) || newPassword == null || newPassword.length() < 6) {
            return false;
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token.trim()).orElse(null);
        if (resetToken == null) {
            return false;
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Clean up token after successful reset
        tokenRepository.delete(resetToken);
        return true;
    }
}
