package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class untuk Siswa
 */
public class SiswaDAO {
    private final DatabaseConfig dbConfig;
    private final UserDAO userDAO;
    private final KelasDAO kelasDAO;
    
    public SiswaDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.userDAO = new UserDAO();
        this.kelasDAO = new KelasDAO();
    }
    
    public List<Siswa> findAll() throws SQLException {
        List<Siswa> siswaList = new ArrayList<>();
        String sql = "SELECT s.*, k.tingkat, k.jurusan, k.rombel FROM siswa s " +
                    "JOIN kelas k ON s.id_kelas = k.id_kelas " +
                    "ORDER BY s.nama_lengkap";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Siswa siswa = mapResultSetToSiswa(rs);
                siswaList.add(siswa);
            }
        }
        return siswaList;
    }
    
    public Siswa findById(int idSiswa) throws SQLException {
        String sql = "SELECT s.*, k.tingkat, k.jurusan, k.rombel FROM siswa s " +
                    "JOIN kelas k ON s.id_kelas = k.id_kelas " +
                    "WHERE s.id_siswa = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSiswa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSiswa(rs);
                }
            }
        }
        return null;
    }
    
    public Siswa findByNis(String nis) throws SQLException {
        String sql = "SELECT s.*, k.tingkat, k.jurusan, k.rombel FROM siswa s " +
                    "JOIN kelas k ON s.id_kelas = k.id_kelas " +
                    "WHERE s.nis = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nis);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSiswa(rs);
                }
            }
        }
        return null;
    }
    
    public Siswa findByUserId(int userId) throws SQLException {
        String sql = "SELECT s.*, k.tingkat, k.jurusan, k.rombel FROM siswa s " +
                    "JOIN kelas k ON s.id_kelas = k.id_kelas " +
                    "WHERE s.id_user = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSiswa(rs);
                }
            }
        }
        return null;
    }
    
    public List<Siswa> findByKelas(int idKelas) throws SQLException {
        List<Siswa> siswaList = new ArrayList<>();
        String sql = "SELECT s.*, k.nama_kelas, k.tingkat, k.jurusan FROM siswa s " +
                    "JOIN kelas k ON s.id_kelas = k.id_kelas " +
                    "WHERE s.id_kelas = ? ORDER BY s.nama_lengkap";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idKelas);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    siswaList.add(mapResultSetToSiswa(rs));
                }
            }
        }
        return siswaList;
    }
    
    public Siswa save(Siswa siswa) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            if (siswa.getIdSiswa() == 0) {
                siswa = insert(siswa, conn);
            } else {
                siswa = update(siswa, conn);
            }
            
            conn.commit();
            return siswa;
            
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
    
    private Siswa insert(Siswa siswa, Connection conn) throws SQLException {
        // Insert user first
        User user = siswa.getUser();
        user = userDAO.save(user);
        siswa.setUser(user);
        
        // REVISED - hanya field yang diinput
        String sql = "INSERT INTO siswa (id_user, nis, nama_lengkap, id_kelas, jenis_kelamin, status_aktif) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, user.getIdUser());
            stmt.setString(2, siswa.getNis());
            stmt.setString(3, siswa.getNamaLengkap());
            stmt.setInt(4, siswa.getKelas().getIdKelas());
            stmt.setString(5, siswa.getJenisKelamin());
            stmt.setBoolean(6, siswa.isStatusAktif());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        siswa.setIdSiswa(generatedKeys.getInt(1));
                        return siswa;
                    }
                }
            }
        }
        throw new SQLException("Creating siswa failed, no ID obtained.");
    }
    
    private Siswa update(Siswa siswa, Connection conn) throws SQLException {
        // Update user first
        userDAO.save(siswa.getUser());
        
        // REVISED - hanya field yang diinput
        String sql = "UPDATE siswa SET nis = ?, nama_lengkap = ?, id_kelas = ?, jenis_kelamin = ?, " +
                    "status_aktif = ? WHERE id_siswa = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, siswa.getNis());
            stmt.setString(2, siswa.getNamaLengkap());
            stmt.setInt(3, siswa.getKelas().getIdKelas());
            stmt.setString(4, siswa.getJenisKelamin());
            stmt.setBoolean(5, siswa.isStatusAktif());
            stmt.setInt(6, siswa.getIdSiswa());
            
            stmt.executeUpdate();
            return siswa;
        }
    }
    
    public void delete(int idSiswa) throws SQLException {
    Connection conn = null;
    try {
        conn = dbConfig.getConnection();
        conn.setAutoCommit(false);

        // Get user id first
        Siswa siswa = findById(idSiswa);
        if (siswa != null && siswa.getUser() != null) {
            int userId = siswa.getUser().getIdUser();

            // Delete user record first
            userDAO.delete(userId);

            // Delete siswa record after
            String deleteSiswaSQL = "DELETE FROM siswa WHERE id_siswa = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSiswaSQL)) {
                stmt.setInt(1, idSiswa);
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

    
    public boolean isNisExists(String nis, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM siswa WHERE nis = ? AND id_siswa != ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nis);
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
   private Siswa mapResultSetToSiswa(ResultSet rs) throws SQLException {
    Siswa siswa = new Siswa();
    siswa.setIdSiswa(rs.getInt("id_siswa"));
    siswa.setNis(rs.getString("nis"));
    siswa.setNamaLengkap(rs.getString("nama_lengkap"));
    siswa.setJenisKelamin(rs.getString("jenis_kelamin"));
    siswa.setStatusAktif(rs.getBoolean("status_aktif"));

    // Set kelas dengan struktur baru
    Kelas kelas = new Kelas();
    kelas.setIdKelas(rs.getInt("id_kelas"));
    kelas.setTingkat(rs.getString("tingkat"));
    kelas.setJurusan(rs.getString("jurusan"));
    kelas.setRombel(rs.getInt("rombel"));
    siswa.setKelas(kelas);

    // Load user data
    try {
        User user = userDAO.findById(rs.getInt("id_user"));
        siswa.setUser(user);
    } catch (SQLException e) {
        System.err.println("Error loading user for siswa: " + e.getMessage());
    }

        return siswa;
    }
}