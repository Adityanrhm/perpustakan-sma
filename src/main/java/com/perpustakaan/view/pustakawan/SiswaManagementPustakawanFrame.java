package com.perpustakaan.view.pustakawan;

import com.perpustakaan.dao.SiswaDAO;
import com.perpustakaan.dao.UserDAO;
import com.perpustakaan.dao.RoleDAO;
import com.perpustakaan.dao.KelasDAO;
import com.perpustakaan.model.Siswa;
import com.perpustakaan.model.User;
import com.perpustakaan.model.Role;
import com.perpustakaan.model.Kelas;
import com.perpustakaan.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Frame terpadu untuk mengelola data siswa - Role Pustakawan
 * Menggabungkan CRUD dan Search dalam satu interface
 */
public class SiswaManagementPustakawanFrame extends JDialog {
    private SiswaDAO siswaDAO;
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private KelasDAO kelasDAO;
    
    // Components
    private JTable tableSiswa;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    // Search Panel
    private JTextField txtSearch;
    private JComboBox<String> cbSearchBy;
    private JComboBox<String> cbFilterTingkat, cbFilterJurusan;
    private JComboBox<Integer> cbFilterRombel;
    private JComboBox<String> cbFilterStatus;
    private JButton btnSearch, btnResetFilter;
    
    // Form components
    private JTextField txtNis, txtNama, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbJenisKelamin;
    private JComboBox<String> cbTingkat;
    private JComboBox<String> cbJurusan;
    private JComboBox<Integer> cbRombel;
    private JCheckBox cbStatusAktif;
    private JButton btnAdd, btnEdit, btnDelete, btnSave, btnCancel, btnRefresh;
    
    // State
    private boolean isEditing = false;
    private int selectedSiswaId = 0;
    
