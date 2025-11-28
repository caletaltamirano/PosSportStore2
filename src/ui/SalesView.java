package ui;

import possportstore.StoreSystem;
import possportstore.CurrentSale;
import possportstore.Product;
import possportstore.CurrentSale.CartItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Point of Sale (POS) view for processing sales transactions.
 * <p>
 * This panel provides the main interface for cashiers to:
 * <ul>
 * <li>Search for products by ID or Name.</li>
 * <li>Select products from a visual catalog (Optimized with large images).</li>
 * <li>View and manage the current shopping cart.</li>
 * <li>Apply discounts (Global or Item-specific).</li>
 * <li>Finalize the sale and generate an invoice.</li>
 * </ul>
 * optimized for 1024x768 resolution and touch interaction.
 * </p>
 */
public class SalesView extends JPanel {

    private final StoreSystem system;
    private final CurrentSale currentSale;

    // UI Components
    private DefaultTableModel cartTableModel;
    private JTable cartTable;

    private JLabel totalLabel;
    private JLabel itemsCountLabel;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel ivaLabel;
    private JLabel finalTotalValueLabel;
    private JPanel catalogPanel;

    // Color Constants
    private static final Color COLOR_WARNING_ORANGE = new Color(255, 165, 0);
    private static final Color COLOR_ACTION_PAY = new Color(40, 167, 69);
    private static final Color COLOR_ACTION_CANCEL = new Color(220, 53, 69);
    private static final Color COLOR_ACTION_DISCOUNT = new Color(108, 117, 125); 

    /**
     * Initializes the Sales View panel.
     * <p>
     * Sets up the layout, initializes the sales session, and constructs
     * the three main sub-panels: Search, Catalog, and Cart/Actions.
     * </p>
     *
     * @param system The main system controller used for product lookups and finalizing sales.
     */
    public SalesView(StoreSystem system) {
        this.system = system;
        this.currentSale = new CurrentSale(system.getAuthenticatedUser());

        setLayout(new BorderLayout(10, 10)); // Reduced gap for smaller screens
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        add(createSearchPanel(), BorderLayout.NORTH);
        add(createCatalogPanel(), BorderLayout.CENTER);
        add(createCartAndActionPanel(), BorderLayout.EAST);

        loadProductsIntoCatalog();
        updateTotalsDisplay();
    }

    /**
     * Creates the top panel containing the search bar and sale summary.
     * @return A JPanel with the search field and total labels.
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);

        // Search Field
        JTextField searchField = new JTextField("Buscar por ID o Nombre...");
        searchField.setFont(new Font("Inter", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(400, 40)); // Compact height

        // Placeholder logic
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Buscar por ID o Nombre...")) searchField.setText("");
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) searchField.setText("Buscar por ID o Nombre...");
            }
        });

        // Search Button
        JButton searchButton = new JButton("🔍 Buscar");
        searchButton.setFont(new Font("Inter", Font.BOLD, 14));
        searchButton.setBackground(MainFrame.COLOR_PRIMARY_BLUE);
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setPreferredSize(new Dimension(120, 40));
        searchButton.addActionListener(e -> searchAndAddToCart(searchField.getText()));

        // Totals Display (Top Right)
        JPanel totalsDisplay = new JPanel(new GridLayout(2, 2, 5, 5));
        totalsDisplay.setBackground(Color.WHITE);

        itemsCountLabel = new JLabel("Artículos: 0");
        itemsCountLabel.setFont(new Font("Inter", Font.BOLD, 14));
        itemsCountLabel.setForeground(MainFrame.COLOR_TEXT_DARK);
        
        totalLabel = new JLabel("TOTAL: ₡0.00");
        totalLabel.setFont(new Font("Inter", Font.BOLD, 20)); 
        totalLabel.setForeground(MainFrame.COLOR_PRIMARY_BLUE);

        totalsDisplay.add(itemsCountLabel);
        totalsDisplay.add(new JLabel("")); // Spacer
        totalsDisplay.add(totalLabel);

        panel.add(searchField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.WEST);
        panel.add(totalsDisplay, BorderLayout.EAST);
        
        return panel;
    }

    /**
     * Creates the center panel that displays the product catalog grid.
     * @return A JPanel containing a scrollable grid of products.
     */
    private JPanel createCatalogPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Grid layout for product cards
        catalogPanel = new JPanel(new GridLayout(0, 3, 10, 10)); // 3 columns
        catalogPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(catalogPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        scrollPane.setBorder(null); // Clean look
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    /**
     * Loads products from the system inventory into the catalog panel.
     * <p>
     * Clears existing components and recreates buttons for each product.
     * NOW INCLUDES IMAGE LOADING based on Product ID.
     * <b>Images are scaled larger (110px) and text is smaller (10px).</b>
     * </p>
     */
    private void loadProductsIntoCatalog() {
        catalogPanel.removeAll();
        for (Product p : system.getProducts()) {
            if (p != null && p.getStock() > 0) {
                // 1. Text Configuration (HTML) - Smaller fonts
                String buttonText = "<html><div style='text-align:center;'>" 
                        + "<b style='font-size:10px;'>" + p.getName() + "</b><br>" // Name size 10px
                        + String.format("<span style='font-size:10px;'>₡%.2f</span><br>"
                        + "<span style='color:gray; font-size:9px;'>Stock: %d</span>", p.getPrice(), p.getStock())
                        + "</div></html>";
                
                JButton productButton = new JButton(buttonText);
                
                // --- Image Logic ---
                // Looks for ID.png in 'images' folder
                String imagePath = "images/" + p.getIdProduct() + ".png";
                java.io.File imgFile = new java.io.File(imagePath);

                if (imgFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(imagePath);
                    // Larger Image Scale (110x110)
                    Image scaledImg = originalIcon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
                    productButton.setIcon(new ImageIcon(scaledImg));
                    
                    productButton.setHorizontalTextPosition(SwingConstants.CENTER);
                    productButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                } 

                // Button Visual Adjustments
                // Increased height to 180 to fit larger image
                productButton.setPreferredSize(new Dimension(160, 180)); 
                productButton.setBackground(Color.WHITE);
                productButton.setFont(new Font("Inter", Font.PLAIN, 12));
                productButton.setFocusPainted(false);
                // Soft border
                productButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                
                // Action
                productButton.addActionListener(e -> addProductToCart(p, 1));
                
                catalogPanel.add(productButton);
            }
        }
        catalogPanel.revalidate();
        catalogPanel.repaint();
    }

    /**
     * Creates the right-side panel containing the shopping cart table and action buttons.
     * @return A JPanel with the cart and checkout controls.
     */
    private JPanel createCartAndActionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(300, 0)); // Fixed width, flexible height
        panel.setBackground(Color.WHITE);

