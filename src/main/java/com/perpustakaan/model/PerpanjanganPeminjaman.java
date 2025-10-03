package com.perpustakaan.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PerpanjanganPeminjaman {
    
    private int idPerpanjangan;
    private DetailTransaksiPeminjaman detail;
    private LocalDate tanggalPerpanjangan;
    private LocalDate tanggalKembaliLama;
    private LocalDate tanggalKembaliBaru;
    private Pustakawan pustakawan;
    private String alasan;
    private LocalDateTime createdAt;
    
    public PerpanjanganPeminjaman() {
        this.tanggalPerpanjangan = LocalDate.now();
    }
    
    // Getters and Setters
    public int getIdPerpanjangan() { return idPerpanjangan; }
    public void setIdPerpanjangan(int idPerpanjangan) { this.idPerpanjangan = idPerpanjangan; }
    
    public DetailTransaksiPeminjaman getDetail() { return detail; }
    public void setDetail(DetailTransaksiPeminjaman detail) { this.detail = detail; }
    
    public LocalDate getTanggalPerpanjangan() { return tanggalPerpanjangan; }
    public void setTanggalPerpanjangan(LocalDate tanggalPerpanjangan) { this.tanggalPerpanjangan = tanggalPerpanjangan; }
    
    public LocalDate getTanggalKembaliLama() { return tanggalKembaliLama; }
    public void setTanggalKembaliLama(LocalDate tanggalKembaliLama) { this.tanggalKembaliLama = tanggalKembaliLama; }
    
    public LocalDate getTanggalKembaliBaru() { return tanggalKembaliBaru; }
    public void setTanggalKembaliBaru(LocalDate tanggalKembaliBaru) { this.tanggalKembaliBaru = tanggalKembaliBaru; }
    
    public Pustakawan getPustakawan() { return pustakawan; }
    public void setPustakawan(Pustakawan pustakawan) { this.pustakawan = pustakawan; }
    
    public String getAlasan() { return alasan; }
    public void setAlasan(String alasan) { this.alasan = alasan; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public long getDurasiPerpanjangan() {
        if (tanggalKembaliLama == null || tanggalKembaliBaru == null) return 0;
        return ChronoUnit.DAYS.between(tanggalKembaliLama, tanggalKembaliBaru);
    }
}