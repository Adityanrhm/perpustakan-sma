package com.perpustakaan.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Model class untuk Detail Transaksi Peminjaman - NEW
 */
public class DetailTransaksiPeminjaman {
    private int idDetail;
    private TransaksiPeminjaman transaksi; // parent transaction
    private Buku buku;
    private int jumlahPinjam;
    private LocalDate tanggalKembaliRencana;
    private LocalDate tanggalKembaliAktual;
    private int jumlahKembali;
    private int jumlahHilang;
    private int jumlahRusak;
    private StatusTransaksi status;
    private BigDecimal dendaPerItem;
    private BigDecimal totalDendaItem;
    private String catatanDetail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public DetailTransaksiPeminjaman() {
        this.dendaPerItem = BigDecimal.ZERO;
        this.totalDendaItem = BigDecimal.ZERO;
        this.jumlahPinjam = 1;
        this.jumlahKembali = 0;
        this.jumlahHilang = 0;
        this.jumlahRusak = 0;
    }
    
    // Getters and Setters
    public int getIdDetail() { return idDetail; }
    public void setIdDetail(int idDetail) { this.idDetail = idDetail; }
    
    public TransaksiPeminjaman getTransaksi() { return transaksi; }
    public void setTransaksi(TransaksiPeminjaman transaksi) { this.transaksi = transaksi; }
    
    public Buku getBuku() { return buku; }
    public void setBuku(Buku buku) { this.buku = buku; }
    
    public int getJumlahPinjam() { return jumlahPinjam; }
    public void setJumlahPinjam(int jumlahPinjam) { this.jumlahPinjam = jumlahPinjam; }
    
    public LocalDate getTanggalKembaliRencana() { return tanggalKembaliRencana; }
    public void setTanggalKembaliRencana(LocalDate tanggalKembaliRencana) { this.tanggalKembaliRencana = tanggalKembaliRencana; }
    
    public LocalDate getTanggalKembaliAktual() { return tanggalKembaliAktual; }
    public void setTanggalKembaliAktual(LocalDate tanggalKembaliAktual) { this.tanggalKembaliAktual = tanggalKembaliAktual; }
    
    public int getJumlahKembali() { return jumlahKembali; }
    public void setJumlahKembali(int jumlahKembali) { this.jumlahKembali = jumlahKembali; }
    
    public int getJumlahHilang() { return jumlahHilang; }
    public void setJumlahHilang(int jumlahHilang) { this.jumlahHilang = jumlahHilang; }
    
    public int getJumlahRusak() { return jumlahRusak; }
    public void setJumlahRusak(int jumlahRusak) { this.jumlahRusak = jumlahRusak; }
    
    public StatusTransaksi getStatus() { return status; }
    public void setStatus(StatusTransaksi status) { this.status = status; }
    
    public BigDecimal getDendaPerItem() { return dendaPerItem; }
    public void setDendaPerItem(BigDecimal dendaPerItem) { this.dendaPerItem = dendaPerItem; }
    
    public BigDecimal getTotalDendaItem() { return totalDendaItem; }
    public void setTotalDendaItem(BigDecimal totalDendaItem) { this.totalDendaItem = totalDendaItem; }
    
    public String getCatatanDetail() { return catatanDetail; }
    public void setCatatanDetail(String catatanDetail) { this.catatanDetail = catatanDetail; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public int getJumlahBelumKembali() {
        return jumlahPinjam - jumlahKembali - jumlahHilang - jumlahRusak;
    }
    
    public boolean isLunas() {
        return getJumlahBelumKembali() <= 0;
    }
    
    public boolean isTerlambat() {
        if (tanggalKembaliRencana == null) return false;
        
        LocalDate tanggalAcuan = tanggalKembaliAktual != null ? tanggalKembaliAktual : LocalDate.now();
        return tanggalAcuan.isAfter(tanggalKembaliRencana);
    }
    
    public long getHariTerlambat() {
        if (tanggalKembaliRencana == null) return 0;
        
        LocalDate tanggalAcuan = tanggalKembaliAktual != null ? tanggalKembaliAktual : LocalDate.now();
        
        if (tanggalAcuan.isAfter(tanggalKembaliRencana)) {
            return ChronoUnit.DAYS.between(tanggalKembaliRencana, tanggalAcuan);
        }
        return 0;
    }
    
    public long getSisaHariPinjam() {
        if (isLunas() || tanggalKembaliRencana == null) return 0;
        
        long sisa = ChronoUnit.DAYS.between(LocalDate.now(), tanggalKembaliRencana);
        return Math.max(0, sisa);
    }
    
    public String getStatusDetail() {
        if (isLunas()) {
            if (jumlahHilang > 0) return "HILANG";
            if (jumlahRusak > 0) return "RUSAK";
            return "DIKEMBALIKAN";
        } else {
            if (isTerlambat()) return "TERLAMBAT";
            return "DIPINJAM";
        }
    }
    
    public BigDecimal hitungDenda(BigDecimal dendaPerHari) {
        BigDecimal totalDenda = BigDecimal.ZERO;
        
        // Denda keterlambatan
        long hariTerlambat = getHariTerlambat();
        if (hariTerlambat > 0) {
            totalDenda = totalDenda.add(dendaPerHari.multiply(BigDecimal.valueOf(hariTerlambat * jumlahPinjam)));
        }
        
        // Denda rusak ringan: Rp 10,000 per item
        if (jumlahRusak > 0) {
            totalDenda = totalDenda.add(new BigDecimal("10000").multiply(BigDecimal.valueOf(jumlahRusak)));
        }
        
        // Denda hilang: harga buku atau default
        if (jumlahHilang > 0) {
            BigDecimal hargaBuku = buku != null && buku.getHarga() != null ? 
                                  buku.getHarga() : new BigDecimal("100000");
            totalDenda = totalDenda.add(hargaBuku.multiply(BigDecimal.valueOf(jumlahHilang)));
        }
        
        return totalDenda;
    }
    
    @Override
    public String toString() {
        return buku != null ? buku.getJudul() + " (x" + jumlahPinjam + ")" : "Detail #" + idDetail;
    }
}