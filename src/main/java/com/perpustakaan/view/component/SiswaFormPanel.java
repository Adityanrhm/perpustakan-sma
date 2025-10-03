package com.perpustakaan.view.component;

import com.perpustakaan.model.Siswa;
import com.perpustakaan.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Komponen form reusable untuk data siswa
 * Dapat digunakan oleh Admin dan Pustakawan
 */
public class SiswaFormPanel extends JPanel {
    // Form components
    private JTextField txtNis, txtNama, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbJenisKelamin;
    private JComboBox<String> cbTingkat;
    private JComboBox<String> cbJurusan;
    private JComboBox<Integer> cbRombel;
    private JCheckBox cbStatusAktif;
    
    // Action buttons
    private JButton btnSave, btnCancel;
    
    // State
    private boolean isEditMode = false;
    
    public SiswaFormPanel() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
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
        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");
        
        // Button styling
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0: NIS and Username
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("NIS:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtNis, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtUsername, gbc);
        
        // Row 1: Nama and Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtNama, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtPassword, gbc);
        
        // Row 2: Tingkat and Jurusan
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Tingkat:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cbTingkat, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Jurusan:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cbJurusan, gbc);
        
        // Row 3: Rombel and Jenis Kelamin
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Rombel:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cbRombel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cbJenisKelamin, gbc);
        
        // Row 4: Status Aktif
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cbStatusAktif, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }
    
    // Public methods for form operations
    public void populateForm(Siswa siswa) {
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
    
    public void clearForm() {
        txtNis.setText("");
        txtNama.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cbJenisKelamin.setSelectedIndex(0);
        cbTingkat.setSelectedIndex(-1);
        cbJurusan.setSelectedIndex(-1);
        cbRombel.setSelectedIndex(-1);
        cbStatusAktif.setSelected(true);
        isEditMode = false;
    }
    
    public void setFormEnabled(boolean enabled) {
        txtNis.setEnabled(enabled && !isEditMode);
        txtNama.setEnabled(enabled);
        txtUsername.setEnabled(enabled && !isEditMode);
        txtPassword.setEnabled(enabled);
        cbJenisKelamin.setEnabled(enabled);
        cbTingkat.setEnabled(enabled);
        cbJurusan.setEnabled(enabled);
        cbRombel.setEnabled(enabled);
        cbStatusAktif.setEnabled(enabled);
        btnSave.setEnabled(enabled);
        btnCancel.setEnabled(enabled);
    }
    
    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        txtNis.setEnabled(!editMode);
        txtUsername.setEnabled(!editMode);
    }
    
    public boolean validateForm() {
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
        
        if (!isEditMode) {
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
    
    // Getter methods for form data
    public String getNis() {
        return txtNis.getText().trim();
    }
    
    public String getNamaLengkap() {
        return txtNama.getText().trim();
    }
    
    public String getUsername() {
        return txtUsername.getText().trim();
    }
    
    public String getPassword() {
        return new String(txtPassword.getPassword()).trim();
    }
    
    public String getJenisKelamin() {
        return (String) cbJenisKelamin.getSelectedItem();
    }
    
    public String getTingkat() {
        return (String) cbTingkat.getSelectedItem();
    }
    
    public String getJurusan() {
        return (String) cbJurusan.getSelectedItem();
    }
    
    public int getRombel() {
        return (Integer) cbRombel.getSelectedItem();
    }
    
    public boolean isStatusAktif() {
        return cbStatusAktif.isSelected();
    }
    
    // Event listener setters
    public void setSaveButtonListener(ActionListener listener) {
        btnSave.addActionListener(listener);
    }
    
    public void setCancelButtonListener(ActionListener listener) {
        btnCancel.addActionListener(listener);
    }
}