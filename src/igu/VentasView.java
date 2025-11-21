package IGU;

import possportstore.StSystem;
import possportstore.CurrentSale;
import possportstore.Product;
import possportstore.CurrentSale.CartItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentasView extends JPanel {

    private final StSystem system;
    private CurrentSale currentSale; 
    
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    
    private JLabel totalLabel;
    private JLabel itemsCountLabel;
    private JLabel subtotalLabel;
    private JLabel ivaLabel;
    private JLabel finalTotalValueLabel;
    private JPanel catalogPanel; 
    
    private static final Color COLOR_WARNING_ORANGE = new Color(255, 165, 0); 
    private static final Color COLOR_ACTION_PAY = new Color(40, 167, 69); 
    private static final Color COLOR_ACTION_CANCEL = new Color(220, 53, 69); 

    
    public VentasView(StSystem system) {
        this.system = system;
        this.currentSale = new CurrentSale(system.getAuthenticatedUser()); 
        
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        add(createSearchPanel(), BorderLayout.NORTH);
        add(createCatalogPanel(), BorderLayout.CENTER); 
        add(createCartAndActionPanel(), BorderLayout.EAST); 
        
        loadProductsIntoCatalog(); 
        updateTotalsDisplay(); 
    }
    
    // --- 1. Panel Superior (Sin Cambios Relevantes) ---
    private JPanel createSearchPanel() {
        // ... (Tu implementación de createSearchPanel) ...
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);

        JTextField searchField = new JTextField("Buscar por ID o Nombre...");
        searchField.setFont(new Font("Inter", Font.PLAIN, 20));
        searchField.setPreferredSize(new Dimension(500, 50));
        
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
             public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Buscar por ID o Nombre...")) {
                    searchField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Buscar por ID o Nombre...");
                }
            }
        });

        JButton searchButton = new JButton("🔍 Buscar");
        searchButton.setFont(new Font("Inter", Font.BOLD, 16));
        searchButton.setBackground(MainFrame.COLOR_PRIMARY_BLUE);
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setPreferredSize(new Dimension(150, 50));
        searchButton.addActionListener(e -> searchAndAddToCart(searchField.getText()));
        
        JPanel totalsDisplay = new JPanel(new GridLayout(2, 2, 5, 5));
        totalsDisplay.setBackground(Color.WHITE);
        totalsDisplay.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        itemsCountLabel = new JLabel("Artículos: 0");
        itemsCountLabel.setFont(new Font("Inter", Font.BOLD, 18));
        itemsCountLabel.setForeground(MainFrame.COLOR_TEXT_DARK);

        totalLabel = new JLabel("TOTAL: €0.00");
        totalLabel.setFont(new Font("Inter", Font.BOLD, 22));
        totalLabel.setForeground(MainFrame.COLOR_PRIMARY_BLUE);

        totalsDisplay.add(itemsCountLabel);
        totalsDisplay.add(new JLabel(""));
        totalsDisplay.add(totalLabel);
        
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.WEST);
        panel.add(totalsDisplay, BorderLayout.EAST);
        
        return panel;
    }

    // --- 2. Panel Central (Catálogo - MODIFICADO) ---
    private JPanel createCatalogPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE); 
        mainPanel.setBorder(BorderFactory.createEmptyBorder()); 
        
        catalogPanel = new JPanel(new GridLayout(0, 3, 15, 15)); 
        catalogPanel.setBackground(Color.WHITE); 
        
        JScrollPane scrollPane = new JScrollPane(catalogPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(25);
        
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getViewport().setBackground(Color.WHITE); 
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private void loadProductsIntoCatalog() {
        catalogPanel.removeAll();
        for (Product p : system.getProducts()) { 
            if (p.getStock() > 0) {
                
                // 🔑 MODIFICACIÓN: Reducir el tamaño de la fuente de los productos
                String buttonText = "<html><div style='text-align:left; padding:8px;'>" 
                    + "<b style='font-size:14px;'>" + p.getName() + "</b><br>" // Título más pequeño (antes 16px)
                    + String.format("<span style='font-size:12px;'>€%.2f | Stock: %d</span>", p.getPrice(), p.getStock()) // Detalle más pequeño (antes 14px)
                    + "</div></html>";
                    
                JButton productButton = new JButton(buttonText);
                
                productButton.setPreferredSize(new Dimension(200, 120)); 
                productButton.setBackground(new Color(250, 250, 250)); 
                productButton.setFont(new Font("Inter", Font.PLAIN, 12)); 
                productButton.setHorizontalAlignment(SwingConstants.LEFT);
                productButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY.brighter(), 1)); 
                
                productButton.addActionListener(e -> addProductToCart(p, 1));
                
                catalogPanel.add(productButton);
            }
        }
        catalogPanel.revalidate();
        catalogPanel.repaint();
    }
    
    // --- 3. Panel Este (Carrito y Acciones - MODIFICADO) ---
    private JPanel createCartAndActionPanel() {
        // 🔑 MODIFICACIÓN: Reducimos el ancho preferido para hacerlo más delgado
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(300, 600)); // 🔑 Antes 350, AHORA 300
        panel.setBackground(Color.WHITE);
        
        // Área 3.1: Tabla del Carrito (Mantenemos los tamaños de fuente/fila grandes para usabilidad)
        cartTableModel = new DefaultTableModel(new Object[]{"ID", "Prod.", "Cant.", "Sub."}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        cartTable = new JTable(cartTableModel);
        
        cartTable.setRowHeight(40); 
        cartTable.setFont(new Font("Inter", Font.PLAIN, 16)); 
        cartTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 16));
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getViewport().setBackground(Color.WHITE); 
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Área 3.2: Totales y Botones de Acción
        JPanel southPanel = new JPanel(new BorderLayout(0, 10));
        southPanel.setBackground(Color.WHITE);
        
        JPanel finalTotalsPanel = new JPanel(new GridLayout(3, 2));
        finalTotalsPanel.setBackground(Color.WHITE);
        
        subtotalLabel = new JLabel("€0.00", SwingConstants.RIGHT);
        ivaLabel = new JLabel("€0.00", SwingConstants.RIGHT);
        finalTotalValueLabel = new JLabel("€0.00", SwingConstants.RIGHT);
        
        finalTotalValueLabel.setFont(new Font("Inter", Font.BOLD, 20)); 
        subtotalLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        ivaLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        
        finalTotalsPanel.add(new JLabel("Subtotal:")).setFont(new Font("Inter", Font.PLAIN, 16));
        finalTotalsPanel.add(subtotalLabel);
        finalTotalsPanel.add(new JLabel("IVA (13%):")).setFont(new Font("Inter", Font.PLAIN, 16));
        finalTotalsPanel.add(ivaLabel);
        
        JLabel totalPagarLabel = new JLabel("TOTAL A PAGAR:");
        totalPagarLabel.setFont(new Font("Inter", Font.BOLD, 20)); 
        
        finalTotalsPanel.add(totalPagarLabel);
        finalTotalsPanel.add(finalTotalValueLabel);
        
        southPanel.add(finalTotalsPanel, BorderLayout.NORTH);
        
        // Botones de Acción
        JPanel actionButtonsPanel = new JPanel(new GridBagLayout()); 
        actionButtonsPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 0); 
        Font largeButtonFont = new Font("Inter", Font.BOLD, 22);
        
        JButton payButton = new JButton("PAGAR");
        payButton.setBackground(COLOR_ACTION_PAY); 
        payButton.setForeground(Color.WHITE); 
        payButton.setFont(largeButtonFont); 
        payButton.setBorderPainted(false);
        payButton.addActionListener(e -> finalizeSale()); 
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.333;
        actionButtonsPanel.add(payButton, gbc);
        
        JButton removeButton = new JButton("Quitar Item");
        removeButton.setBackground(COLOR_WARNING_ORANGE); 
        removeButton.setForeground(Color.WHITE); 
        removeButton.setFont(largeButtonFont); 
        removeButton.setBorderPainted(false);
        removeButton.addActionListener(e -> removeItemFromCart()); 
        
        gbc.gridy = 1; gbc.weighty = 0.333;
        actionButtonsPanel.add(removeButton, gbc);
        
        JButton cancelButton = new JButton("Cancelar Venta");
        cancelButton.setBackground(COLOR_ACTION_CANCEL); 
        cancelButton.setForeground(Color.WHITE); 
        cancelButton.setFont(largeButtonFont); 
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(e -> cancelSale()); 

        gbc.gridy = 2; gbc.weighty = 0.334;
        actionButtonsPanel.add(cancelButton, gbc);
        
        southPanel.add(actionButtonsPanel, BorderLayout.CENTER);
        
        panel.add(southPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // --- Lógica del Carrito y Ventas (Mantenemos la lógica de la última corrección) ---

    private void searchAndAddToCart(String search) {
        Product p = system.buscarProductoPorIdONombre(search); 
        
        if (p != null) {
            addProductToCart(p, 1);
        } else {
            JOptionPane.showMessageDialog(this, "Producto con ID/Nombre '" + search + "' no encontrado.", "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addProductToCart(Product p, int quantity) {
        if (p.getStock() < quantity) {
            JOptionPane.showMessageDialog(this, "No hay suficiente stock para " + p.getName() + " (Stock: " + p.getStock() + ").", "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentSale.addItem(p, quantity);
        updateCartDisplay();
    }

    private void removeItemFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un artículo de la tabla para quitar una unidad.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CartItem itemToRemove = currentSale.getItems()[selectedRow];
        currentSale.removeItem(itemToRemove.getProduct()); 
        updateCartDisplay();
    }
    
    private void finalizeSale() {
        if (currentSale.getItems().length == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío. Agrega productos para finalizar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double finalSaleTotal = currentSale.getCurrentTotal() * (1 + 0.13); 
        
        possportstore.Sale.Invoice finalInvoice = system.finalizeSale(currentSale);
        
        if (finalInvoice != null) {
             JOptionPane.showMessageDialog(this, 
                String.format("Venta Finalizada (#%d).\nTotal a Pagar (incl. IVA): €%.2f\nCajero: %s", 
                    finalInvoice.id, 
                    finalSaleTotal,
                    finalInvoice.cashier), 
                "Pago Procesado", 
                JOptionPane.INFORMATION_MESSAGE);
                
            currentSale.clear(); 
            updateCartDisplay();
            loadProductsIntoCatalog(); 
        }
    }
    
    // Dentro de la clase IGU.VentasView.java

private void cancelSale() {
    if (currentSale.getItems().length > 0) {
        
        // 1. Definimos el mensaje como un String simple y claro.
        String message = 
                "¿Estás seguro de que deseas cancelar la venta actual?\n" +
                "Todos los productos en el carrito se perderán.";

        // 2. Usamos JOptionPane directamente con el String.
        int confirm = JOptionPane.showConfirmDialog(
            this,                               // Componente padre
            message,                            // El mensaje de texto simple
            "⚠️ CONFIRMAR CANCELACIÓN",       // Título del diálogo
            JOptionPane.YES_NO_OPTION,          // Opciones
            JOptionPane.WARNING_MESSAGE         // Tipo de icono
        );
            
        if (confirm == JOptionPane.YES_OPTION) {
            currentSale.clear();
            updateCartDisplay();
            loadProductsIntoCatalog(); // Recargar el catálogo después de la cancelación
        }
    }
}
    
    // --- Métodos de Actualización de Interfaz (Sin Cambios) ---
    
    private void updateCartDisplay() {
        cartTableModel.setRowCount(0);
        
        CartItem[] items = currentSale.getItems();
        for (CartItem item : items) {
            double itemSubtotal = item.getTotalPrice(); 
            
            cartTableModel.addRow(new Object[]{
                item.getProduct().getIdProduct(), 
                item.getProduct().getName(),
                String.format("%d", item.getQuantity()),
                String.format("€%.2f", itemSubtotal)
            });
        }
        
        updateTotalsDisplay();
    }

    private void updateTotalsDisplay() {
        double subtotal = currentSale.getCurrentTotal(); 
        double IVA_RATE = 0.13;
        double iva = subtotal * IVA_RATE; 
        double finalTotal = subtotal + iva;
        
        totalLabel.setText(String.format("TOTAL: €%.2f", finalTotal));
        itemsCountLabel.setText("Artículos: " + currentSale.getItems().length);
        
        subtotalLabel.setText(String.format("€%.2f", subtotal));
        ivaLabel.setText(String.format("€%.2f", iva));
        finalTotalValueLabel.setText(String.format("€%.2f", finalTotal));
    }
}