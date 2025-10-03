package com.perpustakaan.model;

public class StatusTransaksi {
    private int idStatus;
    private String namaStatus;
    private String deskripsi;
    
    public StatusTransaksi() {}
    
    public StatusTransaksi(String namaStatus, String deskripsi) {
        this.namaStatus = namaStatus;
        this.deskripsi = deskripsi;
    }
    
    // Getters and Setters
    public int getIdStatus() { return idStatus; }
    public void setIdStatus(int idStatus) { this.idStatus = idStatus; }
    
    public String getNamaStatus() { return namaStatus; }
    public void setNamaStatus(String namaStatus) { this.namaStatus = namaStatus; }
    
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    
    @Override
    public String toString() {
        return namaStatus;
    }
}