package com.perpustakaan.model;

import java.time.LocalDateTime;

/**
 * Model class untuk User
 */
public class User {
    private int idUser;
    private String username;
    private String nis;
    private String nip;
    private String password;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public User() {}
    
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getNis() { return nis; }
    public void setNis(String nis) { this.nis = nis; }
    
    public String getNip() { return nip; }
    public void setNip(String nip) { this.nip = nip; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Method untuk mendapatkan identifier login (username, nis, atau nip)
    public String getLoginIdentifier() {
        if (username != null && !username.trim().isEmpty()) return username;
        if (nis != null && !nis.trim().isEmpty()) return nis;
        if (nip != null && !nip.trim().isEmpty()) return nip;
        return "";
    }
}