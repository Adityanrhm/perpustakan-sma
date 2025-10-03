package com.perpustakaan.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.view.LoginFrame;
import com.perpustakaan.view.SplashScreen;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Application dengan SplashScreen
 */
public class MainApplication {
    
    public static void main(String[] args) {
        // Set Look and Feel first (before any GUI)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf Look and Feel");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set system Look and Feel: " + ex.getMessage());
            }
        }
        
        // Show splash screen and initialize application
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            
            // Option 1: With real database initialization
            splash.showSplashWithRealInit(dbConfig, () -> {
                // This runs after successful initialization
                new LoginFrame().setVisible(true);
            });
            
            /* Option 2: Simulated initialization (for testing UI)
            splash.showSplashAndInitialize(() -> {
                // Test database connection after splash
                try {
                    if (!dbConfig.testConnection()) {
                        showDatabaseError();
                        System.exit(1);
                    }
                    new LoginFrame().setVisible(true);
                } catch (Exception e) {
                    showApplicationError(e);
                    System.exit(1);
                }
            });
            */
        });
    }
    
    private static void showDatabaseError() {
        JOptionPane.showMessageDialog(null,
            "Tidak dapat terhubung ke database!\n" +
            "Pastikan MySQL server berjalan dan konfigurasi database sudah benar.\n\n" +
            "Periksa konfigurasi di DatabaseConfig.java:\n" +
            "- Host: localhost\n" +
            "- Port: 3306\n" +
            "- Database: perpustakaan_sma\n" +
            "- Username: root\n" +
            "- Password: (kosong)",
            "Database Connection Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private static void showApplicationError(Exception e) {
        JOptionPane.showMessageDialog(null,
            "Error saat menjalankan aplikasi:\n" + e.getMessage(),
            "Application Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}