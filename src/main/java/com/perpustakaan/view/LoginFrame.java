package com.perpustakaan.view;

import com.formdev.flatlaf.FlatLightLaf;
import com.perpustakaan.dao.UserDAO;
import com.perpustakaan.dao.SiswaDAO;
import com.perpustakaan.dao.PustakawanDAO;
import com.perpustakaan.dao.AdminDAO;
import com.perpustakaan.model.User;
import com.perpustakaan.util.UserSession;
import com.perpustakaan.view.pustakawan.PustakawanDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern Login Frame dengan logo asli dan tema coklat
 */
public class LoginFrame extends JFrame {
    private UserDAO userDAO;
    private SiswaDAO siswaDAO;
    private PustakawanDAO pustakawanDAO;
    private AdminDAO adminDAO;
    
    // Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblForgotPassword;
    private JCheckBox cbShowPassword;
    private JProgressBar progressBar;
    
    // Colors - Tema coklat dari logo
    private static final Color PRIMARY_BROWN = new Color(92, 64, 51);      // Coklat tua dari logo
    private static final Color SECONDARY_BROWN = new Color(139, 90, 60);   // Coklat medium
    private static final Color ACCENT_CREAM = new Color(245, 238, 220);    // Krem dari background logo
    private static final Color TEXT_DARK = new Color(62, 44, 36);          // Coklat sangat gelap
    private static final Color BACKGROUND_LIGHT = new Color(250, 248, 243); // Background terang
    
