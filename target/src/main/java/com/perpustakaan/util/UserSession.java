package com.perpustakaan.util;

import com.perpustakaan.model.User;
import java.time.LocalDateTime;


public class UserSession {
    private static UserSession instance;
    private User currentUser;
    private LocalDateTime loginTime;
    private String ipAddress;
    
    private UserSession() {}
    
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    public void startSession(User user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
        this.ipAddress = getLocalIPAddress();
        
        // Log aktivitas login
        System.out.println("User " + user.getUsername() + " logged in at " + loginTime);
    }
    
    public void endSession() {
        if (currentUser != null) {
            System.out.println("User " + currentUser.getUsername() + " logged out");
            this.currentUser = null;
            this.loginTime = null;
            this.ipAddress = null;
        }
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole().getNamaRole() : null;
    }
    
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public boolean hasRole(String roleName) {
        return currentUser != null && 
               currentUser.getRole().getNamaRole().equalsIgnoreCase(roleName);
    }
    
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    public boolean isPustakawan() {
        return hasRole("PUSTAKAWAN");
    }
    
    public boolean isSiswa() {
        return hasRole("SISWA");
    }
    
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getIdUser() : 0;
    }
    
    private String getLocalIPAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    // Method untuk validasi session timeout (opsional)
    public boolean isSessionExpired(int timeoutMinutes) {
        if (loginTime == null) return true;
        return loginTime.plusMinutes(timeoutMinutes).isBefore(LocalDateTime.now());
    }
    
    // Method untuk mendapatkan durasi login
    public String getLoginDuration() {
        if (loginTime == null) return "N/A";
        
        long minutes = java.time.Duration.between(loginTime, LocalDateTime.now()).toMinutes();
        long hours = minutes / 60;
        minutes = minutes % 60;
        
        if (hours > 0) {
            return hours + " jam " + minutes + " menit";
        } else {
            return minutes + " menit";
        }
    }
}