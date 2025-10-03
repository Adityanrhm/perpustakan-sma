package com.perpustakaan.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Professional Splash Screen - Logo Theme Inspired
 */
public class SplashScreen extends JWindow {
    // Color palette dari logo HAEIN
    private static final Color LOGO_BLUE = new Color(30, 136, 229);      // Biru utama logo
    private static final Color LOGO_DARK_BLUE = new Color(13, 71, 161);  // Biru tua
    private static final Color LOGO_LIGHT_BLUE = new Color(66, 165, 245); // Biru muda
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);   // Aksen oranye
    private static final Color PURE_WHITE = Color.WHITE;
    private static final Color SOFT_WHITE = new Color(250, 250, 250);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JLabel lblPercentage;
    private JLabel lblLogo;
    private int progress = 0;
    
    public SplashScreen() {
        initComponents();
        setSize(800, 550);
        setLocationRelativeTo(null);
        
        try {
            setShape(new RoundRectangle2D.Double(0, 0, 800, 550, 20, 20));
        } catch (Exception e) {
            // Fallback
        }
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Pure white background
                g2.setColor(PURE_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Top blue section with logo theme gradient
                GradientPaint topGradient = new GradientPaint(
                    0, 0, LOGO_BLUE,
                    getWidth(), 180, LOGO_LIGHT_BLUE
                );
                g2.setPaint(topGradient);
                g2.fillRoundRect(0, 0, getWidth(), 220, 20, 20);
                
                // Diagonal accent stripe
                g2.setColor(new Color(ACCENT_ORANGE.getRed(), ACCENT_ORANGE.getGreen(), 
                                     ACCENT_ORANGE.getBlue(), 200));
                Path2D stripe = new Path2D.Double();
                stripe.moveTo(getWidth() * 0.7, 0);
                stripe.lineTo(getWidth(), 0);
                stripe.lineTo(getWidth(), 80);
                stripe.lineTo(getWidth() * 0.75, 0);
                stripe.closePath();
                g2.fill(stripe);
                
                // Bottom section subtle gradient
                GradientPaint bottomGradient = new GradientPaint(
                    0, 220, PURE_WHITE,
                    0, getHeight(), SOFT_WHITE
                );
                g2.setPaint(bottomGradient);
                g2.fillRect(0, 220, getWidth(), getHeight() - 220);
                
                // Decorative geometric elements
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                
                // Circle patterns
                g2.setColor(PURE_WHITE);
                g2.fillOval(getWidth() - 200, -50, 250, 250);
                g2.fillOval(-80, 100, 180, 180);
                
                // Bottom right corner accent
                g2.setColor(LOGO_LIGHT_BLUE);
                g2.fillOval(getWidth() - 150, getHeight() - 150, 200, 200);
                
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Logo section
        JPanel logoSection = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = 100;
                int radius = 65;
                
                // Outer glow ring
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(255, 255, 255, 40 - (i * 8)));
                    g2.fillOval(centerX - radius - i * 3, centerY - radius - i * 3, 
                               (radius + i * 3) * 2, (radius + i * 3) * 2);
                }
                
                // White circle background
                g2.setColor(PURE_WHITE);
                g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                
                // Gradient border
                GradientPaint borderGradient = new GradientPaint(
                    centerX - radius, centerY, LOGO_BLUE,
                    centerX + radius, centerY, ACCENT_ORANGE
                );
                g2.setPaint(borderGradient);
                g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(centerX - radius + 2, centerY - radius + 2, 
                           (radius - 2) * 2, (radius - 2) * 2);
                
                g2.dispose();
            }
        };
        logoSection.setOpaque(false);
        logoSection.setLayout(new GridBagLayout());
        logoSection.setPreferredSize(new Dimension(800, 220));
        
