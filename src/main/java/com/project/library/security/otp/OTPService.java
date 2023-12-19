package com.project.library.security.otp;

import java.security.SecureRandom;

public class OTPService {
    private static final String ALLOWED_CHARACTERS = "0123456789";
    private static final int SECRET_KEY_LENGTH = 6;

    public static String generateRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder secretKey = new StringBuilder(SECRET_KEY_LENGTH);

        for (int i = 0; i < SECRET_KEY_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            secretKey.append(randomChar);
        }

        return secretKey.toString();
    }
}
