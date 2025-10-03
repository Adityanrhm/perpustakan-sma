package com.perpustakaan.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;


public class DatabaseConfig {
    private static DatabaseConfig instance;
    private HikariDataSource dataSource;
    
    // Konfigurasi database - sesuaikan dengan environment Anda
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "perpustakaan_sma";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    
    public DatabaseConfig() {
        initializeDataSource();
    }
    
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    private void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        
        // Database connection properties
        config.setJdbcUrl("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME 
                         + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        config.setUsername(DB_USERNAME);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Connection pool properties
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000); // 5 minutes
        config.setConnectionTimeout(20000); // 20 seconds
        config.setMaxLifetime(1200000); // 20 minutes
        
        // Performance and reliability properties
        config.setLeakDetectionThreshold(60000); // 1 minute
        config.setPoolName("PerpustakaanCP");
        
        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        try {
            this.dataSource = new HikariDataSource(config);
            System.out.println("Database connection pool initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection pool: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed");
        }
    }
    
    // Test koneksi database
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    // Method untuk mendapatkan informasi database
    public String getDatabaseInfo() {
        try (Connection conn = getConnection()) {
            return "Database: " + conn.getMetaData().getDatabaseProductName() + " " 
                   + conn.getMetaData().getDatabaseProductVersion();
        } catch (SQLException e) {
            return "Unable to retrieve database info: " + e.getMessage();
        }
    }
}