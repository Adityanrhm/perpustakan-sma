package com.perpustakaan.dao;

import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class untuk Buku
 */
public class BukuDAO {
    private final DatabaseConfig dbConfig;
    private final KategoriBukuDAO kategoriDAO;
    private final RakDAO rakDAO;
    
    public BukuDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.kategoriDAO = new KategoriBukuDAO();
        this.rakDAO = new RakDAO();
    }
    
    public List<Buku> findAll() throws SQLException {
        List<Buku> bukuList = new ArrayList<>();
        String sql = "SELECT b.*, k.nama_kategori, k.deskripsi as kategori_desc, " +
                    "r.kode_rak, r.nama_rak, r.lokasi FROM buku b " +
                    "JOIN kategori_buku k ON b.id_kategori = k.id_kategori " +
                    "JOIN rak r ON b.id_rak = r.id_rak " +
                    "ORDER BY b.judul";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bukuList.add(mapResultSetToBuku(rs));
            }
        }
        return bukuList;
    }
    
    public Buku findById(int idBuku) throws SQLException {
        String sql = "SELECT b.*, k.nama_kategori, k.deskripsi as kategori_desc, " +
                    "r.kode_rak, r.nama_rak, r.lokasi FROM buku b " +
                    "JOIN kategori_buku k ON b.id_kategori = k.id_kategori " +
                    "JOIN rak r ON b.id_rak = r.id_rak " +
                    "WHERE b.id_buku = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBuku);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBuku(rs);
                }
            }
        }
        return null;
    }
    
    public Buku findByKode(String kodeBuku) throws SQLException {
        String sql = "SELECT b.*, k.nama_kategori, k.deskripsi as kategori_desc, " +
                    "r.kode_rak, r.nama_rak, r.lokasi FROM buku b " +
                    "JOIN kategori_buku k ON b.id_kategori = k.id_kategori " +
                    "JOIN rak r ON b.id_rak = r.id_rak " +
                    "WHERE b.kode_buku = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kodeBuku);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBuku(rs);
                }
            }
        }
        return null;
    }
    
    public List<Buku> findAvailableBooks() throws SQLException {
        List<Buku> bukuList = new ArrayList<>();
        String sql = "SELECT b.*, k.nama_kategori, k.deskripsi as kategori_desc, " +
                    "r.kode_rak, r.nama_rak, r.lokasi FROM buku b " +
                    "JOIN kategori_buku k ON b.id_kategori = k.id_kategori " +
                    "JOIN rak r ON b.id_rak = r.id_rak " +
                    "WHERE b.jumlah_tersedia > 0 ORDER BY b.judul";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bukuList.add(mapResultSetToBuku(rs));
            }
        }
        return bukuList;
    }
    
    public List<Buku> searchBooks(String keyword) throws SQLException {
        List<Buku> bukuList = new ArrayList<>();
        String sql = "SELECT b.*, k.nama_kategori, k.deskripsi as kategori_desc, " +
                    "r.kode_rak, r.nama_rak, r.lokasi FROM buku b " +
                    "JOIN kategori_buku k ON b.id_kategori = k.id_kategori " +
                    "JOIN rak r ON b.id_rak = r.id_rak " +
                    "WHERE b.judul LIKE ? OR b.pengarang LIKE ? OR b.kode_buku LIKE ? " +
                    "ORDER BY b.judul";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + keyword + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bukuList.add(mapResultSetToBuku(rs));
                }
            }
        }
        return bukuList;
    }
    
    public Buku save(Buku buku) throws SQLException {
        if (buku.getIdBuku() == 0) {
            return insert(buku);
        } else {
            return update(buku);
        }
    }
    
    private Buku insert(Buku buku) throws SQLException {
        String sql = "INSERT INTO buku (kode_buku, isbn, judul, pengarang, penerbit, tahun_terbit, " +
                    "id_kategori, id_rak, jumlah_total, jumlah_tersedia, harga, deskripsi, cover_image) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, buku.getKodeBuku());
            stmt.setString(2, buku.getIsbn());
            stmt.setString(3, buku.getJudul());
            stmt.setString(4, buku.getPengarang());
            stmt.setString(5, buku.getPenerbit());
            stmt.setInt(6, buku.getTahunTerbit());
            stmt.setInt(7, buku.getKategori().getIdKategori());
            stmt.setInt(8, buku.getRak().getIdRak());
            stmt.setInt(9, buku.getJumlahTotal());
            stmt.setInt(10, buku.getJumlahTersedia());
            stmt.setBigDecimal(11, buku.getHarga());
            stmt.setString(12, buku.getDeskripsi());
            stmt.setString(13, buku.getCoverImage());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        buku.setIdBuku(generatedKeys.getInt(1));
                        return buku;
                    }
                }
            }
        }
        throw new SQLException("Creating buku failed, no ID obtained.");
    }
    
    private Buku update(Buku buku) throws SQLException {
        String sql = "UPDATE buku SET kode_buku = ?, isbn = ?, judul = ?, pengarang = ?, " +
                    "penerbit = ?, tahun_terbit = ?, id_kategori = ?, id_rak = ?, " +
                    "jumlah_total = ?, jumlah_tersedia = ?, harga = ?, deskripsi = ?, " +
                    "cover_image = ? WHERE id_buku = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, buku.getKodeBuku());
            stmt.setString(2, buku.getIsbn());
            stmt.setString(3, buku.getJudul());
            stmt.setString(4, buku.getPengarang());
            stmt.setString(5, buku.getPenerbit());
            stmt.setInt(6, buku.getTahunTerbit());
            stmt.setInt(7, buku.getKategori().getIdKategori());
            stmt.setInt(8, buku.getRak().getIdRak());
            stmt.setInt(9, buku.getJumlahTotal());
            stmt.setInt(10, buku.getJumlahTersedia());
            stmt.setBigDecimal(11, buku.getHarga());
            stmt.setString(12, buku.getDeskripsi());
            stmt.setString(13, buku.getCoverImage());
            stmt.setInt(14, buku.getIdBuku());
            
            stmt.executeUpdate();
            return buku;
        }
    }
    
    public void delete(int idBuku) throws SQLException {
        String sql = "DELETE FROM buku WHERE id_buku = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBuku);
            stmt.executeUpdate();
        }
    }
    
    public boolean isKodeBukuExists(String kodeBuku, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM buku WHERE kode_buku = ? AND id_buku != ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kodeBuku);
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public void updateJumlahTersedia(int idBuku, int jumlahTersedia) throws SQLException {
        String sql = "UPDATE buku SET jumlah_tersedia = ? WHERE id_buku = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, jumlahTersedia);
            stmt.setInt(2, idBuku);
            stmt.executeUpdate();
        }
    }
    
    private Buku mapResultSetToBuku(ResultSet rs) throws SQLException {
        Buku buku = new Buku();
        buku.setIdBuku(rs.getInt("id_buku"));
        buku.setKodeBuku(rs.getString("kode_buku"));
        buku.setIsbn(rs.getString("isbn"));
        buku.setJudul(rs.getString("judul"));
        buku.setPengarang(rs.getString("pengarang"));
        buku.setPenerbit(rs.getString("penerbit"));
        buku.setTahunTerbit(rs.getInt("tahun_terbit"));
        buku.setJumlahTotal(rs.getInt("jumlah_total"));
        buku.setJumlahTersedia(rs.getInt("jumlah_tersedia"));
        buku.setHarga(rs.getBigDecimal("harga"));
        buku.setDeskripsi(rs.getString("deskripsi"));
        buku.setCoverImage(rs.getString("cover_image"));
        
        // Set kategori
        KategoriBuku kategori = new KategoriBuku();
        kategori.setIdKategori(rs.getInt("id_kategori"));
        kategori.setNamaKategori(rs.getString("nama_kategori"));
        kategori.setDeskripsi(rs.getString("kategori_desc"));
        buku.setKategori(kategori);
        
        // Set rak
        Rak rak = new Rak();
        rak.setIdRak(rs.getInt("id_rak"));
        rak.setKodeRak(rs.getString("kode_rak"));
        rak.setNamaRak(rs.getString("nama_rak"));
        rak.setLokasi(rs.getString("lokasi"));
        buku.setRak(rak);
        
        // Set timestamps
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            buku.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            buku.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return buku;
    }
}