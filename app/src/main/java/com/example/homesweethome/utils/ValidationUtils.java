package com.example.homesweethome.utils;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null
                && !email.trim().isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String cleaned = phone.replaceAll("[\\s\\-()]", "");
        return cleaned.length() == 14 && cleaned.matches("[+]?[0-9]+");
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidOtp(String otp) {
        return otp != null && otp.trim().length() == 6 && otp.trim().matches("[0-9]+");
    }

    public static boolean passwordsMatch(String password, String confirm) {
        return password != null && password.equals(confirm);
    }
}