    public SiswaManagementPustakawanFrame(Frame parent) {
        super(parent, "Kelola Data Siswa", true);
        
        siswaDAO = new SiswaDAO();
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();
        kelasDAO = new KelasDAO();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1300, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Search Panel Components
        txtSearch = new JTextField(20);
        cbSearchBy = new JComboBox<>(new String[]{
            "Semua", "NIS", "Nama", "Username"
        });
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
        cbFilterStatus = new JComboBox<>(new String[]{
            "Semua Status", "Aktif", "Tidak Aktif"
        });
        
        btnSearch = new JButton("Cari");
        btnResetFilter = new JButton("Reset Filter");
        
        // Table
        String[] columns = {"ID", "NIS", "Username", "Nama Lengkap", "tingkat", "Jurusan", "Rombel", "Jenis Kelamin", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSiswa = new JTable(tableModel);
        tableSiswa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSiswa.getColumnModel().getColumn(0).setMaxWidth(50);
        tableSiswa.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableSiswa.getColumnModel().getColumn(2).setPreferredWidth(180);
        scrollPane = new JScrollPane(tableSiswa);
        
        // Form components
        txtNis = new JTextField(20);
        txtNama = new JTextField(20);
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        
        cbJenisKelamin = new JComboBox<>(new String[]{"L", "P"});
        cbTingkat = new JComboBox<>(new String[]{"X", "XI", "XII"});
        cbJurusan = new JComboBox<>(new String[]{"IPA", "IPS", "BAHASA"});
        cbRombel = new JComboBox<>(new Integer[]{1, 2, 3});
        cbStatusAktif = new JCheckBox("Status Aktif", true);
        
        // Action buttons
        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");
        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");
        btnRefresh = new JButton("Refresh");
        
        // Button styling
        setupButtonColors();
        
        // Initial state
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        setFormEnabled(false);
    }
    
    private void setupButtonColors() {
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
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Search Panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Pencarian & Filter"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1 - Search
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Kata Kunci:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(txtSearch, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(new JLabel("Cari di:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(cbSearchBy, gbc);
        
        // Row 2 - Filters
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(new JLabel("Tingkat:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(cbFilterTingkat, gbc);
        
         gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(cbFilterStatus, gbc);
        
        // Row 3 - More Filters
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(new JLabel("Rombel:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(cbFilterRombel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(new JLabel("Jurusan:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(cbFilterJurusan, gbc);
        
        // Button row
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 3;
        JPanel searchButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchButtonPanel.add(btnSearch);
        searchButtonPanel.add(btnResetFilter);
        searchPanel.add(searchButtonPanel, gbc);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        
        // Top panel combining search and actions
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        formPanel.setVisible(false); // Initially hidden
        
        // Main layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Data Siswa"));
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0: NIS and Username
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("NIS:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(txtNis, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(txtUsername, gbc);
        
        // Row 1: Nama and Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(txtNama, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(txtPassword, gbc);
        
        // Row 2: Tingkat and Jurusan
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Tingkat:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(cbTingkat, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Jurusan:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(cbJurusan, gbc);
        
        // Row 3: Rombel and Jenis Kelamin
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Rombel:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(cbRombel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(cbJenisKelamin, gbc);
        
        // Row 4: Status Aktif
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(cbStatusAktif, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        formPanel.add(contentPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return formPanel;
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
        
        // Search and Filter
        btnSearch.addActionListener(e -> performSearch());
        btnResetFilter.addActionListener(e -> resetFilter());
        cbFilterTingkat.addActionListener(e -> performSearch());
        cbFilterJurusan.addActionListener(e -> performSearch());
        cbFilterRombel.addActionListener(e -> performSearch());
        cbFilterStatus.addActionListener(e -> performSearch());
        
        // Action buttons
        btnAdd.addActionListener(e -> startAddMode());
        btnEdit.addActionListener(e -> startEditMode());
        btnDelete.addActionListener(e -> deleteSiswa());
        btnSave.addActionListener(e -> saveSiswa());
        btnCancel.addActionListener(e -> cancelForm());
        btnRefresh.addActionListener(e -> loadData());
        
        // Enter key for search
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
    }
    
    private void loadData() {
        try {
            List<Siswa> siswaList = siswaDAO.findAll();
            populateTable(siswaList);
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading data: " + e.getMessage());
        }
    }
    
    private void populateTable(List<Siswa> siswaList) {
        tableModel.setRowCount(0);
        
        for (Siswa siswa : siswaList) {
            Object[] row = {
                siswa.getIdSiswa(),
                siswa.getNis(),
                siswa.getUser() != null ? siswa.getUser().getUsername() : "",
                siswa.getNamaLengkap(),
                siswa.getKelas() != null ? siswa.getKelas().getTingkat() : "",
                siswa.getKelas() != null ? siswa.getKelas().getJurusan() : "",
                siswa.getKelas() != null ? siswa.getKelas().getRombel() : "",
                
                "L".equals(siswa.getJenisKelamin()) ? "Laki-laki" : "Perempuan",
                siswa.isStatusAktif() ? "Aktif" : "Tidak Aktif"
            };
            tableModel.addRow(row);
        }
    }
    
    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        String searchBy = (String) cbSearchBy.getSelectedItem();
        String tingkat = (String) cbFilterTingkat.getSelectedItem();
        String jurusan = (String) cbFilterJurusan.getSelectedItem();
        Integer rombel = (Integer) cbFilterRombel.getSelectedItem();
        String status = (String) cbFilterStatus.getSelectedItem();
        
        try {
            List<Siswa> siswaList = siswaDAO.findAll();
            
            // Apply keyword filter
            if (!keyword.isEmpty()) {
                siswaList = siswaList.stream()
                    .filter(siswa -> matchesKeyword(siswa, keyword, searchBy))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Apply tingkat filter
            if (!"Semua".equals(tingkat)) {
                siswaList = siswaList.stream()
                    .filter(siswa -> siswa.getKelas() != null && siswa.getKelas().getTingkat().equals(tingkat))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Apply jurusan filter
            if (!"Semua".equals(jurusan)) {
                siswaList = siswaList.stream()
                    .filter(siswa -> siswa.getKelas() != null && siswa.getKelas().getJurusan().equals(jurusan))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Apply rombel filter
            if (rombel != null) {
                siswaList = siswaList.stream()
                    .filter(siswa -> siswa.getKelas() != null && siswa.getKelas().getRombel() == rombel.intValue())
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Apply status filter
            if (!"Semua Status".equals(status)) {
                siswaList = siswaList.stream()
                    .filter(siswa -> matchesStatus(siswa, status))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            populateTable(siswaList);
            
            
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error performing search: " + e.getMessage());
        }
    }
    
    private boolean matchesKeyword(Siswa siswa, String keyword, String searchBy) {
        String lowerKeyword = keyword.toLowerCase();
        
        switch (searchBy) {
            case "NIS":
                return siswa.getNis().toLowerCase().contains(lowerKeyword);
            case "Nama":
                return siswa.getNamaLengkap().toLowerCase().contains(lowerKeyword);
            case "Username":
                return siswa.getUser() != null && 
                       siswa.getUser().getUsername().toLowerCase().contains(lowerKeyword);
            case "Semua":
            default:
                return siswa.getNis().toLowerCase().contains(lowerKeyword) ||
                       siswa.getNamaLengkap().toLowerCase().contains(lowerKeyword) ||
                       (siswa.getUser() != null && siswa.getUser().getUsername().toLowerCase().contains(lowerKeyword));
        }
    }
    
    private boolean matchesStatus(Siswa siswa, String status) {
        switch (status) {
            case "Aktif":
                return siswa.isStatusAktif();
            case "Tidak Aktif":
                return !siswa.isStatusAktif();
            default:
                return true;
        }
    }
    
    private void resetFilter() {
        txtSearch.setText("");
        cbSearchBy.setSelectedIndex(0);
        cbFilterTingkat.setSelectedIndex(0);
        cbFilterJurusan.setSelectedIndex(0);
        cbFilterRombel.setSelectedIndex(0);
        cbFilterStatus.setSelectedIndex(0);
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
        
        // Show form panel
        getContentPane().getComponent(2).setVisible(true);
        revalidate();
        repaint();
        
        txtNis.requestFocus();
    }
    
    private void startEditMode() {
        int selectedRow = tableSiswa.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showWarningMessage(this, "Pilih data yang akan diedit!");
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
                
                // Disable NIS and Username editing
                txtNis.setEnabled(false);
                txtUsername.setEnabled(false);
                txtPassword.setText("");
                
                // Show form panel
                getContentPane().getComponent(2).setVisible(true);
                revalidate();
                repaint();
                
                txtNama.requestFocus();
            }
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error loading data: " + e.getMessage());
        }
    }
    
    private void deleteSiswa() {
        int selectedRow = tableSiswa.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showWarningMessage(this, "Pilih data yang akan dihapus!");
            return;
        }
        
        String nama = (String) tableModel.getValueAt(selectedRow, 2);
        if (UIHelper.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus data siswa: " + nama + "?")) {
            
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                siswaDAO.delete(id);
                UIHelper.showSuccessMessage(this, "Data berhasil dihapus!");
                loadData();
            } catch (SQLException e) {
                UIHelper.showErrorMessage(this, "Error deleting data: " + e.getMessage());
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
                    userDAO.save(user);
                }
            } else {
                user = new User();
                Role siswaRole = roleDAO.findByName("SISWA");
                user.setRole(siswaRole);
                user.setUsername(txtUsername.getText().trim());
                user.setNis(txtNis.getText().trim());
                user.setPassword(new String(txtPassword.getPassword()).trim());
            }
            
            user.setActive(cbStatusAktif.isSelected());
            siswa.setUser(user);
            
            // Set siswa data
            siswa.setNis(txtNis.getText().trim());
            siswa.setNamaLengkap(txtNama.getText().trim());
            siswa.setJenisKelamin((String) cbJenisKelamin.getSelectedItem());
            siswa.setStatusAktif(cbStatusAktif.isSelected());
            
            // Find kelas
            String tingkat = (String) cbTingkat.getSelectedItem();
            String jurusan = (String) cbJurusan.getSelectedItem();
            Integer rombel = (Integer) cbRombel.getSelectedItem();
            
            Kelas selectedKelas = kelasDAO.findByTingkatJurusanRombel(tingkat, jurusan, rombel);
            
            if (selectedKelas == null) {
                UIHelper.showErrorMessage(this, 
                    "Kelas tidak ditemukan untuk " + tingkat + "-" + jurusan + "-" + rombel);
                return;
            }
            
            siswa.setKelas(selectedKelas);
            
            // Validate uniqueness
            if (siswaDAO.isNisExists(siswa.getNis(), selectedSiswaId)) {
                UIHelper.showWarningMessage(this, "NIS sudah terdaftar!");
                txtNis.requestFocus();
                return;
            }
            
            if (!isEditing && userDAO.isUsernameExists(user.getUsername(), 0)) {
                UIHelper.showWarningMessage(this, "Username sudah terdaftar!");
                txtUsername.requestFocus();
                return;
            }
            
            siswaDAO.save(siswa);
            
            UIHelper.showSuccessMessage(this, 
                isEditing ? "Data berhasil diperbarui!" : "Data berhasil ditambahkan!");
            
            cancelForm();
            loadData();
            
        } catch (SQLException e) {
            UIHelper.showErrorMessage(this, "Error saving data: " + e.getMessage());
        }
    }
    
    private boolean validateForm() {
        if (txtNis.getText().trim().isEmpty()) {
            UIHelper.showWarningMessage(this, "NIS tidak boleh kosong!");
            txtNis.requestFocus();
            return false;
        }
        
        if (txtNama.getText().trim().isEmpty()) {
            UIHelper.showWarningMessage(this, "Nama lengkap tidak boleh kosong!");
            txtNama.requestFocus();
            return false;
        }
        
        if (cbTingkat.getSelectedItem() == null) {
            UIHelper.showWarningMessage(this, "Tingkat harus dipilih!");
            cbTingkat.requestFocus();
            return false;
        }
        
        if (cbJurusan.getSelectedItem() == null) {
            UIHelper.showWarningMessage(this, "Jurusan harus dipilih!");
            cbJurusan.requestFocus();
            return false;
        }
        
        if (cbRombel.getSelectedItem() == null) {
            UIHelper.showWarningMessage(this, "Rombel harus dipilih!");
            cbRombel.requestFocus();
            return false;
        }
        
        if (!isEditing) {
            if (txtUsername.getText().trim().isEmpty()) {
                UIHelper.showWarningMessage(this, "Username tidak boleh kosong!");
                txtUsername.requestFocus();
                return false;
            }
            
            if (new String(txtPassword.getPassword()).trim().isEmpty()) {
                UIHelper.showWarningMessage(this, "Password tidak boleh kosong!");
                txtPassword.requestFocus();
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
        
        // Set kelas components
        if (siswa.getKelas() != null) {
            cbTingkat.setSelectedItem(siswa.getKelas().getTingkat());
            cbJurusan.setSelectedItem(siswa.getKelas().getJurusan());
            cbRombel.setSelectedItem(siswa.getKelas().getRombel());
        }
        
        // Set user data
        if (siswa.getUser() != null) {
            txtUsername.setText(siswa.getUser().getUsername());
            cbStatusAktif.setSelected(siswa.getUser().isActive());
        }
        
        // Clear password for edit mode
        txtPassword.setText("");
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
        
        // Hide form panel
        getContentPane().getComponent(2).setVisible(false);
        revalidate();
        repaint();
    }
}