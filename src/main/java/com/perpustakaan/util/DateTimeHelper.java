package com.perpustakaan.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class untuk operasi tanggal dan waktu
 */
public class DateTimeHelper {
    
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter SQL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter SQL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Format LocalDate untuk tampilan (dd/MM/yyyy)
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Format LocalDateTime untuk tampilan (dd/MM/yyyy HH:mm:ss)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Format LocalDate untuk SQL (yyyy-MM-dd)
     */
    public static String formatDateForSQL(LocalDate date) {
        if (date == null) return "";
        return date.format(SQL_DATE_FORMATTER);
    }
    
    /**
     * Format LocalDateTime untuk SQL (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDateTimeForSQL(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(SQL_DATETIME_FORMATTER);
    }
    
    /**
     * Parse string tanggal dari format dd/MM/yyyy
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            // Try SQL format as fallback
            try {
                return LocalDate.parse(dateString.trim(), SQL_DATE_FORMATTER);
            } catch (Exception ex) {
                return null;
            }
        }
    }
    
    /**
     * Parse string tanggal dari format SQL yyyy-MM-dd
     */
    public static LocalDate parseSQLDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        
        try {
            return LocalDate.parse(dateString.trim(), SQL_DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Hitung selisih hari antara dua tanggal
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * Cek apakah tanggal sudah lewat (overdue)
     */
    public static boolean isOverdue(LocalDate dueDate) {
        if (dueDate == null) return false;
        return LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * Tambah hari ke tanggal
     */
    public static LocalDate addDays(LocalDate date, int days) {
        if (date == null) return null;
        return date.plusDays(days);
    }
    
    /**
     * Kurangi hari dari tanggal
     */
    public static LocalDate subtractDays(LocalDate date, int days) {
        if (date == null) return null;
        return date.minusDays(days);
    }
    
    /**
     * Get tanggal hari ini
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Get tanggal dan waktu sekarang
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * Cek apakah tahun kabisat
     */
    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }
    
    /**
     * Get nama hari dalam bahasa Indonesia
     */
    public static String getDayNameIndonesian(LocalDate date) {
        if (date == null) return "";
        
        String[] dayNames = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        return dayNames[date.getDayOfWeek().getValue() - 1];
    }
    
    /**
     * Get nama bulan dalam bahasa Indonesia
     */
    public static String getMonthNameIndonesian(LocalDate date) {
        if (date == null) return "";
        
        String[] monthNames = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };
        return monthNames[date.getMonthValue() - 1];
    }
    
    /**
     * Format tanggal lengkap dalam bahasa Indonesia
     */
    public static String formatDateIndonesian(LocalDate date) {
        if (date == null) return "";
        
        return getDayNameIndonesian(date) + ", " + 
               date.getDayOfMonth() + " " + 
               getMonthNameIndonesian(date) + " " + 
               date.getYear();
    }
}