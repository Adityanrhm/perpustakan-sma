package com.perpustakaan.model;

/**
 * Model class untuk Kelas - REVISED
 */
public class Kelas {
    private int idKelas;
    private String tingkat; // X, XI, XII
    private String jurusan; // IPA, IPS, BAHASA
    private int rombel; // 1, 2, 3, dst
    
    public Kelas() {}
    
    public Kelas(String tingkat, String jurusan, int rombel) {
        this.tingkat = tingkat;
        this.jurusan = jurusan;
        this.rombel = rombel;
    }
    
    // Getters and Setters
    public int getIdKelas() { return idKelas; }
    public void setIdKelas(int idKelas) { this.idKelas = idKelas; }
    
    public String getTingkat() { return tingkat; }
    public void setTingkat(String tingkat) { this.tingkat = tingkat; }
    
    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }
    
    public int getRombel() { return rombel; }
    public void setRombel(int rombel) { this.rombel = rombel; }
    
    // Helper method untuk mendapatkan nama kelas lengkap
    public String getNamaKelas() {
        return tingkat + "-" + jurusan + "-" + rombel;
    }
    
    @Override
    public String toString() {
        return getNamaKelas();
    }
}
