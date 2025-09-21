package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.User;
import com.perpustakaan.model.Role;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class untuk User
 */
public class UserDAO {
    private final DatabaseConfig dbConfig;
    private final RoleDAO roleDAO;
    
    public UserDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.roleDAO = new RoleDAO();
    }
    
    public User authenticate(String loginId, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE (username = ? OR nis = ? OR nip = ?) AND password = MD5(?) AND is_active = 1";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, loginId);
            stmt.setString(2, loginId);
            stmt.setString(3, loginId);
            stmt.setString(4, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    public User save(User user) throws SQLException {
        if (user.getIdUser() == 0) {
            return insert(user);
        } else {
            return update(user);
        }
    }
    
    private User insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, nis, nip, password, id_role, is_active) VALUES (?, ?, ?, MD5(?), ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getNis());
            stmt.setString(3, user.getNip());
            stmt.setString(4, user.getPassword());
            stmt.setInt(5, user.getRole().getIdRole());
            stmt.setBoolean(6, user.isActive());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setIdUser(generatedKeys.getInt(1));
                        return user;
                    }
                }
            }
        }
        throw new SQLException("Creating user failed, no ID obtained.");
    }
    
    private User update(User user) throws SQLException {
        // Check if password should be updated
        boolean updatePassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();
        
        String sql;
        if (updatePassword) {
            sql = "UPDATE users SET username = ?, nis = ?, nip = ?, password = MD5(?), id_role = ?, is_active = ? WHERE id_user = ?";
        } else {
            sql = "UPDATE users SET username = ?, nis = ?, nip = ?, id_role = ?, is_active = ? WHERE id_user = ?";
        }
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getNis());
            stmt.setString(3, user.getNip());
            
            if (updatePassword) {
                stmt.setString(4, user.getPassword());
                stmt.setInt(5, user.getRole().getIdRole());
                stmt.setBoolean(6, user.isActive());
                stmt.setInt(7, user.getIdUser());
            } else {
                stmt.setInt(4, user.getRole().getIdRole());
                stmt.setBoolean(5, user.isActive());
                stmt.setInt(6, user.getIdUser());
            }
            
            stmt.executeUpdate();
            return user;
        }
    }
    
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = MD5(?) WHERE id_user = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    public void delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id_user = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    public List<User> findByRole(String roleName) throws SQLException {
        String sql = "SELECT u.* FROM users u JOIN role r ON u.id_role = r.id_role WHERE r.nama_role = ?";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roleName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }
    
    public boolean isUsernameExists(String username, int excludeUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND id_user != ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setInt(2, excludeUserId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setIdUser(rs.getInt("id_user"));
        user.setUsername(rs.getString("username"));
        user.setNis(rs.getString("nis"));
        user.setNip(rs.getString("nip"));
        user.setActive(rs.getBoolean("is_active"));
        
        // Get role
        Role role = roleDAO.findById(rs.getInt("id_role"));
        user.setRole(role);
        
        // Convert timestamps
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            user.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            user.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return user;
    }
}