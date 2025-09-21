package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.Kelas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class untuk Kelas
 */
public class KelasDAO {
    private final DatabaseConfig dbConfig;
    
    public KelasDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    public List<Kelas> findAll() throws SQLException {
        List<Kelas> kelasList = new ArrayList<>();
        String sql = "SELECT * FROM kelas ORDER BY tingkat, nama_kelas";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Kelas kelas = mapResultSetToKelas(rs);
                kelasList.add(kelas);
            }
        }
        return kelasList;
    }
    
    public Kelas findById(int idKelas) throws SQLException {
        String sql = "SELECT * FROM kelas WHERE id_kelas = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idKelas);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKelas(rs);
                }
            }
        }
        return null;
    }
    
    public List<Kelas> findByTingkat(int tingkat) throws SQLException {
        List<Kelas> kelasList = new ArrayList<>();
        String sql = "SELECT * FROM kelas WHERE tingkat = ? ORDER BY nama_kelas";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tingkat);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    kelasList.add(mapResultSetToKelas(rs));
                }
            }
        }
        return kelasList;
    }
    
    public Kelas save(Kelas kelas) throws SQLException {
        if (kelas.getIdKelas() == 0) {
            return insert(kelas);
        } else {
            return update(kelas);
        }
    }
    
    public Kelas findByTingkatJurusanRombel(String tingkat, String jurusan, int rombel) throws SQLException {
    String sql = "SELECT * FROM kelas WHERE tingkat = ? AND jurusan = ? AND rombel = ?";

    try (Connection conn = dbConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, tingkat);
        stmt.setString(2, jurusan);
        stmt.setInt(3, rombel);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToKelas(rs);
            }
        }
    }
    return null; // kalau tidak ada hasil
}

    
private Kelas insert(Kelas kelas) throws SQLException {
    String sql = "INSERT INTO kelas (tingkat, jurusan, rombel) VALUES (?, ?, ?)";

    try (Connection conn = dbConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, kelas.getTingkat());
        stmt.setString(2, kelas.getJurusan());
        stmt.setInt(3, kelas.getRombel());

        int affectedRows = stmt.executeUpdate();
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    kelas.setIdKelas(generatedKeys.getInt(1));
                    return kelas;
                }
            }
        }
    }
    throw new SQLException("Creating kelas failed, no ID obtained.");
}
   
private Kelas update(Kelas kelas) throws SQLException {
    String sql = "UPDATE kelas SET tingkat = ?, jurusan = ?, rombel = ? WHERE id_kelas = ?";

    try (Connection conn = dbConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, kelas.getTingkat());
        stmt.setString(2, kelas.getJurusan());
        stmt.setInt(3, kelas.getRombel());
        stmt.setInt(4, kelas.getIdKelas());

        stmt.executeUpdate();
        return kelas;
    }
}
    
private Kelas mapResultSetToKelas(ResultSet rs) throws SQLException {
    Kelas kelas = new Kelas();
    kelas.setIdKelas(rs.getInt("id_kelas"));
    kelas.setTingkat(rs.getString("tingkat"));
    kelas.setJurusan(rs.getString("jurusan"));
    kelas.setRombel(rs.getInt("rombel"));
    return kelas;
}
}