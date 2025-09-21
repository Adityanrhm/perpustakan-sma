package com.perpustakaan.model;

import java.time.LocalDateTime;

/**
 * Model class untuk Role
 */
public class Role {
    private int idRole;
    private String namaRole;
    private String deskripsi;
    
    public Role() {}
    
    public Role(int idRole, String namaRole, String deskripsi) {
        this.idRole = idRole;
        this.namaRole = namaRole;
        this.deskripsi = deskripsi;
    }
    
    // Getters and Setters
    public int getIdRole() { return idRole; }
    public void setIdRole(int idRole) { this.idRole = idRole; }
    
    public String getNamaRole() { return namaRole; }
    public void setNamaRole(String namaRole) { this.namaRole = namaRole; }
    
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    
    @Override
    public String toString() {
        return namaRole;
    }
}
