package IGU;

import possportstore.Product;
import possportstore.StSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StockDialog extends JDialog {

    private final StSystem system;
    private final ProductosView parentView;
    private final Product product;
    private JTextField stockField;
    
    // Dimensión de los botones de acción para consistencia
    private static final Dimension ACTION_BUTTON_SIZE = new Dimension(120, 35); 

    public StockDialog(JFrame parent, StSystem system, ProductosView parentView, Product product) {
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
        
        // Panel de Formulario
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.add(new JLabel("Stock Actual:"));
        formPanel.add(new JLabel(String.valueOf(product.getStock())));
        
        formPanel.add(new JLabel("Nuevo Stock:"));
        stockField = new JTextField(String.valueOf(product.getStock()));
        stockField.setPreferredSize(new Dimension(100, 35)); 
        formPanel.add(stockField);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Panel de Botones
        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
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

            // Llamar al sistema para actualizar
            boolean success = system.updateProductStock(product.getIdProduct(), newStock);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Stock de '" + product.getName() + "' actualizado a " + newStock + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                parentView.refreshView(); // Recargar la tabla
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se pudo actualizar el stock.", "Error Interno", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un número entero válido para el stock.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}