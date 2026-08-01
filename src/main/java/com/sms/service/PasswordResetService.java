package com.sms.service;

import com.sms.entity.PasswordResetToken;
import com.sms.entity.User;

public interface PasswordResetService {
    PasswordResetToken createPasswordResetToken(String email);
    boolean validateToken(String token);
    User getUserByToken(String token);
    boolean resetPassword(String token, String newPassword);
}
