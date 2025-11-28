package ui;

import possportstore.Product;
import possportstore.StoreSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * View for managing product inventory (CRUD).
 */
public class ProductsView extends JPanel {

    private final StoreSystem system;
    private JTable productsTable;           
    private DefaultTableModel tableModel;   

    public ProductsView(StoreSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10)); 
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        
        refreshView(); 
    }

    private JPanel createHeader() {
        JLabel title = new JLabel("GESTIÓN DE PRODUCTOS");
        title.setFont(MainFrame.FONT_TITLE.deriveFont(15f)); 
        title.setForeground(MainFrame.COLOR_TEXT_DARK); 
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); 
        buttonPanel.setBackground(Color.WHITE);

        // --- Modify Stock Button ---
        JButton stockButton = new JButton("Modificar Stock");
        stockButton.setFont(new Font("Inter", Font.BOLD, 16));
        stockButton.setBackground(new Color(255, 193, 7)); // Yellow
        stockButton.setForeground(Color.BLACK);
        stockButton.setBorderPainted(false);
        stockButton.setFocusPainted(false);
        stockButton.setPreferredSize(new Dimension(200, 50));
        stockButton.addActionListener(e -> modifyStockAction()); 

        // --- Delete Button ---
        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setFont(new Font("Inter", Font.BOLD, 16)); 
        deleteButton.setBackground(new Color(220, 53, 69)); // Red
        deleteButton.setForeground(Color.WHITE); 
        deleteButton.setBorderPainted(false); 
        deleteButton.setFocusPainted(false); 
        deleteButton.setPreferredSize(new Dimension(200, 50)); 
        deleteButton.addActionListener(e -> deleteProductAction());
        
        // --- Add Button ---
        JButton addButton = new JButton("Nuevo");
        addButton.setFont(new Font("Inter", Font.BOLD, 16)); 
        addButton.setBackground(MainFrame.COLOR_PRIMARY_BLUE); 
        addButton.setForeground(Color.WHITE); 
        addButton.setBorderPainted(false); 
        addButton.setFocusPainted(false); 
        addButton.setPreferredSize(new Dimension(200, 50)); 

        addButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddProductDialog dialog = new AddProductDialog(parentFrame, system, this);
            dialog.setVisible(true);
        });
        
        buttonPanel.add(stockButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST); 
        return headerPanel;
    }
    
    private JScrollPane createContent() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Stock", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productsTable = new JTable(tableModel); 
        productsTable.setRowHeight(30);
        productsTable.setFont(new Font("Inter", Font.PLAIN, 14));
        productsTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        
        return new JScrollPane(productsTable);
    }
    
    private void modifyStockAction() {
        int selectedRow = productsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla para modificar el stock.", "Error de Selección", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String idProductToModify = (String) productsTable.getModel().getValueAt(productsTable.convertRowIndexToModel(selectedRow), 0);
        
        Product product = system.findProductById(idProductToModify);

        if (product != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            StockDialog dialog = new StockDialog(parentFrame, system, this, product);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error: Producto no encontrado en la base de datos.", "Error Interno", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProductAction() {
        int selectedRow = productsTable.getSelectedRow(); 
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla para eliminar.", "Error de Selección", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String idProductToDelete = (String) productsTable.getModel().getValueAt(productsTable.convertRowIndexToModel(selectedRow), 0);

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "¿Estás seguro de que quieres eliminar el producto con ID: " + idProductToDelete + "?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = system.deleteProduct(idProductToDelete); 
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Producto eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refreshView();
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se pudo encontrar o eliminar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refreshes table data from the system backend.
     */
    public void refreshView() {
        tableModel.setRowCount(0); 
        Product[] products = system.getProducts();
        
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getIdProduct(), 
                p.getName(), 
                p.getPrice(), 
                p.getStock(), 
                p.getClass().getSimpleName() 
            });
        }
    }
}