package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class untuk Role
 */
public class RoleDAO {
    private final DatabaseConfig dbConfig;
    
    public RoleDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    public List<Role> findAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM role ORDER BY nama_role";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Role role = new Role();
                role.setIdRole(rs.getInt("id_role"));
                role.setNamaRole(rs.getString("nama_role"));
                role.setDeskripsi(rs.getString("deskripsi"));
                roles.add(role);
            }
        }
        return roles;
    }
    
    public Role findById(int idRole) throws SQLException {
        String sql = "SELECT * FROM role WHERE id_role = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idRole);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setIdRole(rs.getInt("id_role"));
                    role.setNamaRole(rs.getString("nama_role"));
                    role.setDeskripsi(rs.getString("deskripsi"));
                    return role;
                }
            }
        }
        return null;
    }
    
    public Role findByName(String namaRole) throws SQLException {
        String sql = "SELECT * FROM role WHERE nama_role = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, namaRole);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setIdRole(rs.getInt("id_role"));
                    role.setNamaRole(rs.getString("nama_role"));
                    role.setDeskripsi(rs.getString("deskripsi"));
                    return role;
                }
            }
        }
        return null;
    }
}