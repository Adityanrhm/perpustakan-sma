package com.perpustakaan.view.admin;

import com.perpustakaan.dao.PustakawanDAO;
import com.perpustakaan.dao.UserDAO;
import com.perpustakaan.dao.RoleDAO;
import com.perpustakaan.model.Pustakawan;
import com.perpustakaan.model.User;
import com.perpustakaan.model.Role;
import com.perpustakaan.util.TableModelHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Frame untuk mengelola data pustakawan
 */
public class PustakawanManagementFrame extends JDialog {
    private PustakawanDAO pustakawanDAO;
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    
    // Components
    private JTable tablePustakawan;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    // Form components - ADD status aktif checkbox
    private JTextField txtNip, txtNama, txtEmail, txtTelepon, txtUsername;
    private JPasswordField txtPassword;
    private JTextArea txtAlamat;
    private JCheckBox cbStatusAktif;
    private JButton btnAdd, btnEdit, btnDelete, btnSave, btnCancel, btnRefresh;
    
    // Search components
    private JTextField txtSearch;
    private JButton btnSearch, btnResetFilter;
    
    // Form panel
    private JPanel formPanel;
    private boolean isEditing = false;
    private int selectedPustakawanId = 0;
    
    public PustakawanManagementFrame(Frame parent) {
        super(parent, "Kelola Data Pustakawan", true); // true = modal
        pustakawanDAO = new PustakawanDAO();
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Table
        String[] columns = {"ID", "NIP",  "Username", "Nama Lengkap", "Email", "No. Telepon", "Alamat", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePustakawan = new JTable(tableModel);
        tablePustakawan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePustakawan.getColumnModel().getColumn(0).setMaxWidth(50);
        scrollPane = new JScrollPane(tablePustakawan);
        
        // Form components - REMOVE tanggal_mulai_kerja
        txtNip = new JTextField(20);
        txtNama = new JTextField(20);
        txtEmail = new JTextField();
        txtTelepon = new JTextField(15);
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtAlamat = new JTextArea(3, 20);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        
        cbStatusAktif = new JCheckBox("Status Aktif", true);
        
        // Search components
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Cari berdasarkan NIP atau Nama");
        
        // Buttons
        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");
        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");
        btnRefresh = new JButton("Refresh");
        btnSearch = new JButton("Cari");
        btnResetFilter = new JButton("Reset Filter");
        
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(255, 193, 7));
        btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(23, 162, 184));
        btnRefresh.setForeground(Color.WHITE);
        btnSearch.setBackground(new Color(40, 167, 69));
        btnSearch.setForeground(Color.WHITE);
        btnResetFilter.setBackground(new Color(108, 117, 125));
        btnResetFilter.setForeground(Color.WHITE);
        