//        lblLogo = new JLabel();
//        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
//        
//        try {
//            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logoperpussmahaein.png"));
//            Image scaledImage = logoIcon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
//            lblLogo.setIcon(new ImageIcon(scaledImage));
//        } catch (Exception e) {
//            // Modern book icon
//            lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 75));
//            lblLogo.setText("📚");
//        }
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 0, 0, 0);
//        logoSection.add(lblLogo, gbc);
        
        // Title section
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setOpaque(false);
        titleSection.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel lblBrand = new JLabel("HAEIN");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblBrand.setForeground(LOGO_BLUE);
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel("LIBRARY MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Professional separator
        JPanel separator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int centerY = height / 2;
                
                // Center bar with gradient
                GradientPaint barGradient = new GradientPaint(
                    width / 2 - 80, centerY, ACCENT_ORANGE,
                    width / 2 + 80, centerY, LOGO_BLUE
                );
                g2.setPaint(barGradient);
                g2.fillRoundRect(width / 2 - 80, centerY - 2, 160, 4, 4, 4);
                
                g2.dispose();
            }
        };
        separator.setOpaque(false);
        separator.setPreferredSize(new Dimension(400, 25));
        separator.setMaximumSize(new Dimension(400, 25));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Sistem Informasi Perpustakaan SMA");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titleSection.add(lblBrand);
        titleSection.add(Box.createVerticalStrut(8));
        titleSection.add(lblTitle);
        titleSection.add(Box.createVerticalStrut(15));
        titleSection.add(separator);
        titleSection.add(Box.createVerticalStrut(10));
        titleSection.add(lblSubtitle);
        
        // Progress section
        JPanel progressSection = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card with subtle shadow
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(42, 17, getWidth() - 84, getHeight() - 34, 15, 15);
                
                g2.setColor(PURE_WHITE);
                g2.fillRoundRect(40, 15, getWidth() - 80, getHeight() - 30, 15, 15);
                
                // Border accent
                g2.setColor(new Color(LOGO_BLUE.getRed(), LOGO_BLUE.getGreen(), 
                                     LOGO_BLUE.getBlue(), 30));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(40, 15, getWidth() - 80, getHeight() - 30, 15, 15);
                
                g2.dispose();
            }
        };
        progressSection.setOpaque(false);
        progressSection.setLayout(new BoxLayout(progressSection, BoxLayout.Y_AXIS));
        progressSection.setBorder(BorderFactory.createEmptyBorder(25, 100, 25, 100));
        
        // Progress bar
        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Track
                g2.setColor(new Color(240, 240, 240));
                g2.fillRoundRect(0, 0, w, h, h, h);
                
                // Progress
                int progressWidth = (int) ((w - 4) * (getValue() / (double) getMaximum()));
                if (progressWidth > 0) {
                    // Multi-color gradient based on progress
                    Color startColor = LOGO_BLUE;
                    Color endColor = ACCENT_ORANGE;
                    
                    if (getValue() >= 80) {
                        startColor = LOGO_LIGHT_BLUE;
                        endColor = new Color(76, 175, 80); // Green for completion
                    }
                    
                    GradientPaint gradient = new GradientPaint(
                        0, 0, startColor,
                        progressWidth, 0, endColor
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(2, 2, progressWidth, h - 4, h - 4, h - 4);
                    
                    // Glossy effect
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                    GradientPaint gloss = new GradientPaint(
                        0, 0, PURE_WHITE,
                        0, h / 2, new Color(255, 255, 255, 0)
                    );
                    g2.setPaint(gloss);
                    g2.fillRoundRect(2, 2, progressWidth, h / 2, h / 2, h / 2);
                    
                    // Animated shimmer (optional)
                    if (getValue() < 100) {
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                        g2.setColor(PURE_WHITE);
                        int shimmerPos = (int) (progressWidth * 0.7);
                        g2.fillRoundRect(shimmerPos - 20, 2, 40, h - 4, h - 4, h - 4);
                    }
                }
                
                g2.dispose();
            }
        };
        progressBar.setPreferredSize(new Dimension(600, 32));
        progressBar.setMaximumSize(new Dimension(600, 32));
        progressBar.setStringPainted(false);
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Percentage with modern styling
        lblPercentage = new JLabel("0%");
        lblPercentage.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPercentage.setForeground(LOGO_BLUE);
        lblPercentage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Status message
        lblStatus = new JLabel("Initializing system components...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setForeground(TEXT_SECONDARY);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Loading indicator
        JLabel lblIndicator = new JLabel("●●●");
        lblIndicator.setFont(new Font("Arial", Font.PLAIN, 12));
        lblIndicator.setForeground(LOGO_LIGHT_BLUE);
        lblIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        progressSection.add(Box.createVerticalStrut(15));
        progressSection.add(progressBar);
        progressSection.add(Box.createVerticalStrut(15));
        progressSection.add(lblPercentage);
        progressSection.add(Box.createVerticalStrut(20));
        progressSection.add(lblStatus);
        progressSection.add(Box.createVerticalStrut(8));
        progressSection.add(lblIndicator);
        progressSection.add(Box.createVerticalStrut(10));
        
        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        JLabel lblCopyright = new JLabel("© 2025 SMA HAEIN");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(TEXT_SECONDARY);
        
        JLabel lblSeparator = new JLabel("|");
        lblSeparator.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSeparator.setForeground(new Color(200, 200, 200));
        
        JLabel lblVersion = new JLabel("Version 1.0.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVersion.setForeground(TEXT_SECONDARY);
        
        JLabel lblSeparator2 = new JLabel("|");
        lblSeparator2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSeparator2.setForeground(new Color(200, 200, 200));
        
        JLabel lblTech = new JLabel("Java • MySQL");
        lblTech.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTech.setForeground(TEXT_SECONDARY);
        
        footer.add(lblCopyright);
        footer.add(lblSeparator);
        footer.add(lblVersion);
        footer.add(lblSeparator2);
        footer.add(lblTech);
        
        // Main layout
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        
        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);
        topArea.add(logoSection, BorderLayout.NORTH);
        topArea.add(titleSection, BorderLayout.CENTER);
        
        contentArea.add(topArea, BorderLayout.NORTH);
        contentArea.add(progressSection, BorderLayout.CENTER);
        contentArea.add(footer, BorderLayout.SOUTH);
        
        mainPanel.add(contentArea, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }
    
    public void setProgress(int value) {
        progress = value;
        progressBar.setValue(value);
        lblPercentage.setText(value + "%");
        
        // Dynamic color change
        if (value >= 80) {
            lblPercentage.setForeground(new Color(76, 175, 80));
        } else if (value >= 50) {
            lblPercentage.setForeground(ACCENT_ORANGE);
        } else {
            lblPercentage.setForeground(LOGO_BLUE);
        }
        
        progressBar.repaint();
    }
    
    public void setStatus(String message) {
        lblStatus.setText(message);
    }
    
    public void showSplashAndInitialize(Runnable onComplete) {
        setVisible(true);
        
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                String[] messages = {
                    "Loading system configuration...",
                    "Establishing database connection...",
                    "Verifying database integrity...",
                    "Loading application modules...",
                    "Initializing user interface...",
                    "Ready to launch!"
                };
                
                int[] progressSteps = {18, 38, 58, 78, 95, 100};
                
                for (int i = 0; i < messages.length; i++) {
                    setStatus(messages[i]);
                    
                    int startProgress = i > 0 ? progressSteps[i-1] : 0;
                    int endProgress = progressSteps[i];
                    
                    for (int p = startProgress; p <= endProgress; p++) {
                        publish(p);
                        Thread.sleep(i == messages.length - 1 ? 8 : 18);
                    }
                    
                    if (i < messages.length - 1) {
                        Thread.sleep(150);
                    }
                }
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    setProgress(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dispose();
                if (onComplete != null) {
                    SwingUtilities.invokeLater(onComplete);
                }
            }
        };
        
        worker.execute();
    }
    
    public void showSplashWithRealInit(com.perpustakaan.config.DatabaseConfig dbConfig, Runnable onComplete) {
        setVisible(true);
        
        SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    setStatus("Loading system configuration...");
                    animateProgress(0, 18, 300);
                    
                    setStatus("Establishing database connection...");
                    animateProgress(18, 38, 500);
                    
                    setStatus("Verifying database integrity...");
                    boolean connected = dbConfig.testConnection();
                    if (!connected) {
                        throw new Exception("Database connection failed!");
                    }
                    animateProgress(38, 58, 400);
                    
                    setStatus("Loading database information...");
                    String dbInfo = dbConfig.getDatabaseInfo();
                    System.out.println(dbInfo);
                    animateProgress(58, 78, 400);
                    
                    setStatus("Initializing application components...");
                    animateProgress(78, 95, 450);
                    
                    setStatus("Ready to launch!");
                    animateProgress(95, 100, 200);
                    Thread.sleep(300);
                    
                    return true;
                    
                } catch (Exception e) {
                    setStatus("Error: " + e.getMessage());
                    Thread.sleep(2000);
                    throw e;
                }
            }
            
            private void animateProgress(int start, int end, int duration) throws InterruptedException {
                int steps = end - start;
                int delay = Math.max(10, duration / steps);
                
                for (int i = start; i <= end; i++) {
                    publish(i);
                    Thread.sleep(delay);
                }
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    setProgress(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    dispose();
                    
                    if (success && onComplete != null) {
                        SwingUtilities.invokeLater(onComplete);
                    }
                } catch (Exception e) {
                    dispose();
                    JOptionPane.showMessageDialog(null,
                        "Failed to initialize application:\n" + e.getMessage(),
                        "Initialization Error",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        };
        
        worker.execute();
    }
}