        // --- Cart Table ---
        cartTableModel = new DefaultTableModel(new Object[]{"Prod.", "Cant.", "Desc.", "Sub."}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(25); // Compact rows
        cartTable.setFont(new Font("Inter", Font.PLAIN, 12));
        cartTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        
        // Column width adjustments
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(40); // Qty
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(50); // Desc

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- South Panel (Totals + Buttons) ---
        JPanel southPanel = new JPanel(new BorderLayout(0, 10));
        southPanel.setBackground(Color.WHITE);

        // Detailed Totals Grid
        JPanel finalTotalsPanel = new JPanel(new GridLayout(4, 2)); 
        finalTotalsPanel.setBackground(Color.WHITE);
        finalTotalsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        Font fontLabels = new Font("Inter", Font.PLAIN, 12);
        Font fontValues = new Font("Inter", Font.BOLD, 12);

        subtotalLabel = new JLabel("₡0.00", SwingConstants.RIGHT);
        discountLabel = new JLabel("-₡0.00", SwingConstants.RIGHT);
        discountLabel.setForeground(COLOR_ACTION_CANCEL);
        ivaLabel = new JLabel("₡0.00", SwingConstants.RIGHT);
        finalTotalValueLabel = new JLabel("₡0.00", SwingConstants.RIGHT);
        
        // Apply fonts
        subtotalLabel.setFont(fontValues);
        discountLabel.setFont(fontValues);
        ivaLabel.setFont(fontValues);
        finalTotalValueLabel.setFont(new Font("Inter", Font.BOLD, 16));

        addTotalRow(finalTotalsPanel, "Subtotal:", subtotalLabel, fontLabels);
        addTotalRow(finalTotalsPanel, "Desc. Global:", discountLabel, fontLabels);
        addTotalRow(finalTotalsPanel, "IVA (13%):", ivaLabel, fontLabels);
        addTotalRow(finalTotalsPanel, "TOTAL:", finalTotalValueLabel, new Font("Inter", Font.BOLD, 16));

        southPanel.add(finalTotalsPanel, BorderLayout.NORTH);

        // --- Action Buttons Grid ---
        JPanel actionButtonsPanel = new JPanel(new GridLayout(2, 2, 5, 5)); // 2x2 Grid
        actionButtonsPanel.setBackground(Color.WHITE);
        actionButtonsPanel.setPreferredSize(new Dimension(0, 110)); // Fixed height area for buttons

        Font buttonFont = new Font("Inter", Font.BOLD, 14); // Smaller font for buttons

        // PAY Button
        JButton payButton = createActionButton("Pagar", COLOR_ACTION_PAY, Color.WHITE, buttonFont);
        payButton.addActionListener(e -> finalizeSale());

        // DISCOUNT Button
        JButton discountButton = createActionButton("Descuento", COLOR_ACTION_DISCOUNT, Color.WHITE, buttonFont);
        discountButton.addActionListener(e -> openDiscountDialog());

        // REMOVE Button
        JButton removeButton = createActionButton("Quitar", COLOR_WARNING_ORANGE, Color.WHITE, buttonFont);
        removeButton.addActionListener(e -> removeItemFromCart());

        // CANCEL Button
        JButton cancelButton = createActionButton("Cancelar", COLOR_ACTION_CANCEL, Color.WHITE, buttonFont);
        cancelButton.addActionListener(e -> cancelSale());

        // Add to grid
        actionButtonsPanel.add(payButton);
        actionButtonsPanel.add(discountButton);
        actionButtonsPanel.add(removeButton);
        actionButtonsPanel.add(cancelButton);

        southPanel.add(actionButtonsPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    /**
     * Helper to add a label-value pair to the totals panel.
     */
    private void addTotalRow(JPanel panel, String title, JLabel valueLabel, Font font) {
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(font);
        panel.add(titleLbl);
        panel.add(valueLabel);
    }
    
    /**
     * Helper to create styled action buttons.
     */
    private JButton createActionButton(String text, Color bg, Color fg, Font font) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(font);
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * Opens the Touch-Friendly Discount Dialog.
     * <p>
     * Validates that the cart is not empty before opening the dialog.
     * </p>
     */
    private void openDiscountDialog() {
        if (currentSale.getItems().length == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = cartTable.getSelectedRow();
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        // Open the custom dialog instead of JOptionPane
        DiscountDialog dialog = new DiscountDialog(parent, this, currentSale, selectedRow);
        dialog.setVisible(true);
    }
    
    /**
     * Callback method to refresh the cart table after external changes (like discounts).
     */
    public void updateCartDisplay() {
        cartTableModel.setRowCount(0);
        for (CartItem item : currentSale.getItems()) {
            String discountStr = (item.getDiscountPercent() > 0) ? String.format("-%.0f%%", item.getDiscountPercent()*100) : "-";
            cartTableModel.addRow(new Object[]{
                item.getProduct().getName(),
                item.getQuantity(),
                discountStr,
                String.format("₡%.2f", item.getItemSubtotal())
            });
        }
        updateTotalsDisplay();
    }

    /**
     * Updates the text labels for Subtotal, Discount, VAT, and Total.
     */
    private void updateTotalsDisplay() {
        double subtotalNet = currentSale.getCurrentTotal(); // Net subtotal (after discounts)
        
        // Calculate Gross Total (List Price)
        double grossTotal = 0;
        for(CartItem i : currentSale.getItems()) {
            grossTotal += i.getProduct().getPrice() * i.getQuantity();
        }
        
        double discountAmount = grossTotal - subtotalNet;
        double iva = subtotalNet * 0.13;
        double finalTotal = subtotalNet + iva;

        // Update Top Labels
        totalLabel.setText(String.format("TOTAL: ₡%.2f", finalTotal));
        itemsCountLabel.setText("Artículos: " + currentSale.getItems().length);
        
        // Update Detailed Labels
        subtotalLabel.setText(String.format("₡%.2f", grossTotal)); 
        discountLabel.setText(String.format("-₡%.2f", discountAmount)); 
        ivaLabel.setText(String.format("₡%.2f", iva));
        finalTotalValueLabel.setText(String.format("₡%.2f", finalTotal));
    }

    /**
     * Searches for a product by ID or Name and adds it to the cart if found.
     * @param search The search query string.
     */
    private void searchAndAddToCart(String search) {
        Product p = system.searchProduct(search);
        if (p != null) {
            addProductToCart(p, 1);
        } else {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a specific product to the shopping cart.
     * <p>
     * Checks inventory levels before adding. If sufficient stock exists,
     * the item is added and the catalog is refreshed (to update stock display).
     * </p>
     * @param p        The product to add.
     * @param quantity The quantity to add.
     */
    private void addProductToCart(Product p, int quantity) {
        if (p.getStock() < quantity) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        currentSale.addItem(p, quantity);
        updateCartDisplay();
        loadProductsIntoCatalog();
    }

    /**
     * Removes the currently selected item from the cart table.
     */
    private void removeItemFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        CartItem itemToRemove = currentSale.getItems()[selectedRow];
        currentSale.removeItem(itemToRemove.getProduct());
        updateCartDisplay();
        loadProductsIntoCatalog();
    }

    /**
     * Finalizes the sale, creates an invoice, and clears the cart.
     */
    private void finalizeSale() {
        if (currentSale.getItems().length == 0) {
            JOptionPane.showMessageDialog(this, "Carrito vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        possportstore.Sale.Invoice finalInvoice = system.finalizeSale(currentSale);
        
        if (finalInvoice != null) {
            JOptionPane.showMessageDialog(this, "Venta Exitosa #" + finalInvoice.getId(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            currentSale.clear();
            updateCartDisplay();
            loadProductsIntoCatalog();
        }
    }

    /**
     * Cancels the current transaction and clears the cart after confirmation.
     */
    private void cancelSale() {
        if (currentSale.getItems().length > 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Cancelar venta?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                currentSale.clear();
                updateCartDisplay();
                loadProductsIntoCatalog();
            }
        }
    }
}