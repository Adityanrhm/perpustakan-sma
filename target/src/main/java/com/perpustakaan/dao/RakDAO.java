package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RakDAO {
    private final DatabaseConfig dbConfig;
    
    public RakDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    public List<Rak> findAll() throws SQLException {
        List<Rak> rakList = new ArrayList<>();
        String sql = "SELECT * FROM rak ORDER BY kode_rak";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                rakList.add(mapResultSetToRak(rs));
            }
        }
        return rakList;
    }
    
    public Rak findById(int idRak) throws SQLException {
        String sql = "SELECT * FROM rak WHERE id_rak = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idRak);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRak(rs);
                }
            }
        }
        return null;
    }
    
    public Rak findByKode(String kodeRak) throws SQLException {
        String sql = "SELECT * FROM rak WHERE kode_rak = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kodeRak);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRak(rs);
                }
            }
        }
        return null;
    }
    
    private Rak mapResultSetToRak(ResultSet rs) throws SQLException {
        Rak rak = new Rak();
        rak.setIdRak(rs.getInt("id_rak"));
        rak.setKodeRak(rs.getString("kode_rak"));
        rak.setNamaRak(rs.getString("nama_rak"));
        rak.setLokasi(rs.getString("lokasi"));
        rak.setKapasitas(rs.getInt("kapasitas"));
        rak.setDeskripsi(rs.getString("deskripsi"));
        return rak;
    }
}