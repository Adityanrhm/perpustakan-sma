package com.perpustakaan.view;

import com.perpustakaan.dao.UserDAO;
import com.perpustakaan.model.User;
import com.perpustakaan.util.UserSession;
import com.perpustakaan.view.admin.AdminDashboard;
import com.perpustakaan.view.pustakawan.PustakawanDashboard;
import com.perpustakaan.view.siswa.SiswaDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Login Frame untuk autentikasi user dengan enhanced UI
 */
public class LoginFrame extends JFrame {
    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);
    
    private JTextField txtLoginId;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private JLabel lblLoginId;
    private JLabel lblPassword;
    private JLabel lblLogo;
    private JPanel panelMain;
    private JPanel panelLeft;
    private JPanel panelRight;
    private JPanel panelForm;
    private JPanel panelButton;
    private JPanel panelHeader;
    
    private UserDAO userDAO;
    
    public LoginFrame() {
        userDAO = new UserDAO();
        initComponents();
        setupLayout();
        setupEventListeners();
        
        setTitle("Login - Perpustakaan HAEIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true); // Remove default window decorations
        
        // Create custom shape for rounded corners
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Add window shadow effect
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SHADOW_COLOR, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    private void initComponents() {
        // Main panels
        panelMain = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(74, 144, 226),
                    getWidth(), getHeight(), new Color(52, 152, 219)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        
        panelLeft = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panelLeft.setLayout(new BorderLayout());
        panelLeft.setPreferredSize(new Dimension(400, 600));
        
        panelRight = new JPanel(new BorderLayout());
        panelRight.setBackground(WHITE);
        panelRight.setPreferredSize(new Dimension(500, 600));
        
        panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        
        panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(WHITE);
        panelForm.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelButton.setBackground(WHITE);
        
        // Logo - You can replace this with actual image loading
        lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setVerticalAlignment(SwingConstants.CENTER);
        
        // Try to load logo image, fallback to icon if not found
        URL logoUrl = getClass().getResource("/images/logoperpussmahaein.png");
        if (logoUrl != null) {
            ImageIcon originalIcon = new ImageIcon(logoUrl);

            // Resize sesuai kebutuhan, misal 250x250
            Image scaledImage = originalIcon.getImage().getScaledInstance(430, 590, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            lblLogo.setIcon(scaledIcon);
        } else {
            lblLogo.setText("HAEIN LIBRARY SYSTEM");
        }

        // Title and subtitle
        lblTitle = new JLabel("Selamat Datang", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_COLOR);
        
        lblSubtitle = new JLabel("Masuk ke akun Anda", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(149, 165, 166));
        
        // Form labels
        lblLoginId = new JLabel("Username/NIS/NIP");
        lblLoginId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLoginId.setForeground(TEXT_COLOR);
        
        lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(TEXT_COLOR);
        
        // Form fields with custom styling
        txtLoginId = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Border
                if (hasFocus()) {
                    g2.setColor(PRIMARY_COLOR);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(new Color(220, 220, 220));
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtLoginId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLoginId.setBorder(new EmptyBorder(12, 15, 12, 15));
        txtLoginId.setBackground(new Color(248, 249, 250));
        txtLoginId.setOpaque(false);
        
        txtPassword = new JPasswordField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Border
                if (hasFocus()) {
                    g2.setColor(PRIMARY_COLOR);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(new Color(220, 220, 220));
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(new EmptyBorder(12, 15, 12, 15));
        txtPassword.setBackground(new Color(248, 249, 250));
        txtPassword.setOpaque(false);
        
        // Custom buttons
        btnLogin = createStyledButton("Masuk", PRIMARY_COLOR, WHITE, true);
        btnExit = createStyledButton("Keluar", ERROR_COLOR, WHITE, false);
        
        // Close button for undecorated window
        JButton btnClose = new JButton("×");
        btnClose.setFont(new Font("Arial", Font.BOLD, 20));
        btnClose.setForeground(TEXT_COLOR);
        btnClose.setBackground(WHITE);
        btnClose.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                LoginFrame.this,
                "Apakah Anda yakin ingin keluar?",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.setBackground(WHITE);
        closePanel.add(btnClose);
        
        panelHeader.add(closePanel, BorderLayout.EAST);
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor, boolean isPrimary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color currentBg = getModel().isPressed() ? bgColor.darker() : 
                                 getModel().isRollover() ? bgColor.brighter() : bgColor;
                
                g2.setColor(currentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setPreferredSize(new Dimension(120, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        // Left panel layout
        panelLeft.add(lblLogo, BorderLayout.CENTER);
        
        // Header layout
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(WHITE);
        titlePanel.setBorder(new EmptyBorder(30, 0, 20, 0));
        
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(lblSubtitle);
        
        // Form layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        panelForm.add(lblLoginId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        panelForm.add(txtLoginId, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        panelForm.add(lblPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        panelForm.add(txtPassword, gbc);
        
        // Buttons
        panelButton.add(btnLogin);
        panelButton.add(btnExit);
        
        // Right panel layout
        panelRight.add(panelHeader, BorderLayout.NORTH);
        panelRight.add(titlePanel, BorderLayout.NORTH);
        panelRight.add(panelForm, BorderLayout.CENTER);
        panelRight.add(panelButton, BorderLayout.SOUTH);
        
        // Main panel
        panelMain.add(panelLeft, BorderLayout.WEST);
        panelMain.add(panelRight, BorderLayout.CENTER);
        
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
            public void keyReleased (KeyEvent e) {}
        });
        
        // Make window draggable
        addMouseMotionListener(new MouseAdapter() {
            private Point initialClick;
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick != null) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;
                    
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;
                    
                    int X = thisX + xMoved;
                    int Y = thisY + yMoved;
                    setLocation(X, Y);
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
            
            private Point initialClick;
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick != null) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;
                    
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;
                    
                    int X = thisX + xMoved;
                    int Y = thisY + yMoved;
                    setLocation(X, Y);
                }
            }
        });
    }
    
    private void performLogin() {
        String loginId = txtLoginId.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (loginId.isEmpty()) {
            showCustomMessage("Username/NIS/NIP tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            txtLoginId.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showCustomMessage("Password tidak boleh kosong!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        
        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);
        btnLogin.setText("Memuat...");
        
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticate(loginId, password);
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnLogin.setEnabled(true);
                btnLogin.setText("Masuk");
                
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
                        showCustomMessage("Username/NIS/NIP atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText("");
                        txtLoginId.selectAll();
                        txtLoginId.requestFocus();
                    }
                } catch (Exception e) {
                    showCustomMessage("Error saat login: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private void showCustomMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", WHITE);
        UIManager.put("Panel.background", WHITE);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        
        JOptionPane.showMessageDialog(this, message, title, messageType);
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
                showCustomMessage("Role tidak dikenali: " + roleName, "Error", JOptionPane.ERROR_MESSAGE);
                UserSession.getInstance().endSession();
                break;
        }
    }
}