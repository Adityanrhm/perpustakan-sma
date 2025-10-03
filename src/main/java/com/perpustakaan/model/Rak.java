package com.perpustakaan.model;

/**
 * Model class untuk Rak
 */
public class Rak {
    private int idRak;
    private String kodeRak;
    private String namaRak;
    private String lokasi;
    private int kapasitas;
    private String deskripsi;
    
    public Rak() {}
    
    public Rak(String kodeRak, String namaRak, String lokasi, int kapasitas) {
        this.kodeRak = kodeRak;
        this.namaRak = namaRak;
        this.lokasi = lokasi;
        this.kapasitas = kapasitas;
    }
    
    // Getters and Setters
    public int getIdRak() { return idRak; }
    public void setIdRak(int idRak) { this.idRak = idRak; }
    
    public String getKodeRak() { return kodeRak; }
    public void setKodeRak(String kodeRak) { this.kodeRak = kodeRak; }
    
    public String getNamaRak() { return namaRak; }
    public void setNamaRak(String namaRak) { this.namaRak = namaRak; }
    
    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    
    public int getKapasitas() { return kapasitas; }
    public void setKapasitas(int kapasitas) { this.kapasitas = kapasitas; }
    
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    
    @Override
    public String toString() {
        return kodeRak + " - " + namaRak;
    }
}