package com.perpustakaan.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.perpustakaan.config.DatabaseConfig;
import com.perpustakaan.view.LoginFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class MainApplication {
    
    public static void main(String[] args) {
        // Set Look and Feel
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
        
        // Test database connection
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConfig dbConfig = DatabaseConfig.getInstance();
                if (!dbConfig.testConnection()) {
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
                    System.exit(1);
                }
                
                // Show login frame
                new LoginFrame().setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Error saat menjalankan aplikasi:\n" + e.getMessage(),
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}