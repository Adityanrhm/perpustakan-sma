package com.perpustakaan.view.admin;

import com.perpustakaan.dao.SiswaDAO;
import com.perpustakaan.dao.UserDAO;
import com.perpustakaan.dao.RoleDAO;
import com.perpustakaan.dao.KelasDAO;
import com.perpustakaan.model.Siswa;
import com.perpustakaan.model.User;
import com.perpustakaan.model.Role;
import com.perpustakaan.model.Kelas;

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
 * Frame untuk mengelola data siswa
 */
public class SiswaManagementFrame extends JDialog {
    private SiswaDAO siswaDAO;
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private KelasDAO kelasDAO;
    
    // Components
    private JTable tableSiswa;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    // Form components - REVISED for new kelas structure
    private JTextField txtNis, txtNama, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbJenisKelamin;
    private JComboBox<String> cbTingkat;
    private JComboBox<String> cbJurusan;
    private JComboBox<Integer> cbRombel;
    private JCheckBox cbStatusAktif;
    private JButton btnAdd, btnEdit, btnDelete, btnSave, btnCancel, btnRefresh;
    
    // Filter and Search components - REVISED
    private JComboBox<String> cbFilterTingkat, cbFilterJurusan;
    private JComboBox<Integer> cbFilterRombel;
    private JTextField txtSearch;
    private JButton btnSearch, btnResetFilter;
    
    // Form panel
    private JPanel formPanel;
    private boolean isEditing = false;
    private int selectedSiswaId = 0;
    
