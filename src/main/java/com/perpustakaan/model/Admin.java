package com.perpustakaan.model;

/**
 * Model class untuk Admin
 */
public class Admin {
    private int idAdmin;
    private User user;
    private String nip;
    private String namaLengkap;
    private String email;
    private String noTelepon;
    private String alamat;
    
    public Admin() {}
    
    public Admin(String nip, String namaLengkap, String email) {
        this.nip = nip;
        this.namaLengkap = namaLengkap;
        this.email = email;
    }
    
    // Getters and Setters
    public int getIdAdmin() { return idAdmin; }
    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getNip() { return nip; }
    public void setNip(String nip) { this.nip = nip; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
    
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    
    @Override
    public String toString() {
        return nip + " - " + namaLengkap;
    }
}