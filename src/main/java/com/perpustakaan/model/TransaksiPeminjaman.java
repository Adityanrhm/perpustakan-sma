package com.perpustakaan.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


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
    private int totalBuku;
    private BigDecimal totalDenda;
    private String statusKeseluruhan; // AKTIF, SELESAI, SEBAGIAN_DIKEMBALIKAN
    private List<DetailTransaksiPeminjaman> detailItems;
    
    public TransaksiPeminjaman() {
        this.denda = BigDecimal.ZERO;
        this.detailItems = new ArrayList<>();
        this.totalBuku = 0;
        this.totalDenda = BigDecimal.ZERO;
        this.statusKeseluruhan = "AKTIF";
    }

    
    public TransaksiPeminjaman(String kodeTransaksi, Siswa siswa, Buku buku, Pustakawan pustakawan) {
        this.kodeTransaksi = kodeTransaksi;
        this.siswa = siswa;
        this.buku = buku;
        this.pustakawan = pustakawan;
        this.tanggalPinjam = LocalDate.now();
        this.denda = BigDecimal.ZERO;
        this.detailItems = new ArrayList<>();
        this.totalBuku = 0;
        this.totalDenda = BigDecimal.ZERO;
        this.statusKeseluruhan = "AKTIF";
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
    
    public int getTotalBuku() { return totalBuku; }
    public void setTotalBuku(int totalBuku) { this.totalBuku = totalBuku; }

    public BigDecimal getTotalDenda() { return totalDenda; }
    public void setTotalDenda(BigDecimal totalDenda) { this.totalDenda = totalDenda; }

    public String getStatusKeseluruhan() { return statusKeseluruhan; }
    public void setStatusKeseluruhan(String statusKeseluruhan) { this.statusKeseluruhan = statusKeseluruhan; }

    public List<DetailTransaksiPeminjaman> getDetailItems() { return detailItems; }
    public void setDetailItems(List<DetailTransaksiPeminjaman> detailItems) { this.detailItems = detailItems; }

    
    // Helper methods - FIXED VERSION
    public boolean isDipinjam() {
        return status != null && "DIPINJAM".equals(status.getNamaStatus()) && tanggalKembaliAktual == null;
    }
    
    public boolean isTerlambat() {
        if (tanggalKembaliRencana == null) return false;
        
        LocalDate tanggalAcuan;
        if (tanggalKembaliAktual != null) {
            // Sudah dikembalikan, cek apakah terlambat saat dikembalikan
            tanggalAcuan = tanggalKembaliAktual;
        } else {
            // Belum dikembalikan, cek apakah sudah terlambat hari ini
            tanggalAcuan = LocalDate.now();
        }
        
        return tanggalAcuan.isAfter(tanggalKembaliRencana);
    }
    
    public long getHariTerlambat() {
        if (tanggalKembaliRencana == null) return 0;
        
        LocalDate tanggalAcuan;
        if (tanggalKembaliAktual != null) {
            // Sudah dikembalikan, hitung keterlambatan saat dikembalikan
            tanggalAcuan = tanggalKembaliAktual;
        } else {
            // Belum dikembalikan, hitung keterlambatan sampai hari ini
            tanggalAcuan = LocalDate.now();
        }
        
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
        if (tanggalKembaliAktual != null || tanggalKembaliRencana == null) {
            return 0; // Sudah dikembalikan atau tidak ada tanggal rencana
        }
        
        long sisa = ChronoUnit.DAYS.between(LocalDate.now(), tanggalKembaliRencana);
        return Math.max(0, sisa);
    }
    
    // TAMBAHAN: Method untuk menentukan status aktual berdasarkan kondisi
    public String getStatusAktual() {
        if (tanggalKembaliAktual != null) {
            // Sudah dikembalikan
            if (status != null && ("HILANG".equals(status.getNamaStatus()) || "RUSAK".equals(status.getNamaStatus()))) {
                return status.getNamaStatus();
            }
            return "DIKEMBALIKAN";
        } else {
            // Belum dikembalikan
            if (isTerlambat()) {
                return "TERLAMBAT";
            } else {
                return "DIPINJAM";
            }
        }
    }
    
    public void addDetailItem(DetailTransaksiPeminjaman detail) {
        detail.setTransaksi(this);
        this.detailItems.add(detail);
    }

    public void removeDetailItem(DetailTransaksiPeminjaman detail) {
        this.detailItems.remove(detail);
    }

    public int getTotalBukuBelumKembali() {
        return detailItems.stream()
            .mapToInt(detail -> detail.getJumlahBelumKembali())
            .sum();
    }

    public boolean isAllItemsReturned() {
        return getTotalBukuBelumKembali() == 0;
    }

    public boolean hasOverdueItems() {
        return detailItems.stream()
            .anyMatch(DetailTransaksiPeminjaman::isTerlambat);
    }

    public BigDecimal calculateTotalFine(BigDecimal dendaPerHari) {
        return detailItems.stream()
            .map(detail -> detail.hitungDenda(dendaPerHari))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    
    @Override
    public String toString() {
        return kodeTransaksi + " - " + (siswa != null ? siswa.getNamaLengkap() : "");
    }
}