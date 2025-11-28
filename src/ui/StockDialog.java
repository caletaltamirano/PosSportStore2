package ui;

import possportstore.Product;
import possportstore.StoreSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for quick stock updates.
 */
public class StockDialog extends JDialog {

    private final StoreSystem system;
    private final ProductsView parentView;
    private final Product product;
    private JTextField stockField;
    
    private static final Dimension ACTION_BUTTON_SIZE = new Dimension(120, 35); 

    public StockDialog(JFrame parent, StoreSystem system, ProductsView parentView, Product product) {
        super(parent, "Modificar Stock - " + product.getName() + " (ID: " + product.getIdProduct() + ")", true);
        this.system = system;
        this.parentView = parentView;
        this.product = product;
        
        setLayout(new BorderLayout());
        setSize(350, 200); 
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.add(new JLabel("Stock Actual:"));
        formPanel.add(new JLabel(String.valueOf(product.getStock())));
        
        formPanel.add(new JLabel("Nuevo Stock:"));
        stockField = new JTextField(String.valueOf(product.getStock()));
        stockField.setPreferredSize(new Dimension(100, 35)); 
        formPanel.add(stockField);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Guardar");
        saveButton.setFont(new Font("Inter", Font.BOLD, 14));
        saveButton.setBackground(MainFrame.COLOR_PRIMARY_BLUE);
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(ACTION_BUTTON_SIZE); 
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveStock());

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Inter", Font.BOLD, 14));
        cancelButton.setPreferredSize(ACTION_BUTTON_SIZE); 
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(cancelButton);
        panel.add(saveButton);
        return panel;
    }

    private void saveStock() {
        String newStockText = stockField.getText().trim();
        
        if (newStockText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo de stock no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int newStock = Integer.parseInt(newStockText);
            
            if (newStock < 0) {
                JOptionPane.showMessageDialog(this, "El stock no puede ser negativo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = system.updateProductStock(product.getIdProduct(), newStock);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Stock de '" + product.getName() + "' actualizado a " + newStock + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                parentView.refreshView(); 
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se pudo actualizar el stock.", "Error Interno", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un número entero válido para el stock.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}