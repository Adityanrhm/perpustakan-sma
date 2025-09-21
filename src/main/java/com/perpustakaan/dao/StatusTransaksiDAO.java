package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.StatusTransaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatusTransaksiDAO {
    private final DatabaseConfig dbConfig;
    
    public StatusTransaksiDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    public List<StatusTransaksi> findAll() throws SQLException {
        List<StatusTransaksi> statusList = new ArrayList<>();
        String sql = "SELECT * FROM status_transaksi ORDER BY nama_status";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                statusList.add(mapResultSetToStatus(rs));
            }
        }
        return statusList;
    }
    
    public StatusTransaksi findById(int idStatus) throws SQLException {
        String sql = "SELECT * FROM status_transaksi WHERE id_status = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idStatus);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStatus(rs);
                }
            }
        }
        return null;
    }
    
    public StatusTransaksi findByName(String namaStatus) throws SQLException {
        String sql = "SELECT * FROM status_transaksi WHERE nama_status = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, namaStatus);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStatus(rs);
                }
            }
        }
        return null;
    }
    
    private StatusTransaksi mapResultSetToStatus(ResultSet rs) throws SQLException {
        StatusTransaksi status = new StatusTransaksi();
        status.setIdStatus(rs.getInt("id_status"));
        status.setNamaStatus(rs.getString("nama_status"));
        status.setDeskripsi(rs.getString("deskripsi"));
        return status;
    }
}