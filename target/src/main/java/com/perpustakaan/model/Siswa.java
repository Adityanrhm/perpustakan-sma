package com.perpustakaan.model;

import java.time.LocalDate;

/**
 * Model class untuk Siswa - REVISED
 */
public class Siswa {
    private int idSiswa;
    private User user;
    private String nis;
    private String namaLengkap;
    private Kelas kelas;
    private String jenisKelamin;
    private boolean statusAktif;
    // REMOVE: tempatLahir, tanggalLahir, alamat, noTelepon, namaWali, noTeleponWali, foto
    
    public Siswa() {}
    
    public Siswa(String nis, String namaLengkap, Kelas kelas, String jenisKelamin) {
        this.nis = nis;
        this.namaLengkap = namaLengkap;
        this.kelas = kelas;
        this.jenisKelamin = jenisKelamin;
        this.statusAktif = true;
    }
    
    // Getters and Setters
    public int getIdSiswa() { return idSiswa; }
    public void setIdSiswa(int idSiswa) { this.idSiswa = idSiswa; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getNis() { return nis; }
    public void setNis(String nis) { this.nis = nis; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    public Kelas getKelas() { return kelas; }
    public void setKelas(Kelas kelas) { this.kelas = kelas; }
    
    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }
    
    public boolean isStatusAktif() { return statusAktif; }
    public void setStatusAktif(boolean statusAktif) { this.statusAktif = statusAktif; }
    
    // REMOVE all unused getter/setter methods
    
    @Override
    public String toString() {
        return nis + " - " + namaLengkap;
    }
}