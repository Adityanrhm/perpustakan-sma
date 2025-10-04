package com.perpustakaan.view.admin;

import com.perpustakaan.util.UserSession;
import com.perpustakaan.util.UIHelper;
import com.perpustakaan.view.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Admin Dashboard - Simple & Clean (No Sidebar)
 */
public class AdminDashboard extends JFrame {
    private static final Color PRIMARY_BROWN = new Color(92, 64, 51);
    private static final Color SECONDARY_BROWN = new Color(139, 90, 60);
    private static final Color ACCENT_CREAM = new Color(245, 238, 220);
    private static final Color TEXT_DARK = new Color(62, 44, 36);
    private static final Color BACKGROUND_LIGHT = new Color(250, 248, 243);
    
    private JLabel lblWelcome, lblUserInfo, lblDateTime;
    
    public AdminDashboard() {
        initComponents();
        setupLayout();
        updateUserInfo();
        
        setTitle("Admin Dashboard - Perpustakaan SMA Haein");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    private void initComponents() {
        lblWelcome = new JLabel("Selamat Datang, Administrator!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(TEXT_DARK);
        
        lblUserInfo = new JLabel("");
        lblUserInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUserInfo.setForeground(SECONDARY_BROWN);
        
        lblDateTime = new JLabel("");
        lblDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDateTime.setForeground(new Color(127, 140, 141));
        
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();
        updateDateTime();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_LIGHT);
        
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        topBar.add(lblWelcome, BorderLayout.WEST);
        
        JPanel topRight = new JPanel();
        topRight.setLayout(new BoxLayout(topRight, BoxLayout.Y_AXIS));
        topRight.setBackground(Color.WHITE);
        lblUserInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lblDateTime.setAlignmentX(Component.RIGHT_ALIGNMENT);
        topRight.add(lblUserInfo);
        topRight.add(Box.createRigidArea(new Dimension(0, 3)));
        topRight.add(lblDateTime);
        
        topBar.add(topRight, BorderLayout.EAST);
        
        // Main content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_LIGHT);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Stats cards
        JPanel statsPanel = createStatsPanel();
        mainContent.add(statsPanel, BorderLayout.NORTH);
        
        // Quick access
        JPanel quickAccessPanel = createQuickAccessPanel();
        mainContent.add(quickAccessPanel, BorderLayout.CENTER);
        
        add(topBar, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }
    
    private JPanel createStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 20, 0));
        stats.setBackground(BACKGROUND_LIGHT);
        stats.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        stats.add(createStatCard("---", "Total Buku", PRIMARY_BROWN));
        stats.add(createStatCard("---", "Total Siswa", SECONDARY_BROWN));
        stats.add(createStatCard("---", "Transaksi Aktif", new Color(41, 128, 185)));
        stats.add(createStatCard("---", "Peminjaman Hari Ini", new Color(46, 204, 113)));
        
        return stats;
    }
    
    private JPanel createStatCard(String value, String title, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(accentColor);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(127, 140, 141));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(lblValue);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblTitle);
        
        return card;
    }
    
    private JPanel createQuickAccessPanel() {
        JPanel quickAccess = new JPanel(new BorderLayout());
        quickAccess.setBackground(BACKGROUND_LIGHT);
        
        JLabel lblTitle = new JLabel("Akses Cepat");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsPanel.setBackground(BACKGROUND_LIGHT);
        
        // Hanya module untuk Admin sesuai kode awal
        cardsPanel.add(createQuickCard("Kelola Pustakawan", () -> openPustakawanManagement()));
        cardsPanel.add(createQuickCard("Kelola Siswa", () -> openSiswaManagement()));
        cardsPanel.add(createQuickCard("Laporan Data Buku", () -> openReportFrame()));
        cardsPanel.add(createQuickCard("Laporan Transaksi Peminjaman", () -> openReportFrame()));
        cardsPanel.add(createQuickCard("Laporan Data Siswa", () -> openReportFrame()));
        
        // Logout card
        JPanel logoutCard = createQuickCard("Logout", () -> logout());
        logoutCard.setBackground(new Color(231, 76, 60));
        ((JLabel) logoutCard.getComponent(0)).setForeground(Color.WHITE);
        cardsPanel.add(logoutCard);
        
        quickAccess.add(lblTitle, BorderLayout.NORTH);
        quickAccess.add(cardsPanel, BorderLayout.CENTER);
        
        return quickAccess;
    }
    
    private JPanel createQuickCard(String title, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        
        card.add(label, BorderLayout.CENTER);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalBg = card.getBackground();
            
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (originalBg.equals(Color.WHITE)) {
                    card.setBackground(ACCENT_CREAM);
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_BROWN, 2),
                        BorderFactory.createEmptyBorder(29, 19, 29, 19)
                    ));
                } else {
                    card.setBackground(new Color(192, 57, 43));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(originalBg);
                if (originalBg.equals(Color.WHITE)) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                        BorderFactory.createEmptyBorder(30, 20, 30, 20)
                    ));
                }
            }
        });
        
        return card;
    }
    
    private void updateUserInfo() {
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            lblUserInfo.setText(session.getCurrentUser().getLoginIdentifier() + " | " + session.getLoginDuration());
        }
    }
    
    private void updateDateTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");
        lblDateTime.setText(now.format(formatter));
        updateUserInfo();
    }
    
    // Actions - Hanya untuk Admin
    private void openPustakawanManagement() {
        try {
            new PustakawanManagementFrame(this).setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error: " + e.getMessage());
        }
    }
    
    private void openSiswaManagement() {
        try {
            new SiswaManagementFrame(this).setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error: " + e.getMessage());
        }
    }
    
    private void openReportFrame() {
        try {
            new ReportFrame().setVisible(true);
        } catch (Exception e) {
            UIHelper.showErrorMessage(this, "Error: " + e.getMessage());
        }
    }
    
    private void logout() {
        if (UIHelper.showConfirmDialog(this, "Yakin ingin logout?")) {
            UserSession.getInstance().endSession();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void exitApplication() {
        if (UIHelper.showConfirmDialog(this, "Yakin ingin keluar?")) {
            UserSession.getInstance().endSession();
            System.exit(0);
        }
    }
}