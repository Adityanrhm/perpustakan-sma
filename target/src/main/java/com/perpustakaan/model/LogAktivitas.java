package com.perpustakaan.model;

import java.time.LocalDateTime;

public class LogAktivitas {
    private int idLog;
    private User user;
    private String aktivitas;
    private String detail;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    
    public LogAktivitas() {}
    
    public LogAktivitas(User user, String aktivitas, String detail) {
        this.user = user;
        this.aktivitas = aktivitas;
        this.detail = detail;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getIdLog() { return idLog; }
    public void setIdLog(int idLog) { this.idLog = idLog; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getAktivitas() { return aktivitas; }
    public void setAktivitas(String aktivitas) { this.aktivitas = aktivitas; }
    
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}