    public LoginFrame() {
        userDAO = new UserDAO();
        siswaDAO = new SiswaDAO();
        pustakawanDAO = new PustakawanDAO();
        adminDAO = new AdminDAO();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        
        setTitle("Login - Perpustakaan SMA Haein");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initComponents() {
        // Username field
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        // Password field
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        // Show password checkbox
        cbShowPassword = new JCheckBox("Tampilkan Password");
        cbShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbShowPassword.setFocusPainted(false);
        cbShowPassword.setOpaque(false);
        
        // Login button
        btnLogin = new JButton("MASUK");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(PRIMARY_BROWN);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(200, 48));
        
        // Forgot password link
        lblForgotPassword = new JLabel("Lupa Password?");
        lblForgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblForgotPassword.setForeground(SECONDARY_BROWN);
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(300, 4));
        progressBar.setForeground(PRIMARY_BROWN);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_LIGHT);
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);
        
        // Left panel - Logo dan branding
        JPanel leftPanel = createLogoPanel();
        
        // Right panel - Login form
        JPanel rightPanel = createLoginFormPanel();
        
        // Add panels
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.45;
        gbc.weighty = 1.0;
        
        gbc.gridx = 0;
        mainPanel.add(leftPanel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.55;
        mainPanel.add(rightPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ACCENT_CREAM);
        panel.setBorder(new EmptyBorder(50, 40, 50, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        
        // Logo asli (menggunakan komponen sederhana untuk representasi)
        JPanel logoPanel = createLogoGraphics();
        gbc.gridy = 0;
        panel.add(logoPanel, gbc);
        
        // Title
        JLabel lblTitle1 = new JLabel("PERPUSTAKAAN");
        lblTitle1.setFont(new Font("Impact", Font.BOLD, 36));
        lblTitle1.setForeground(PRIMARY_BROWN);
        lblTitle1.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 0, 5, 0);
        panel.add(lblTitle1, gbc);
        
        JLabel lblTitle2 = new JLabel("SMA HAEIN");
        lblTitle2.setFont(new Font("Impact", Font.BOLD, 36));
        lblTitle2.setForeground(PRIMARY_BROWN);
        lblTitle2.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblTitle2, gbc);
        
        // Divider line
        JSeparator separator = new JSeparator();
        separator.setForeground(SECONDARY_BROWN);
        separator.setPreferredSize(new Dimension(200, 2));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 50, 10, 50);
        panel.add(separator, gbc);
        
        // Tagline
        JLabel lblTagline = new JLabel("Sistem Manajemen Perpustakaan");
        lblTagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblTagline.setForeground(TEXT_DARK);
        lblTagline.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(lblTagline, gbc);
        
        return panel;
    }

    private JPanel createLogoGraphics() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);

        // Load logo dari resources
        java.net.URL imageURL = getClass().getResource("/images/logoperpussmahaein.png");
        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(img));
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
            logoPanel.add(lblLogo, BorderLayout.CENTER);
        } else {
            JLabel lblError = new JLabel("Logo not found");
            lblError.setHorizontalAlignment(SwingConstants.CENTER);
            logoPanel.add(lblError, BorderLayout.CENTER);
        }

        return logoPanel;
    }


    private JPanel createLoginFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(60, 60, 60, 60));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        
        // Welcome text
        JLabel lblWelcome = new JLabel("Selamat Datang");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblWelcome.setForeground(TEXT_DARK);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(lblWelcome, gbc);
        
        // Subtitle
        JLabel lblSubtext = new JLabel("Masuk ke akun Anda");
        lblSubtext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtext.setForeground(new Color(127, 140, 141));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 35, 0);
        panel.add(lblSubtext, gbc);
        
        // Username label
        JLabel lblUsername = new JLabel("Username / NIS / NIP");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(TEXT_DARK);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 8, 0);
        panel.add(lblUsername, gbc);
        
        // Username field
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 18, 0);
        panel.add(txtUsername, gbc);
        
        // Password label
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(TEXT_DARK);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 8, 0);
        panel.add(lblPassword, gbc);
        
        // Password field
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(txtPassword, gbc);
        
        // Show password checkbox
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel.add(cbShowPassword, gbc);
        
        // Progress bar
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(progressBar, gbc);
        
        // Login button
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 0, 15, 0);
        panel.add(btnLogin, gbc);
        
        // Forgot password
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        forgotPanel.setBackground(Color.WHITE);
        forgotPanel.add(lblForgotPassword);
        gbc.gridy = 9;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(forgotPanel, gbc);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(new EmptyBorder(12, 0, 12, 0));
        
        JLabel lblFooter = new JLabel("© 2025 SMA Haein - Perpustakaan System v1.0");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(127, 140, 141));
        panel.add(lblFooter);
        
        return panel;
    }
    
    private void setupEventListeners() {
        btnLogin.addActionListener(e -> performLogin());
        
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
        
        cbShowPassword.addActionListener(e -> {
            txtPassword.setEchoChar(cbShowPassword.isSelected() ? (char) 0 : '•');
        });
        
        lblForgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleForgotPassword();
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblForgotPassword.setText("<html><u>Lupa Password?</u></html>");
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblForgotPassword.setText("Lupa Password?");
            }
        });
        
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(SECONDARY_BROWN);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(PRIMARY_BROWN);
            }
        });
    }
    
    private void performLogin() {
        String loginId = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (loginId.isEmpty()) {
            showError("Username/NIS/NIP tidak boleh kosong!");
            txtUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Password tidak boleh kosong!");
            txtPassword.requestFocus();
            return;
        }
        
        setFormEnabled(false);
        progressBar.setVisible(true);
        
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                Thread.sleep(500);
                return userDAO.authenticate(loginId, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    
                    if (user != null) {
                        handleSuccessfulLogin(user);
                    } else {
                        showError("Username/Password salah!");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception e) {
                    showError("Error saat login: " + e.getMessage());
                } finally {
                    setFormEnabled(true);
                    progressBar.setVisible(false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void handleSuccessfulLogin(User user) {
        UserSession.getInstance().startSession(user);
        
        try {
            String roleName = user.getRole().getNamaRole();
            
            switch (roleName) {
                case "ADMIN":
                    openAdminDashboard();
                    break;
                case "PUSTAKAWAN":
                    openPustakawanDashboard();
                    break;
                case "SISWA":
                    openSiswaDashboard();
                    break;
                default:
                    showError("Role tidak dikenali: " + roleName);
                    return;
            }
            
            dispose();
            
        } catch (Exception e) {
            showError("Error membuka dashboard: " + e.getMessage());
        }
    }
    
    private void openAdminDashboard() {
        SwingUtilities.invokeLater(() -> {
            com.perpustakaan.view.admin.AdminDashboard dashboard = 
                new com.perpustakaan.view.admin.AdminDashboard();
            dashboard.setVisible(true);
        });
    }
    
    private void openPustakawanDashboard() {
        SwingUtilities.invokeLater(() -> {
            PustakawanDashboard dashboard = new PustakawanDashboard();
            dashboard.setVisible(true);
        });
    }
    
    private void openSiswaDashboard() {
        JOptionPane.showMessageDialog(this, 
            "Siswa Dashboard belum tersedia.",
            "Info", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleForgotPassword() {
        JOptionPane.showMessageDialog(this,
            "Untuk reset password, silakan hubungi administrator perpustakaan.\n\n" +
            "Contact: admin@perpustakaan.sch.id",
            "Lupa Password",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void setFormEnabled(boolean enabled) {
        txtUsername.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
        cbShowPassword.setEnabled(enabled);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Login Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}