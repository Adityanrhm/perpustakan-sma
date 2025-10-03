package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class untuk Admin
 */
public class AdminDAO {
    private final DatabaseConfig dbConfig;
    private final UserDAO userDAO;
    
    public AdminDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.userDAO = new UserDAO();
    }
    
    public List<Admin> findAll() throws SQLException {
        List<Admin> adminList = new ArrayList<>();
        String sql = "SELECT * FROM admin ORDER BY nama_lengkap";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Admin admin = mapResultSetToAdmin(rs);
                adminList.add(admin);
            }
        }
        return adminList;
    }
    
    public Admin findById(int idAdmin) throws SQLException {
        String sql = "SELECT * FROM admin WHERE id_admin = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAdmin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        }
        return null;
    }
    
    public Admin findByNip(String nip) throws SQLException {
        String sql = "SELECT * FROM admin WHERE nip = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nip);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        }
        return null;
    }
    
    public Admin findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM admin WHERE id_user = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        }
        return null;
    }
    
    public Admin save(Admin admin) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            if (admin.getIdAdmin() == 0) {
                admin = insert(admin, conn);
            } else {
                admin = update(admin, conn);
            }
            
            conn.commit();
            return admin;
            
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
    
    private Admin insert(Admin admin, Connection conn) throws SQLException {
        // Insert user first
        User user = admin.getUser();
        user = userDAO.save(user);
        admin.setUser(user);
        
        String sql = "INSERT INTO admin (id_user, nip, nama_lengkap, email, no_telepon, alamat) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, user.getIdUser());
            stmt.setString(2, admin.getNip());
            stmt.setString(3, admin.getNamaLengkap());
            stmt.setString(4, admin.getEmail());
            stmt.setString(5, admin.getNoTelepon());
            stmt.setString(6, admin.getAlamat());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        admin.setIdAdmin(generatedKeys.getInt(1));
                        return admin;
                    }
                }
            }
        }
        throw new SQLException("Creating admin failed, no ID obtained.");
    }
    
    private Admin update(Admin admin, Connection conn) throws SQLException {
        // Update user first
        userDAO.save(admin.getUser());
        
        String sql = "UPDATE admin SET nip = ?, nama_lengkap = ?, email = ?, no_telepon = ?, " +
                    "alamat = ? WHERE id_admin = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admin.getNip());
            stmt.setString(2, admin.getNamaLengkap());
            stmt.setString(3, admin.getEmail());
            stmt.setString(4, admin.getNoTelepon());
            stmt.setString(5, admin.getAlamat());
            stmt.setInt(6, admin.getIdAdmin());
            
            stmt.executeUpdate();
            return admin;
        }
    }
    
    public void delete(int idAdmin) throws SQLException {
    Connection conn = null;
    try {
        conn = dbConfig.getConnection();
        conn.setAutoCommit(false);

        // Get user id first
        Admin admin = findById(idAdmin);
        if (admin != null && admin.getUser() != null) {
            int userId = admin.getUser().getIdUser();

            // Delete user record first
            userDAO.delete(userId);

            // Delete siswa record after
            String deleteSiswaSQL = "DELETE FROM admin WHERE id_admin = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSiswaSQL)) {
                stmt.setInt(1, idAdmin);
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

    
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setIdAdmin(rs.getInt("id_admin"));
        admin.setNip(rs.getString("nip"));
        admin.setNamaLengkap(rs.getString("nama_lengkap"));
        admin.setEmail(rs.getString("email"));
        admin.setNoTelepon(rs.getString("no_telepon"));
        admin.setAlamat(rs.getString("alamat"));
        
        // Load user data
        try {
            User user = userDAO.findById(rs.getInt("id_user"));
            admin.setUser(user);
        } catch (SQLException e) {
            System.err.println("Error loading user for admin: " + e.getMessage());
        }
        
        return admin;
    }
}