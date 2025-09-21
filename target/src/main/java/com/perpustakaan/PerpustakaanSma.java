package com.perpustakaan;

import com.perpustakaan.config.DatabaseConfig;

public class PerpustakaanSma {
    public static void main(String[] args) {
        DatabaseConfig db = DatabaseConfig.getInstance();

        // Test koneksi
        if (db.testConnection()) {
            System.out.println("✅ Koneksi ke database berhasil!");
            System.out.println(db.getDatabaseInfo());
        } else {
            System.out.println("❌ Koneksi ke database gagal!");
        }

        // Jangan lupa tutup pool ketika aplikasi berhenti
        db.closeDataSource();
    }
}