        // Initially disable save and cancel buttons
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        setFormEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel with search and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Cari:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnResetFilter);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(btnAdd);
        topPanel.add(btnEdit);
        topPanel.add(btnDelete);
        topPanel.add(btnRefresh);
        
        // Form panel - REMOVE tanggal_mulai_kerja
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Data Pustakawan"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0: NIP
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("NIP:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtNip, gbc);
        
        // Row 0: Username
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtUsername, gbc);
        
        // Row 1: Nama
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtNama, gbc);
        
        // Row 1: Password
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtPassword, gbc);
        
        // Row 2: Email and Status Aktif
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cbStatusAktif, gbc);
        
        // Row 3: No Telepon
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtTelepon, gbc);
        
        // Row 4: Alamat (spanning 4 columns)
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JScrollPane(txtAlamat), gbc);
        
        // Button panel for form
        JPanel formButtonPanel = new JPanel(new FlowLayout());
        formButtonPanel.add(btnSave);
        formButtonPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(formButtonPanel, gbc);
        
        // Main layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Table selection
        tablePustakawan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tablePustakawan.getSelectedRow();
                btnEdit.setEnabled(selectedRow >= 0);
                btnDelete.setEnabled(selectedRow >= 0);
            }
        });
        
        btnAdd.addActionListener(e -> startAddMode());
        btnEdit.addActionListener(e -> startEditMode());
        btnDelete.addActionListener(e -> deletePustakawan());
        btnSave.addActionListener(e -> savePustakawan());
        btnCancel.addActionListener(e -> cancelForm());
        btnRefresh.addActionListener(e -> loadData());
        
        // Search functionality
        btnSearch.addActionListener(e -> searchPustakawan());
        btnResetFilter.addActionListener(e -> resetFilter());
        
        // Enter key for search
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    searchPustakawan();
                }
            }
        });
    }
    
    private void loadData() {
        try {
            List<Pustakawan> pustakawanList = pustakawanDAO.findAll();
            populateTable(pustakawanList);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateTable(List<Pustakawan> pustakawanList) {
        tableModel.setRowCount(0);
        
        for (Pustakawan pustakawan : pustakawanList) {
            Object[] row = {
                pustakawan.getIdPustakawan(),
                pustakawan.getNip(),
                pustakawan.getUser() != null ? pustakawan.getUser().getUsername() : "",
                pustakawan.getNamaLengkap(),
                pustakawan.getEmail(),
                pustakawan.getNoTelepon(),
                pustakawan.getAlamat(),
                pustakawan.getUser() != null && pustakawan.getUser().isActive() ? "Aktif" : "Tidak Aktif"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchPustakawan() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<Pustakawan> allPustakawan = pustakawanDAO.findAll();
            List<Pustakawan> filteredPustakawan = allPustakawan.stream()
                .filter(pustakawan -> 
                    pustakawan.getNip().toLowerCase().contains(keyword.toLowerCase()) ||
                    pustakawan.getNamaLengkap().toLowerCase().contains(keyword.toLowerCase())
                )
                .collect(java.util.stream.Collectors.toList());
            
            populateTable(filteredPustakawan);
            
            JOptionPane.showMessageDialog(this, 
                "Ditemukan " + filteredPustakawan.size() + " pustakawan dengan kata kunci: " + keyword,
                "Hasil Pencarian", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error searching data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetFilter() {
        txtSearch.setText("");
        loadData();
    }
    
    private void startAddMode() {
        isEditing = false;
        selectedPustakawanId = 0;
        clearForm();
        setFormEnabled(true);
        btnSave.setEnabled(true);
        btnCancel.setEnabled(true);
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        txtNip.requestFocus();
    }
    
    private void startEditMode() {
        int selectedRow = tablePustakawan.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diedit!");
            return;
        }
        
        selectedPustakawanId = (int) tableModel.getValueAt(selectedRow, 0);
        isEditing = true;
        
        try {
            Pustakawan pustakawan = pustakawanDAO.findById(selectedPustakawanId);
            if (pustakawan != null) {
                populateForm(pustakawan);
                setFormEnabled(true);
                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
                btnAdd.setEnabled(false);
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
                
                // Don't allow editing NIP and Username
                txtNip.setEnabled(false);
                txtUsername.setEnabled(false);
                // Password is optional for edit
                txtPassword.setText("");
                txtNama.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deletePustakawan() {
        int selectedRow = tablePustakawan.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
            return;
        }
        
        String nama = (String) tableModel.getValueAt(selectedRow, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus data pustakawan: " + nama + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                pustakawanDAO.delete(id);
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting data: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void savePustakawan() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Pustakawan pustakawan = new Pustakawan();
            
            // Create or get user
            User user;
            if (isEditing) {
                Pustakawan existingPustakawan = pustakawanDAO.findById(selectedPustakawanId);
                user = existingPustakawan.getUser();
                pustakawan.setIdPustakawan(selectedPustakawanId);
                
                // Update password only if provided
                String password = new String(txtPassword.getPassword()).trim();
                if (!password.isEmpty()) {
                    user.setPassword(password);
                    // Save user with new password first
                    userDAO.save(user);
                }
            } else {
                user = new User();
                Role pustakawanRole = roleDAO.findByName("PUSTAKAWAN");
                user.setRole(pustakawanRole);
                user.setUsername(txtUsername.getText().trim());
                user.setNip(txtNip.getText().trim());
                
                String password = new String(txtPassword.getPassword()).trim();
                user.setPassword(password);
            }
            
            user.setActive(cbStatusAktif.isSelected());
            pustakawan.setUser(user);
            
            // Set pustakawan data - ONLY FIELDS THAT ARE INPUTTED
            pustakawan.setNip(txtNip.getText().trim());
            pustakawan.setNamaLengkap(txtNama.getText().trim());
            pustakawan.setEmail(txtEmail.getText().trim());
            pustakawan.setNoTelepon(txtTelepon.getText().trim());
            pustakawan.setAlamat(txtAlamat.getText().trim());
            
            // REMOVE tanggal_mulai_kerja logic
            
            pustakawanDAO.save(pustakawan);
            
            JOptionPane.showMessageDialog(this, 
                isEditing ? "Data berhasil diperbarui!" : "Data berhasil ditambahkan!");
            
            cancelForm();
            loadData();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        if (txtNip.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIP tidak boleh kosong!");
            txtNip.requestFocus();
            return false;
        }
        
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap tidak boleh kosong!");
            txtNama.requestFocus();
            return false;
        }
        
        if (!isEditing) {
            if (txtUsername.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!");
                txtUsername.requestFocus();
                return false;
            }
            
            if (new String(txtPassword.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!");
                txtPassword.requestFocus();
                return false;
            }
        }
        
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid!");
            txtEmail.requestFocus();
            return false;
        }
        
        // Validate NIP uniqueness
        try {
            if (pustakawanDAO.isNipExists(txtNip.getText().trim(), selectedPustakawanId)) {
                JOptionPane.showMessageDialog(this, "NIP sudah terdaftar!");
                txtNip.requestFocus();
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error validating NIP: " + e.getMessage());
            return false;
        }
        
        // Validate username uniqueness
        if (!isEditing) {
            try {
                if (userDAO.isUsernameExists(txtUsername.getText().trim(), 0)) {
                    JOptionPane.showMessageDialog(this, "Username sudah terdaftar!");
                    txtUsername.requestFocus();
                    return false;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error validating username: " + e.getMessage());
                return false;
            }
        }
        
        return true;
    }
    
    private void populateForm(Pustakawan pustakawan) {
        txtNip.setText(pustakawan.getNip());
        txtNama.setText(pustakawan.getNamaLengkap());
        txtEmail.setText(pustakawan.getEmail() != null ? pustakawan.getEmail() : "");
        txtTelepon.setText(pustakawan.getNoTelepon() != null ? pustakawan.getNoTelepon() : "");
        txtAlamat.setText(pustakawan.getAlamat() != null ? pustakawan.getAlamat() : "");
        
        if (pustakawan.getUser() != null) {
            txtUsername.setText(pustakawan.getUser().getUsername());
            cbStatusAktif.setSelected(pustakawan.getUser().isActive());
        }
    }
    
    private void clearForm() {
        txtNip.setText("");
        txtNama.setText("");
        txtEmail.setText("");
        txtTelepon.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtAlamat.setText("");
        cbStatusAktif.setSelected(true);
    }
    
    private void setFormEnabled(boolean enabled) {
        txtNip.setEnabled(enabled);
        txtNama.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        txtTelepon.setEnabled(enabled);
        txtUsername.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
        txtAlamat.setEnabled(enabled);
        // REMOVE txtTanggalMulai.setEnabled(enabled);
    }
    
    private void cancelForm() {
        isEditing = false;
        selectedPustakawanId = 0;
        clearForm();
        setFormEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        
        // Re-enable NIP and Username fields for next add operation
        txtNip.setEnabled(false);
        txtUsername.setEnabled(false);
    }
    
    // Remove the setModal method - no longer needed
    // JDialog is inherently modal when constructed with modal=true
}