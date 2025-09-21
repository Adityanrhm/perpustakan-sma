package com.perpustakaan.util;

import java.util.regex.Pattern;

/**
 * Utility class untuk validasi data
 */
public class ValidationHelper {
    
    // Email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Phone pattern (Indonesian format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+62|62|0)8[1-9][0-9]{6,10}$"
    );
    
    // NIS pattern (digits only, 6-20 characters)
    private static final Pattern NIS_PATTERN = Pattern.compile("^[0-9]{6,20}$");
    
    // NIP pattern (18 digits)
    private static final Pattern NIP_PATTERN = Pattern.compile("^[0-9]{18}$");
    
    // Username pattern (alphanumeric and underscore, 3-50 characters)
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number (Indonesian format)
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional in most cases
        }
        // Remove spaces and dashes
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validate NIS format
     */
    public static boolean isValidNis(String nis) {
        if (nis == null || nis.trim().isEmpty()) {
            return false;
        }
        return NIS_PATTERN.matcher(nis.trim()).matches();
    }
    
    /**
     * Validate NIP format
     */
    public static boolean isValidNip(String nip) {
        if (nip == null || nip.trim().isEmpty()) {
            return false;
        }
        return NIP_PATTERN.matcher(nip.trim()).matches();
    }
    
    /**
     * Validate username format
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return true; // Basic validation, can be enhanced
    }
    
    /**
     * Validate date string format (YYYY-MM-DD)
     */
    public static boolean isValidDateFormat(String date) {
        if (date == null || date.trim().isEmpty()) {
            return true; // Date is optional in some cases
        }
        
        try {
            java.time.LocalDate.parse(date.trim());
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * Sanitize string input (remove dangerous characters)
     */
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        
        return input.trim()
                   .replaceAll("[<>\"'&]", "") // Remove potentially dangerous characters
                   .substring(0, Math.min(input.length(), 255)); // Limit length
    }
    
    /**
     * Validate numeric input
     */
    public static boolean isValidNumber(String number) {
        if (isEmpty(number)) return false;
        
        try {
            Double.parseDouble(number.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate positive integer
     */
    public static boolean isValidPositiveInteger(String number) {
        if (isEmpty(number)) return false;
        
        try {
            int value = Integer.parseInt(number.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}