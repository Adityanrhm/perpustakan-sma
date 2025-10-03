package com.perpustakaan.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Buku {
    private int idBuku;
    private String kodeBuku;
    private String isbn;
    private String judul;
    private String pengarang;
    private String penerbit;
    private int tahunTerbit;
    private KategoriBuku kategori;
    private Rak rak;
    private int jumlahTotal;
    private int jumlahTersedia;
    private BigDecimal harga;
    private String deskripsi;
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Buku() {}
    
    public Buku(String kodeBuku, String judul, String pengarang, KategoriBuku kategori, Rak rak) {
        this.kodeBuku = kodeBuku;
        this.judul = judul;
        this.pengarang = pengarang;
        this.kategori = kategori;
        this.rak = rak;
        this.jumlahTotal = 1;
        this.jumlahTersedia = 1;
    }
    
    // Getters and Setters
    public int getIdBuku() { return idBuku; }
    public void setIdBuku(int idBuku) { this.idBuku = idBuku; }
    
    public String getKodeBuku() { return kodeBuku; }
    public void setKodeBuku(String kodeBuku) { this.kodeBuku = kodeBuku; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    
    public String getPengarang() { return pengarang; }
    public void setPengarang(String pengarang) { this.pengarang = pengarang; }
    
    public String getPenerbit() { return penerbit; }
    public void setPenerbit(String penerbit) { this.penerbit = penerbit; }
    
    public int getTahunTerbit() { return tahunTerbit; }
    public void setTahunTerbit(int tahunTerbit) { this.tahunTerbit = tahunTerbit; }
    
    public KategoriBuku getKategori() { return kategori; }
    public void setKategori(KategoriBuku kategori) { this.kategori = kategori; }
    
    public Rak getRak() { return rak; }
    public void setRak(Rak rak) { this.rak = rak; }
    
    public int getJumlahTotal() { return jumlahTotal; }
    public void setJumlahTotal(int jumlahTotal) { this.jumlahTotal = jumlahTotal; }
    
    public int getJumlahTersedia() { return jumlahTersedia; }
    public void setJumlahTersedia(int jumlahTersedia) { this.jumlahTersedia = jumlahTersedia; }
    
    public BigDecimal getHarga() { return harga; }
    public void setHarga(BigDecimal harga) { this.harga = harga; }
    
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isAvailable() {
        return jumlahTersedia > 0;
    }
    
    public int getJumlahDipinjam() {
        return jumlahTotal - jumlahTersedia;
    }
    
    @Override
    public String toString() {
        return kodeBuku + " - " + judul;
    }
}