    public SiswaManagementFrame(Frame parent) {
        super(parent, "Kelola Data Siswa", true); // true = modal
        siswaDAO = new SiswaDAO();
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();
        kelasDAO = new KelasDAO();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Table - REVISED columns
        String[] columns = {"ID", "NIS", "Nama Lengkap", "Kelas", "Jenis Kelamin", "Username", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSiswa = new JTable(tableModel);
        tableSiswa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSiswa.getColumnModel().getColumn(0).setMaxWidth(50);
        scrollPane = new JScrollPane(tableSiswa);
        
        // Form components - REVISED for new structure
        txtNis = new JTextField(20);
        txtNama = new JTextField(20);
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        
        cbJenisKelamin = new JComboBox<>(new String[]{"L", "P"});
        cbTingkat = new JComboBox<>(new String[]{"X", "XI", "XII"});
        cbJurusan = new JComboBox<>(new String[]{"IPA", "IPS", "BAHASA"});
        cbRombel = new JComboBox<>(new Integer[]{1, 2, 3});
        cbStatusAktif = new JCheckBox("Status Aktif", true);
        
        // Filter components - REVISED
        cbFilterTingkat = new JComboBox<>(new String[]{"Semua", "X", "XI", "XII"});
        cbFilterJurusan = new JComboBox<>(new String[]{"Semua", "IPA", "IPS", "BAHASA"});
        cbFilterRombel = new JComboBox<>(new Integer[]{null, 1, 2, 3});
        cbFilterRombel.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Semua");
                }
                return this;
            }
        });
        
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Cari berdasarkan NIS atau Nama");
        
        // Buttons
        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");
        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");
        btnRefresh = new JButton("Refresh");
        btnSearch = new JButton("Cari");
        btnResetFilter = new JButton("Reset Filter");
        
        // Button colors
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
        
        // Top panel with REVISED filter - tingkat, jurusan, rombel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tingkat:"));
        topPanel.add(cbFilterTingkat);
        topPanel.add(new JLabel("Jurusan:"));
        topPanel.add(cbFilterJurusan);
        topPanel.add(new JLabel("Rombel:"));
        topPanel.add(cbFilterRombel);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(new JLabel("Cari:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnResetFilter);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(btnAdd);
        topPanel.add(btnEdit);
        topPanel.add(btnDelete);
        topPanel.add(btnRefresh);
        
        // Form panel - REVISED with rombel
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Data Siswa"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0: NIS and Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("NIS:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtNis, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtUsername, gbc);
        
        // Row 1: Nama and Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtNama, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtPassword, gbc);
        
        // Row 2: Tingkat and Jurusan
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Tingkat:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cbTingkat, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Jurusan:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cbJurusan, gbc);
        
        // Row 3: Rombel and Jenis Kelamin
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Rombel:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cbRombel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cbJenisKelamin, gbc);
        
        // Row 4: Status Aktif
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cbStatusAktif, gbc);
        
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
        tableSiswa.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableSiswa.getSelectedRow();
                btnEdit.setEnabled(selectedRow >= 0);
                btnDelete.setEnabled(selectedRow >= 0);
            }
        });
        
        btnAdd.addActionListener(e -> startAddMode());
        btnEdit.addActionListener(e -> startEditMode());
        btnDelete.addActionListener(e -> deleteSiswa());
        btnSave.addActionListener(e -> saveSiswa());
        btnCancel.addActionListener(e -> cancelForm());
        btnRefresh.addActionListener(e -> {
            loadData();
        });
        
        // Search and Filter - REVISED
        btnSearch.addActionListener(e -> searchSiswa());
        btnResetFilter.addActionListener(e -> resetFilter());
        cbFilterTingkat.addActionListener(e -> applyFilter());
        cbFilterJurusan.addActionListener(e -> applyFilter());
        cbFilterRombel.addActionListener(e -> applyFilter());
        
        // Enter key for search
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    searchSiswa();
                }
            }
        });
    }
    
    
    private void loadData() {
        try {
            List<Siswa> siswaList = siswaDAO.findAll();
            populateTable(siswaList);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateTable(List<Siswa> siswaList) {
        tableModel.setRowCount(0);
        
        for (Siswa siswa : siswaList) {
            Object[] row = {
                siswa.getIdSiswa(),
                siswa.getNis(),
                siswa.getNamaLengkap(),
                siswa.getKelas() != null ? siswa.getKelas().getNamaKelas() : "", // Will show X-IPA-1 format
                "L".equals(siswa.getJenisKelamin()) ? "Laki-laki" : "Perempuan",
                siswa.getUser() != null ? siswa.getUser().getUsername() : "",
                siswa.isStatusAktif() ? "Aktif" : "Tidak Aktif"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchSiswa() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<Siswa> allSiswa = siswaDAO.findAll();
            List<Siswa> filteredSiswa = allSiswa.stream()
                .filter(siswa -> 
                    siswa.getNis().toLowerCase().contains(keyword.toLowerCase()) ||
                    siswa.getNamaLengkap().toLowerCase().contains(keyword.toLowerCase())
                )
                .collect(java.util.stream.Collectors.toList());
            
            populateTable(filteredSiswa);
            
            JOptionPane.showMessageDialog(this, 
                "Ditemukan " + filteredSiswa.size() + " siswa dengan kata kunci: " + keyword,
                "Hasil Pencarian", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error searching data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyFilter() {
        String selectedTingkat = (String) cbFilterTingkat.getSelectedItem();
        String selectedJurusan = (String) cbFilterJurusan.getSelectedItem();
        Integer selectedRombel = (Integer) cbFilterRombel.getSelectedItem();
        
        try {
            List<Siswa> allSiswa = siswaDAO.findAll();
            List<Siswa> filteredSiswa = allSiswa.stream()
                .filter(siswa -> {
                    if (siswa.getKelas() == null) return false;
                    
                    boolean tingkatMatch = "Semua".equals(selectedTingkat) || 
                                         siswa.getKelas().getTingkat().equals(selectedTingkat);
                    
                    boolean jurusanMatch = "Semua".equals(selectedJurusan) || 
                                         siswa.getKelas().getJurusan().equals(selectedJurusan);
                    
                    boolean rombelMatch = selectedRombel == null || 
                                        siswa.getKelas().getRombel() == selectedRombel.intValue();
                    
                    return tingkatMatch && jurusanMatch && rombelMatch;
                })
                .collect(java.util.stream.Collectors.toList());
            
            populateTable(filteredSiswa);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error filtering data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetFilter() {
        cbFilterTingkat.setSelectedIndex(0); // "Semua"
        cbFilterJurusan.setSelectedIndex(0); // "Semua"
        cbFilterRombel.setSelectedIndex(0); // null/"Semua"
        txtSearch.setText("");
        loadData();
    }
    
    private void startAddMode() {
        isEditing = false;
        selectedSiswaId = 0;
        clearForm();
        setFormEnabled(true);
        btnSave.setEnabled(true);
        btnCancel.setEnabled(true);
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        txtNis.requestFocus();
    }
    
    private void startEditMode() {
        int selectedRow = tableSiswa.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diedit!");
            return;
        }
        
        selectedSiswaId = (int) tableModel.getValueAt(selectedRow, 0);
        isEditing = true;
        
        try {
            Siswa siswa = siswaDAO.findById(selectedSiswaId);
            if (siswa != null) {
                populateForm(siswa);
                setFormEnabled(true);
                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
                btnAdd.setEnabled(false);
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
                
                // Don't allow editing NIS and Username
                txtNis.setEnabled(false);
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
    
    private void deleteSiswa() {
        int selectedRow = tableSiswa.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
            return;
        }
        
        String nama = (String) tableModel.getValueAt(selectedRow, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus data siswa: " + nama + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                siswaDAO.delete(id);
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
    
    private void saveSiswa() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Siswa siswa = new Siswa();
            
            // Create or get user
            User user;
            if (isEditing) {
                Siswa existingSiswa = siswaDAO.findById(selectedSiswaId);
                user = existingSiswa.getUser();
                siswa.setIdSiswa(selectedSiswaId);
                
                // Update password only if provided
                String password = new String(txtPassword.getPassword()).trim();
                if (!password.isEmpty()) {
                    user.setPassword(password);
                    // Save user with new password first
                    userDAO.save(user);
                }
            } else {
                user = new User();
                Role siswaRole = roleDAO.findByName("SISWA");
                user.setRole(siswaRole);
                user.setUsername(txtUsername.getText().trim());
                user.setNis(txtNis.getText().trim());
                
                String password = new String(txtPassword.getPassword()).trim();
                user.setPassword(password);
            }
            
            user.setActive(cbStatusAktif.isSelected());
            siswa.setUser(user);
            
            // Set siswa data - ONLY FIELDS THAT ARE INPUTTED
            siswa.setNis(txtNis.getText().trim());
            siswa.setNamaLengkap(txtNama.getText().trim());
            siswa.setJenisKelamin((String) cbJenisKelamin.getSelectedItem());
            siswa.setStatusAktif(cbStatusAktif.isSelected());
            
            // Find kelas based on tingkat, jurusan, and rombel - REVISED
            String tingkat = (String) cbTingkat.getSelectedItem();
            String jurusan = (String) cbJurusan.getSelectedItem();
            Integer rombel = (Integer) cbRombel.getSelectedItem();
            
            Kelas selectedKelas = kelasDAO.findByTingkatJurusanRombel(tingkat, jurusan, rombel);
            
            if (selectedKelas == null) {
                JOptionPane.showMessageDialog(this, 
                    "Kelas tidak ditemukan untuk " + tingkat + "-" + jurusan + "-" + rombel,
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            siswa.setKelas(selectedKelas);
            
            // REMOVED fields: tempat_lahir, tanggal_lahir, alamat, no_telepon, nama_wali, no_telepon_wali
            
            siswaDAO.save(siswa);
            
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
        if (txtNis.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIS tidak boleh kosong!");
            txtNis.requestFocus();
            return false;
        }
        
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap tidak boleh kosong!");
            txtNama.requestFocus();
            return false;
        }
        
        if (cbTingkat.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Tingkat harus dipilih!");
            cbTingkat.requestFocus();
            return false;
        }
        
        if (cbJurusan.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Jurusan harus dipilih!");
            cbJurusan.requestFocus();
            return false;
        }
        
        if (cbRombel.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Rombel harus dipilih!");
            cbRombel.requestFocus();
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
        
        // Validate NIS uniqueness
        try {
            if (siswaDAO.isNisExists(txtNis.getText().trim(), selectedSiswaId)) {
                JOptionPane.showMessageDialog(this, "NIS sudah terdaftar!");
                txtNis.requestFocus();
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error validating NIS: " + e.getMessage());
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
    
    private void populateForm(Siswa siswa) {
        txtNis.setText(siswa.getNis());
        txtNama.setText(siswa.getNamaLengkap());
        cbJenisKelamin.setSelectedItem(siswa.getJenisKelamin());
        cbStatusAktif.setSelected(siswa.isStatusAktif());
        
        // Set tingkat, jurusan, and rombel based on kelas - REVISED
        if (siswa.getKelas() != null) {
            cbTingkat.setSelectedItem(siswa.getKelas().getTingkat());
            cbJurusan.setSelectedItem(siswa.getKelas().getJurusan());
            cbRombel.setSelectedItem(siswa.getKelas().getRombel());
        }
        
        if (siswa.getUser() != null) {
            txtUsername.setText(siswa.getUser().getUsername());
        }
    }
    
    private void clearForm() {
        txtNis.setText("");
        txtNama.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cbJenisKelamin.setSelectedIndex(0);
        cbTingkat.setSelectedIndex(-1);
        cbJurusan.setSelectedIndex(-1);
        cbRombel.setSelectedIndex(-1);
        cbStatusAktif.setSelected(true);
    }
    
    private void setFormEnabled(boolean enabled) {
        txtNis.setEnabled(enabled);
        txtNama.setEnabled(enabled);
        txtUsername.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
        cbJenisKelamin.setEnabled(enabled);
        cbTingkat.setEnabled(enabled);
        cbJurusan.setEnabled(enabled);
        cbRombel.setEnabled(enabled);
        cbStatusAktif.setEnabled(enabled);
    }
    
    private void cancelForm() {
        isEditing = false;
        selectedSiswaId = 0;
        clearForm();
        setFormEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        
        // Re-enable NIS and Username fields for next add operation
        txtNis.setEnabled(false);
        txtUsername.setEnabled(false);
    }
    
    // Remove the setModal method - no longer needed
    // JDialog is inherently modal when constructed with modal=true
}