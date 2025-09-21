package com.perpustakaan.model;

/**
 * Model class untuk Kategori Buku
 */
public class KategoriBuku {
    private int idKategori;
    private String namaKategori;
    private String deskripsi;
    
    public KategoriBuku() {}
    
    public KategoriBuku(String namaKategori, String deskripsi) {
        this.namaKategori = namaKategori;
        this.deskripsi = deskripsi;
    }
    
    // Getters and Setters
    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int idKategori) { this.idKategori = idKategori; }
    
    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }
    
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    
    @Override
    public String toString() {
        return namaKategori;
    }
}