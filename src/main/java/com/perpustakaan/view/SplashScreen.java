package com.perpustakaan.view;

import com.perpustakaan.config.DatabaseConfig;

import javax.swing.*;
import java.awt.*;

/**
 * Modern Splash Screen dengan loading yang fungsional
 */
public class SplashScreen extends JWindow {
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JLabel lblVersion;
    private int progress = 0;
    
    // Colors - tema coklat
    private static final Color PRIMARY_BROWN = new Color(92, 64, 51);
    private static final Color ACCENT_CREAM = new Color(245, 238, 220);
    private static final Color TEXT_DARK = new Color(62, 44, 36);
    
    public SplashScreen() {
        initComponents();
        setupLayout();
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(PRIMARY_BROWN);
        progressBar.setPreferredSize(new Dimension(500, 8));
        progressBar.setBorderPainted(false);
        
        // Status label
        lblStatus = new JLabel("Memulai aplikasi...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(TEXT_DARK);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Version label
        lblVersion = new JLabel("Version 1.0.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(new Color(127, 140, 141));
        lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, ACCENT_CREAM,
                    0, getHeight(), Color.WHITE
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(PRIMARY_BROWN, 2));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 20, 10, 20);
        
        // Logo
        JPanel logoPanel = createLogoGraphics();
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 20, 20, 20);
        mainPanel.add(logoPanel, gbc);
        
        // Title
        JLabel lblTitle1 = new JLabel("PERPUSTAKAAN");
        lblTitle1.setFont(new Font("Impact", Font.BOLD, 32));
        lblTitle1.setForeground(PRIMARY_BROWN);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 0, 20);
        mainPanel.add(lblTitle1, gbc);
        
        JLabel lblTitle2 = new JLabel("SMA HAEIN");
        lblTitle2.setFont(new Font("Impact", Font.BOLD, 32));
        lblTitle2.setForeground(PRIMARY_BROWN);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 20, 20);
        mainPanel.add(lblTitle2, gbc);
        
        // Progress bar
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 50, 10, 50);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(progressBar, gbc);
        
        // Status label
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 20, 15, 20);
        mainPanel.add(lblStatus, gbc);
        
        // Version
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 20, 20, 20);
        mainPanel.add(lblVersion, gbc);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createLogoGraphics() {
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int centerX = width / 2;
                
                // Book base
                g2d.setColor(PRIMARY_BROWN);
                int bookWidth = 100;
                int bookHeight = 65;
                int bookX = centerX - bookWidth / 2;
                int bookY = height - bookHeight - 15;
                
                // Open book
                g2d.fillRoundRect(bookX, bookY, bookWidth / 2 - 3, bookHeight, 8, 8);
                g2d.fillRoundRect(bookX + bookWidth / 2 + 3, bookY, bookWidth / 2 - 3, bookHeight, 8, 8);
                
                // Pages
                g2d.setColor(ACCENT_CREAM);
                for (int i = 0; i < 2; i++) {
                    g2d.fillRect(bookX + 8 + i * 2, bookY + 8, bookWidth / 2 - 18, bookHeight - 16);
                    g2d.fillRect(bookX + bookWidth / 2 + 10 + i * 2, bookY + 8, bookWidth / 2 - 18, bookHeight - 16);
                }
                
                // Building
                g2d.setColor(PRIMARY_BROWN);
                int buildingWidth = 90;
                int buildingHeight = 60;
                int buildingX = centerX - buildingWidth / 2;
                int buildingY = bookY - buildingHeight - 8;
                
                // Roof
                int[] xPoints = {buildingX, centerX, buildingX + buildingWidth};
                int[] yPoints = {buildingY + 15, buildingY - 5, buildingY + 15};
                g2d.fillPolygon(xPoints, yPoints, 3);
                
                // Columns
                int columnWidth = 12;
                int columnHeight = 40;
                int columnY = buildingY + 15;
                
                for (int i = 0; i < 3; i++) {
                    int columnX = buildingX + 10 + i * 30;
                    g2d.fillRect(columnX, columnY, columnWidth, columnHeight);
                }
            }
        };
        
        logoPanel.setPreferredSize(new Dimension(150, 140));
        logoPanel.setOpaque(false);
        return logoPanel;
    }
    
    private void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> lblStatus.setText(status));
    }
    
    private void updateProgress(int value) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            progressBar.setString(value + "%");
        });
    }
    
    /**
     * Show splash with real initialization
     */
    public void showSplashWithRealInit(DatabaseConfig dbConfig, Runnable onComplete) {
        setVisible(true);
        
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Step 1: Initialize database
                publish(10);
                updateStatus("Menginisialisasi database...");
                Thread.sleep(500);
                
                if (!dbConfig.testConnection()) {
                    throw new Exception("Koneksi database gagal!");
                }
                
                // Step 2: Loading configuration
                publish(30);
                updateStatus("Memuat konfigurasi...");
                Thread.sleep(400);
                
                // Step 3: Loading resources
                publish(50);
                updateStatus("Memuat resources...");
                Thread.sleep(400);
                
                // Step 4: Initializing components
                publish(70);
                updateStatus("Menginisialisasi komponen...");
                Thread.sleep(400);
                
                // Step 5: Preparing UI
                publish(85);
                updateStatus("Menyiapkan interface...");
                Thread.sleep(400);
                
                // Step 6: Complete
                publish(100);
                updateStatus("Selesai!");
                Thread.sleep(300);
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                for (Integer value : chunks) {
                    updateProgress(value);
                }
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    
                    // Close splash and run callback
                    SwingUtilities.invokeLater(() -> {
                        setVisible(false);
                        dispose();
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
                    
                } catch (Exception e) {
                    setVisible(false);
                    dispose();
                    
                    JOptionPane.showMessageDialog(null,
                        "Error saat inisialisasi aplikasi:\n" + e.getMessage() +
                        "\n\nPastikan:\n" +
                        "1. MySQL server berjalan\n" +
                        "2. Database 'perpustakaan_sma' sudah dibuat\n" +
                        "3. Konfigurasi database sudah benar",
                        "Initialization Error",
                        JOptionPane.ERROR_MESSAGE);
                    
                    System.exit(1);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Show splash with simulated initialization (for testing)
     */
    public void showSplashAndInitialize(Runnable onComplete) {
        setVisible(true);
        
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                String[] steps = {
                    "Memulai aplikasi...",
                    "Memuat konfigurasi...",
                    "Menginisialisasi database...",
                    "Memuat komponen...",
                    "Menyiapkan interface...",
                    "Selesai!"
                };
                
                for (int i = 0; i < steps.length; i++) {
                    updateStatus(steps[i]);
                    publish((i + 1) * 100 / steps.length);
                    Thread.sleep(300);
                }
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                for (Integer value : chunks) {
                    updateProgress(value);
                }
            }
            
            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    setVisible(false);
                    dispose();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
            }
        };
        
        worker.execute();
    }
}