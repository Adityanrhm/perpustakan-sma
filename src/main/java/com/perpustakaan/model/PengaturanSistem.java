package com.perpustakaan.model;

import java.time.LocalDateTime;

public class PengaturanSistem {
    private int idPengaturan;
    private String namaPengaturan;
    private String nilai;
    private String deskripsi;
    private LocalDateTime updatedAt;
    
    public PengaturanSistem() {}
    
    public PengaturanSistem(String namaPengaturan, String nilai, String deskripsi) {
        this.namaPengaturan = namaPengaturan;
        this.nilai = nilai;
        this.deskripsi = deskripsi;
    }
    
    // Getters and Setters
    public int getIdPengaturan() { return idPengaturan; }
    public void setIdPengaturan(int idPengaturan) { this.idPengaturan = idPengaturan; }
    
    public String getNamaPengaturan() { return namaPengaturan; }
    public void setNamaPengaturan(String namaPengaturan) { this.namaPengaturan = namaPengaturan; }
    
    public String getNilai() { return nilai; }
    public void setNilai(String nilai) { this.nilai = nilai; }
    
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods untuk convert nilai
    public int getNilaiAsInt() {
        try {
            return Integer.parseInt(nilai);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public double getNilaiAsDouble() {
        try {
            return Double.parseDouble(nilai);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public boolean getNilaiAsBoolean() {
        return "true".equalsIgnoreCase(nilai) || "1".equals(nilai);
    }
}