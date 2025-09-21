package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class untuk Pustakawan
 */
public class PustakawanDAO {
    private final DatabaseConfig dbConfig;
    private final UserDAO userDAO;
    
    public PustakawanDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.userDAO = new UserDAO();
    }
    
    public List<Pustakawan> findAll() throws SQLException {
        List<Pustakawan> pustakawanList = new ArrayList<>();
        String sql = "SELECT * FROM pustakawan ORDER BY nama_lengkap";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Pustakawan pustakawan = mapResultSetToPustakawan(rs);
                pustakawanList.add(pustakawan);
            }
        }
        return pustakawanList;
    }
    
    public Pustakawan findById(int idPustakawan) throws SQLException {
        String sql = "SELECT * FROM pustakawan WHERE id_pustakawan = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPustakawan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPustakawan(rs);
                }
            }
        }
        return null;
    }
    
    public Pustakawan findByNip(String nip) throws SQLException {
        String sql = "SELECT * FROM pustakawan WHERE nip = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nip);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPustakawan(rs);
                }
            }
        }
        return null;
    }
    
    public Pustakawan findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM pustakawan WHERE id_user = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPustakawan(rs);
                }
            }
        }
        return null;
    }
    
    public Pustakawan save(Pustakawan pustakawan) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            if (pustakawan.getIdPustakawan() == 0) {
                pustakawan = insert(pustakawan, conn);
            } else {
                pustakawan = update(pustakawan, conn);
            }
            
            conn.commit();
            return pustakawan;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private Pustakawan insert(Pustakawan pustakawan, Connection conn) throws SQLException {
        // Insert user first
        User user = pustakawan.getUser();
        user = userDAO.save(user);
        pustakawan.setUser(user);
        
        // REVISED - hapus tanggal_mulai_kerja
        String sql = "INSERT INTO pustakawan (id_user, nip, nama_lengkap, email, no_telepon, alamat) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, user.getIdUser());
            stmt.setString(2, pustakawan.getNip());
            stmt.setString(3, pustakawan.getNamaLengkap());
            stmt.setString(4, pustakawan.getEmail());
            stmt.setString(5, pustakawan.getNoTelepon());
            stmt.setString(6, pustakawan.getAlamat());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pustakawan.setIdPustakawan(generatedKeys.getInt(1));
                        return pustakawan;
                    }
                }
            }
        }
        throw new SQLException("Creating pustakawan failed, no ID obtained.");
    }
    
    private Pustakawan update(Pustakawan pustakawan, Connection conn) throws SQLException {
        // Update user first
        userDAO.save(pustakawan.getUser());
        
        // REVISED - hapus tanggal_mulai_kerja
        String sql = "UPDATE pustakawan SET nip = ?, nama_lengkap = ?, email = ?, no_telepon = ?, " +
                    "alamat = ? WHERE id_pustakawan = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pustakawan.getNip());
            stmt.setString(2, pustakawan.getNamaLengkap());
            stmt.setString(3, pustakawan.getEmail());
            stmt.setString(4, pustakawan.getNoTelepon());
            stmt.setString(5, pustakawan.getAlamat());
            stmt.setInt(6, pustakawan.getIdPustakawan());
            
            stmt.executeUpdate();
            return pustakawan;
        }
    }
    
    public void delete(int idPustakawan) throws SQLException {
    Connection conn = null;
    try {
        conn = dbConfig.getConnection();
        conn.setAutoCommit(false);

        // Get user id first
        Pustakawan pustakawan = findById(idPustakawan);
        if (pustakawan != null && pustakawan.getUser() != null) {
            int userId = pustakawan.getUser().getIdUser();

            // Delete user record first
            userDAO.delete(userId);

            // Delete siswa record after
            String deleteSiswaSQL = "DELETE FROM pustakawan WHERE id_pustakawan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSiswaSQL)) {
                stmt.setInt(1, idPustakawan);
                stmt.executeUpdate();
            }
        }

        conn.commit();

    } catch (SQLException e) {
        if (conn != null) conn.rollback();
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}

    
    public boolean isNipExists(String nip, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pustakawan WHERE nip = ? AND id_pustakawan != ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nip);
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
   private Pustakawan mapResultSetToPustakawan(ResultSet rs) throws SQLException {
    Pustakawan pustakawan = new Pustakawan();
    pustakawan.setIdPustakawan(rs.getInt("id_pustakawan"));
    pustakawan.setNip(rs.getString("nip"));
    pustakawan.setNamaLengkap(rs.getString("nama_lengkap"));
    pustakawan.setEmail(rs.getString("email"));
    pustakawan.setNoTelepon(rs.getString("no_telepon"));
    pustakawan.setAlamat(rs.getString("alamat"));

    // REMOVE tanggal_mulai_kerja (tidak digunakan)

    // Load user data (jika ada kolom id_user)
    try {
        User user = userDAO.findById(rs.getInt("id_user"));
        pustakawan.setUser(user);
    } catch (SQLException e) {
        System.err.println("Error loading user for pustakawan: " + e.getMessage());
    }

    return pustakawan;
}

}