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

    private JPanel createSearchPanel() {
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

        itemsCountLabel = new JLabel("Artículos: 0");
        itemsCountLabel.setFont(new Font("Inter", Font.BOLD, 18));
        itemsCountLabel.setForeground(MainFrame.COLOR_TEXT_DARK);

        totalLabel = new JLabel("TOTAL: ₡0.00");
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

    private JPanel createCatalogPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        catalogPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        catalogPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(catalogPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUnitIncrement(25);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private void loadProductsIntoCatalog() {
        catalogPanel.removeAll();
        for (Product p : system.getProducts()) {
            if (p.getStock() > 0) {

                String buttonText = "<html><div style='text-align:left; padding:8px;'>"
                        + "<b style='font-size:14px;'>" + p.getName() + "</b><br>"
                        + String.format("<span style='font-size:12px;'>₡%.2f | Stock: %d</span>", p.getPrice(), p.getStock())
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

    private JPanel createCartAndActionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(180, 600));
        panel.setBackground(Color.WHITE);

        cartTableModel = new DefaultTableModel(new Object[]{"Prod.", "Cant.", "Sub."}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(new Font("Inter", Font.PLAIN, 12));
        cartTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(0, 10));
        southPanel.setBackground(Color.WHITE);

        JPanel finalTotalsPanel = new JPanel(new GridLayout(3, 2));
        finalTotalsPanel.setBackground(Color.WHITE);

        subtotalLabel = new JLabel("₡0.00", SwingConstants.RIGHT);
        ivaLabel = new JLabel("₡0.00", SwingConstants.RIGHT);
        finalTotalValueLabel = new JLabel("₡0.00", SwingConstants.RIGHT);

        finalTotalsPanel.add(new JLabel("Subtotal:"));
        finalTotalsPanel.add(subtotalLabel);
        finalTotalsPanel.add(new JLabel("IVA (13%):"));
        finalTotalsPanel.add(ivaLabel);
        finalTotalsPanel.add(new JLabel("TOTAL:"));
        finalTotalsPanel.add(finalTotalValueLabel);

        southPanel.add(finalTotalsPanel, BorderLayout.NORTH);

        // Botones de acción
        JPanel actionButtonsPanel = new JPanel(new GridBagLayout());
        actionButtonsPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

// Estilo común para botones grandes
        Font buttonFont = new Font("Inter", Font.BOLD, 20);

// PAGAR
        JButton payButton = new JButton("Pagar");
        payButton.setBackground(COLOR_ACTION_PAY);
        payButton.setForeground(Color.WHITE);
        payButton.setFont(buttonFont);
        payButton.setPreferredSize(new Dimension(160, 75));
        payButton.setFocusPainted(false);
        payButton.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        payButton.addActionListener(e -> finalizeSale());

        gbc.gridx = 0;
        gbc.gridy = 0;
        actionButtonsPanel.add(payButton, gbc);

// QUITAR
        JButton removeButton = new JButton("Quitar item");
        removeButton.setBackground(COLOR_WARNING_ORANGE);
        removeButton.setForeground(Color.WHITE);
        removeButton.setFont(new Font("Inter", Font.BOLD, 16));
        removeButton.setPreferredSize(new Dimension(160, 60));
        removeButton.setFocusPainted(false);
        removeButton.addActionListener(e -> removeItemFromCart());

        gbc.gridy = 1;
        actionButtonsPanel.add(removeButton, gbc);

// CANCELAR
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(COLOR_ACTION_CANCEL);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(buttonFont);
        cancelButton.setPreferredSize(new Dimension(160, 75));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        cancelButton.addActionListener(e -> cancelSale());

        gbc.gridy = 2;
        actionButtonsPanel.add(cancelButton, gbc);

// Agregar al panel
        southPanel.add(actionButtonsPanel, BorderLayout.CENTER);

        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void searchAndAddToCart(String search) {
        Product p = system.buscarProductoPorIdONombre(search);
        if (p != null) {
            addProductToCart(p, 1);
        } else {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProductToCart(Product p, int quantity) {
        if (p.getStock() < quantity) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        currentSale.addItem(p, quantity);
        updateCartDisplay();
        loadProductsIntoCatalog();
    }

    private void removeItemFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un artículo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CartItem itemToRemove = currentSale.getItems()[selectedRow];
        currentSale.removeItem(itemToRemove.getProduct());
        updateCartDisplay();
        loadProductsIntoCatalog();
    }

    private void finalizeSale() {
        if (currentSale.getItems().length == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double finalSaleTotal = currentSale.getCurrentTotal() * 1.13;
        possportstore.Sale.Invoice finalInvoice = system.finalizeSale(currentSale);

        if (finalInvoice != null) {
            JOptionPane.showMessageDialog(this,
                    String.format("Venta Finalizada (#%d).\nTotal a Pagar: ₡%.2f\nCajero: %s",
                            finalInvoice.id, finalSaleTotal, finalInvoice.cashier));

            currentSale.clear();
            updateCartDisplay();
            loadProductsIntoCatalog();
        }
    }

    private void cancelSale() {
        if (currentSale.getItems().length == 0) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this, "¿Cancelar venta?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            currentSale.clear();
            updateCartDisplay();
            loadProductsIntoCatalog();
        }
    }

    private void updateCartDisplay() {
        cartTableModel.setRowCount(0);

        for (CartItem item : currentSale.getItems()) {
            cartTableModel.addRow(new Object[]{
                item.getProduct().getName(),
                item.getQuantity(),
                String.format("₡%.2f", item.getTotalPrice())
            });
        }
        updateTotalsDisplay();
    }

    private void updateTotalsDisplay() {
        double subtotal = currentSale.getCurrentTotal();
        double iva = subtotal * 0.13;
        double finalTotal = subtotal + iva;

        totalLabel.setText(String.format("TOTAL: ₡%.2f", finalTotal));
        itemsCountLabel.setText("Artículos: " + currentSale.getItems().length);
        subtotalLabel.setText(String.format("₡%.2f", subtotal));
        ivaLabel.setText(String.format("₡%.2f", iva));
        finalTotalValueLabel.setText(String.format("₡%.2f", finalTotal));
    }
}
