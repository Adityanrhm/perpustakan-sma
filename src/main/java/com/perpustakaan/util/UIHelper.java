package com.perpustakaan.util;

import javax.swing.*;
import java.awt.Component;

/**
 * Utility class untuk UI operations
 */
public class UIHelper {
    
    /**
     * Show error message dialog
     */
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show success message dialog
     */
    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show warning message dialog
     */
    public static void showWarningMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmDialog(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent, 
            message, 
            "Konfirmasi", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Show input dialog
     */
    public static String showInputDialog(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
    }
    
    /**
     * Clear all text fields in a container
     */
    public static void clearTextFields(java.awt.Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof JPasswordField) {
                ((JPasswordField) component).setText("");
            } else if (component instanceof JTextArea) {
                ((JTextArea) component).setText("");
            } else if (component instanceof java.awt.Container) {
                clearTextFields((java.awt.Container) component);
            }
        }
    }
    
    /**
     * Enable/disable all input components in a container
     */
    public static void setContainerEnabled(java.awt.Container container, boolean enabled) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField || 
                component instanceof JPasswordField ||
                component instanceof JTextArea ||
                component instanceof JComboBox ||
                component instanceof JCheckBox ||
                component instanceof JRadioButton) {
                component.setEnabled(enabled);
            } else if (component instanceof java.awt.Container) {
                setContainerEnabled((java.awt.Container) component, enabled);
            }
        }
    }
    
    /**
     * Center window on screen
     */
    public static void centerWindow(java.awt.Window window) {
        window.setLocationRelativeTo(null);
    }
    
    /**
     * Set look and feel to system default
     */
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }
    }
    
    /**
     * Format currency value
     */
    public static String formatCurrency(double value) {
        return String.format("Rp %.0f", value);
    }
    
    /**
     * Format percentage value
     */
    public static String formatPercentage(double value) {
        return String.format("%.1f%%", value * 100);
    }
    
    /**
     * Get file extension
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}