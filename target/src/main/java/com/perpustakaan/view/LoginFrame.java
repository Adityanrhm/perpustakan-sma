package com.perpustakaan.view;

import com.perpustakaan.dao.UserDAO;
import com.perpustakaan.model.User;
import com.perpustakaan.util.UserSession;
import com.perpustakaan.view.admin.AdminDashboard;
import com.perpustakaan.view.pustakawan.PustakawanDashboard;
import com.perpustakaan.view.siswa.SiswaDashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;

/**
 * Login Frame untuk autentikasi user
 */
public class LoginFrame extends JFrame {
    private JTextField txtLoginId;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblTitle;
    private JLabel lblLoginId;
    private JLabel lblPassword;
    private JPanel panelMain;
    private JPanel panelForm;
    private JPanel panelButton;
    
    private UserDAO userDAO;
    
    public LoginFrame() {
        userDAO = new UserDAO();
        initComponents();
        setupLayout();
        setupEventListeners();
        
        setTitle("Login - Perpustakaan HAEIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        panelMain = new JPanel(new BorderLayout());
        panelForm = new JPanel(new GridBagLayout());
        panelButton = new JPanel(new FlowLayout());
        
        lblTitle = new JLabel("SISTEM PERPUSTAKAAN HAEIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        lblLoginId = new JLabel("Username/NIS/NIP:");
        txtLoginId = new JTextField(20);
        
        lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField(20);
        
        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(100, 30));
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        
        btnExit = new JButton("Keluar");
        btnExit.setPreferredSize(new Dimension(100, 30));
        btnExit.setBackground(new Color(220, 53, 69));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
    }
    
    private void setupLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username/NIS/NIP
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(lblLoginId, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(txtLoginId, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panelForm.add(lblPassword, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(txtPassword, gbc);
        
        // Buttons
        panelButton.add(btnLogin);
        panelButton.add(btnExit);
        
        // Main panel
        panelMain.add(lblTitle, BorderLayout.NORTH);
        panelMain.add(panelForm, BorderLayout.CENTER);
        panelMain.add(panelButton, BorderLayout.SOUTH);
        
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(panelMain);
    }
    
    private void setupEventListeners() {
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    LoginFrame.this,
                    "Apakah Anda yakin ingin keluar?",
                    "Konfirmasi Keluar",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        
        // Enter key listener for password field
        txtPassword.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        // Enter key listener for login id field
        txtLoginId.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    private void performLogin() {
        String loginId = txtLoginId.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (loginId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username/NIS/NIP tidak boleh kosong!",
                "Validasi Error",
                JOptionPane.ERROR_MESSAGE);
            txtLoginId.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Password tidak boleh kosong!",
                "Validasi Error",
                JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        
        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);
        
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticate(loginId, password);
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnLogin.setEnabled(true);
                
                try {
                    User user = get();
                    if (user != null) {
                        // Start user session
                        UserSession.getInstance().startSession(user);
                        
                        // Open appropriate dashboard based on role
                        openDashboard(user);
                        
                        // Close login frame
                        dispose();
                        
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            "Username/NIS/NIP atau password salah!",
                            "Login Gagal",
                            JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText("");
                        txtLoginId.selectAll();
                        txtLoginId.requestFocus();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Error saat login: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private void openDashboard(User user) {
        String roleName = user.getRole().getNamaRole();
        
        switch (roleName.toUpperCase()) {
            case "ADMIN":
                new AdminDashboard().setVisible(true);
                break;
            case "PUSTAKAWAN":
                new PustakawanDashboard().setVisible(true);
                break;
            case "SISWA":
                new SiswaDashboard().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                    "Role tidak dikenali: " + roleName,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                UserSession.getInstance().endSession();
                break;
        }
    }
}