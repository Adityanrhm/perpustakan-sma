package com.perpustakaan.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TransaksiPeminjaman {
    private int idTransaksi;
    private String kodeTransaksi;
    private Siswa siswa;
    private Buku buku;
    private Pustakawan pustakawan;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalKembaliRencana;
    private LocalDate tanggalKembaliAktual;
    private StatusTransaksi status;
    private BigDecimal denda;
    private String catatan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TransaksiPeminjaman() {
        this.denda = BigDecimal.ZERO;
    }
    
    public TransaksiPeminjaman(String kodeTransaksi, Siswa siswa, Buku buku, Pustakawan pustakawan) {
        this.kodeTransaksi = kodeTransaksi;
        this.siswa = siswa;
        this.buku = buku;
        this.pustakawan = pustakawan;
        this.tanggalPinjam = LocalDate.now();
        this.denda = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }
    
    public String getKodeTransaksi() { return kodeTransaksi; }
    public void setKodeTransaksi(String kodeTransaksi) { this.kodeTransaksi = kodeTransaksi; }
    
    public Siswa getSiswa() { return siswa; }
    public void setSiswa(Siswa siswa) { this.siswa = siswa; }
    
    public Buku getBuku() { return buku; }
    public void setBuku(Buku buku) { this.buku = buku; }
    
    public Pustakawan getPustakawan() { return pustakawan; }
    public void setPustakawan(Pustakawan pustakawan) { this.pustakawan = pustakawan; }
    
    public LocalDate getTanggalPinjam() { return tanggalPinjam; }
    public void setTanggalPinjam(LocalDate tanggalPinjam) { this.tanggalPinjam = tanggalPinjam; }
    
    public LocalDate getTanggalKembaliRencana() { return tanggalKembaliRencana; }
    public void setTanggalKembaliRencana(LocalDate tanggalKembaliRencana) { this.tanggalKembaliRencana = tanggalKembaliRencana; }
    
    public LocalDate getTanggalKembaliAktual() { return tanggalKembaliAktual; }
    public void setTanggalKembaliAktual(LocalDate tanggalKembaliAktual) { this.tanggalKembaliAktual = tanggalKembaliAktual; }
    
    public StatusTransaksi getStatus() { return status; }
    public void setStatus(StatusTransaksi status) { this.status = status; }
    
    public BigDecimal getDenda() { return denda; }
    public void setDenda(BigDecimal denda) { this.denda = denda; }
    
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isDipinjam() {
        return status != null && "DIPINJAM".equals(status.getNamaStatus());
    }
    
    public boolean isTerlambat() {
        if (tanggalKembaliAktual != null) {
            return tanggalKembaliAktual.isAfter(tanggalKembaliRencana);
        } else {
            return LocalDate.now().isAfter(tanggalKembaliRencana);
        }
    }
    
    public long getHariTerlambat() {
        LocalDate tanggalAcuan = tanggalKembaliAktual != null ? 
                                 tanggalKembaliAktual : LocalDate.now();
        
        if (tanggalAcuan.isAfter(tanggalKembaliRencana)) {
            return ChronoUnit.DAYS.between(tanggalKembaliRencana, tanggalAcuan);
        }
        return 0;
    }
    
    public BigDecimal hitungDenda(BigDecimal dendaPerHari) {
        long hariTerlambat = getHariTerlambat();
        if (hariTerlambat > 0) {
            return dendaPerHari.multiply(BigDecimal.valueOf(hariTerlambat));
        }
        return BigDecimal.ZERO;
    }
    
    public long getSisaHariPinjam() {
        if (tanggalKembaliAktual != null) {
            return 0; // Sudah dikembalikan
        }
        
        long sisa = ChronoUnit.DAYS.between(LocalDate.now(), tanggalKembaliRencana);
        return Math.max(0, sisa);
    }
    
    @Override
    public String toString() {
        return kodeTransaksi + " - " + (siswa != null ? siswa.getNamaLengkap() : "");
    }
}