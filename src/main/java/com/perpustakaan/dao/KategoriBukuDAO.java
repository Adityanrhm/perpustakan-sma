package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.KategoriBuku;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class KategoriBukuDAO {
    private final DatabaseConfig dbConfig;
    
    public KategoriBukuDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    public List<KategoriBuku> findAll() throws SQLException {
        List<KategoriBuku> kategoriList = new ArrayList<>();
        String sql = "SELECT * FROM kategori_buku ORDER BY nama_kategori";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                kategoriList.add(mapResultSetToKategori(rs));
            }
        }
        return kategoriList;
    }
    
    public KategoriBuku findById(int idKategori) throws SQLException {
        String sql = "SELECT * FROM kategori_buku WHERE id_kategori = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idKategori);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKategori(rs);
                }
            }
        }
        return null;
    }
    
    private KategoriBuku mapResultSetToKategori(ResultSet rs) throws SQLException {
        KategoriBuku kategori = new KategoriBuku();
        kategori.setIdKategori(rs.getInt("id_kategori"));
        kategori.setNamaKategori(rs.getString("nama_kategori"));
        kategori.setDeskripsi(rs.getString("deskripsi"));
        return kategori;
    }
}