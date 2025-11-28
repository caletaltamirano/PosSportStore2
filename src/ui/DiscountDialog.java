package ui;

import possportstore.CurrentSale;
import possportstore.CurrentSale.CartItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Touch-friendly dialog for applying discounts.
 * Replaces standard input dialogs with large buttons and presets.
 */
public class DiscountDialog extends JDialog {

    private final CurrentSale currentSale;
    private final SalesView parentView;
    private final int selectedItemIndex; // -1 if no item selected

    private boolean applyToGlobal = true;
    private double currentPercent = 0.0;
    
    // UI Components
    private JLabel valueLabel;
    private JButton btnModeGlobal;
    private JButton btnModeItem;
    private JLabel targetLabel;

    public DiscountDialog(JFrame parent, SalesView parentView, CurrentSale currentSale, int selectedItemIndex) {
        super(parent, "Aplicar Descuento", true);
        this.parentView = parentView;
        this.currentSale = currentSale;
        this.selectedItemIndex = selectedItemIndex;

        // Default logic: If item selected, default to item discount, else global
        if (selectedItemIndex != -1) {
            applyToGlobal = false;
            currentPercent = currentSale.getItems()[selectedItemIndex].getDiscountPercent();
        } else {
            applyToGlobal = true;
            currentPercent = currentSale.getGlobalDiscountPercent();
        }

        setSize(600, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
        
        updateModeState(); // Refresh UI state based on initial mode
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // --- Mode Switcher (Global vs Item) ---
        JPanel switchPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        switchPanel.setBackground(Color.WHITE);

        btnModeGlobal = createModeButton("A TODA LA VENTA", true);
        btnModeItem = createModeButton("AL PRODUCTO", false);
        
        if (selectedItemIndex == -1) {
            btnModeItem.setEnabled(false); // Disable if no item selected in table
            btnModeItem.setText("AL PRODUCTO (Selecciona uno primero)");
        }

        switchPanel.add(btnModeGlobal);
        switchPanel.add(btnModeItem);
        
        // --- Target Info ---
        targetLabel = new JLabel("", SwingConstants.CENTER);
        targetLabel.setFont(new Font("Inter", Font.BOLD, 14));
        targetLabel.setForeground(Color.GRAY);

        panel.add(switchPanel);
        panel.add(targetLabel);
        
        return panel;
    }

    private JButton createModeButton(String text, boolean isGlobalMode) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 50));
        
        btn.addActionListener(e -> {
            applyToGlobal = isGlobalMode;
            // Reset percent when switching context to avoid accidents
            currentPercent = isGlobalMode ? currentSale.getGlobalDiscountPercent() 
                                          : currentSale.getItems()[selectedItemIndex].getDiscountPercent();
            updateModeState();
        });
        
        return btn;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 30, 20, 30));

        // --- Value Display ---
        valueLabel = new JLabel("0%", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 70));
        valueLabel.setForeground(MainFrame.COLOR_PRIMARY_BLUE);
        
        // --- Controls (+ / -) ---
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        controlsPanel.setBackground(Color.WHITE);
        
        JButton minusBtn = createCircleButton("-", new Color(240, 240, 240), Color.BLACK);
        JButton plusBtn = createCircleButton("+", MainFrame.COLOR_PRIMARY_BLUE, Color.WHITE);
        
        minusBtn.addActionListener(e -> adjustPercent(-0.01)); // -1%
        plusBtn.addActionListener(e -> adjustPercent(0.01));  // +1%

        controlsPanel.add(minusBtn);
        controlsPanel.add(valueLabel);
        controlsPanel.add(plusBtn);

        // --- Presets Grid ---
        JPanel presetsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        presetsPanel.setBackground(Color.WHITE);
        presetsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        int[] presets = {5, 10, 15, 20, 25, 50};
        for (int p : presets) {
            JButton btn = new JButton(p + "%");
            btn.setFont(new Font("Inter", Font.BOLD, 20));
            btn.setBackground(new Color(230, 240, 255));
            btn.setForeground(MainFrame.COLOR_PRIMARY_BLUE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.addActionListener(e -> setPercent(p / 100.0));
            presetsPanel.add(btn);
        }

        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(presetsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("CANCELAR");
        btnCancel.setFont(new Font("Inter", Font.BOLD, 16));
        btnCancel.setBackground(new Color(240, 240, 240));
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setPreferredSize(new Dimension(0, 60));
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> dispose());

        JButton btnApply = new JButton("APLICAR DESCUENTO");
        btnApply.setFont(new Font("Inter", Font.BOLD, 16));
        btnApply.setBackground(new Color(40, 167, 69)); // Green
        btnApply.setForeground(Color.WHITE);
        btnApply.setBorderPainted(false);
        btnApply.addActionListener(e -> applyAction());

        panel.add(btnCancel);
        panel.add(btnApply);
        return panel;
    }

    // --- Logic ---

    private void updateModeState() {
        // Update Buttons Visual State
        if (applyToGlobal) {
            styleActiveButton(btnModeGlobal);
            styleInactiveButton(btnModeItem);
            targetLabel.setText("Aplicando al TOTAL de la factura");
        } else {
            styleActiveButton(btnModeItem);
            styleInactiveButton(btnModeGlobal);
            CartItem item = currentSale.getItems()[selectedItemIndex];
            targetLabel.setText("Aplicando a: " + item.getProduct().getName());
        }
        updateValueDisplay();
    }
    
    private void styleActiveButton(JButton btn) {
        btn.setBackground(MainFrame.COLOR_PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new LineBorder(MainFrame.COLOR_PRIMARY_BLUE, 2));
    }
    
    private void styleInactiveButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.GRAY);
        btn.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
    }

    private void adjustPercent(double delta) {
        setPercent(currentPercent + delta);
    }

    private void setPercent(double val) {
        currentPercent = Math.max(0.0, Math.min(1.0, val)); // Clamp 0% - 100%
        updateValueDisplay();
    }

    private void updateValueDisplay() {
        valueLabel.setText(String.format("%.0f%%", currentPercent * 100));
    }

    private void applyAction() {
        if (applyToGlobal) {
            currentSale.setGlobalDiscountPercent(currentPercent);
        } else {
            currentSale.setItemDiscount(selectedItemIndex, currentPercent);
        }
        parentView.updateCartDisplay(); // Refresh UI
        dispose();
    }

    private JButton createCircleButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(70, 70));
        btn.setFont(new Font("Inter", Font.BOLD, 36));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }
}