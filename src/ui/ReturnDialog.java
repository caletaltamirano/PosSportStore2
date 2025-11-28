package ui;

import possportstore.StoreSystem;
import possportstore.Sale.Invoice;
import possportstore.Sale.InvoiceItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Touch-friendly dialog for managing product returns.
 * <p>
 * Instead of a table, this version uses a list of interactive rows with
 * large +/- buttons, making it easier to use on touch screens.
 * </p>
 */
public class ReturnDialog extends JDialog {

    private final StoreSystem system;
    private final Invoice invoice;
    private final InvoicesView parentView;
    
    // List to keep track of the interactive rows
    private final List<ReturnItemRow> itemRows;
    private JLabel refundLabel;

    /**
     * Constructs the Touch-Friendly Return Dialog.
     *
     * @param parent     The parent frame.
     * @param system     The system controller.
     * @param parentView The invoices view.
     * @param invoice    The invoice to process.
     */
    public ReturnDialog(JFrame parent, StoreSystem system, InvoicesView parentView, Invoice invoice) {
        super(parent, "Devolución (Modo Táctil) - Factura #" + invoice.getId(), true);
        this.system = system;
        this.parentView = parentView;
        this.invoice = invoice;
        this.itemRows = new ArrayList<>();

        setSize(800, 600); // Slightly larger for touch elements
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        add(createInfoPanel(), BorderLayout.NORTH);
        add(createScrollableItemsPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    /**
     * Creates the header panel with invoice details.
     * @return A JPanel containing invoice info.
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(MainFrame.COLOR_BACKGROUND_MAIN);
        
        Font fontLabel = new Font("Inter", Font.PLAIN, 14);
        Font fontValue = new Font("Inter", Font.BOLD, 16);
        
        panel.add(createInfoItem("Fecha:", invoice.getDate(), fontLabel, fontValue));
        panel.add(createInfoItem("Cajero:", invoice.getCashier(), fontLabel, fontValue));
        // Show total with tax
        double totalWithTax = invoice.getTotal() * 1.13;
        panel.add(createInfoItem("Total Factura:", String.format("₡%.2f", totalWithTax), fontLabel, fontValue));
        
        return panel;
    }
    
    /**
     * Helper method to create a styled info item.
     * @param label The label text.
     * @param value The value text.
     * @param fLabel The font for the label.
     * @param fValue The font for the value.
     * @return A styled JPanel.
     */
    private JPanel createInfoItem(String label, String value, Font fLabel, Font fValue) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(MainFrame.COLOR_BACKGROUND_MAIN);
        JLabel l = new JLabel(label);
        l.setFont(fLabel);
        l.setForeground(Color.GRAY);
        JLabel v = new JLabel(value);
        v.setFont(fValue);
        v.setForeground(MainFrame.COLOR_TEXT_DARK);
        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    /**
     * Creates the scrollable list of product rows.
     * @return A JScrollPane containing the list of items.
     */
    private JScrollPane createScrollableItemsPanel() {
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(Color.WHITE);

        // Populate the list with custom rows using fixed array
        InvoiceItem[] items = invoice.getItems();
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                InvoiceItem item = items[i];
                if (item != null) {
                    ReturnItemRow row = new ReturnItemRow(item);
                    itemRows.add(row);
                    listContainer.add(row);
                    // Add a separator line
                    listContainer.add(new JSeparator(SwingConstants.HORIZONTAL));
                }
            }
        }
        
        // Filler to push items up
        listContainer.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(null);
        return scrollPane;
    }

    /**
     * Creates the bottom action bar with total refund and confirm button.
     * @return A JPanel with action controls.
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        refundLabel = new JLabel("Reembolso Total: ₡0.00");
        refundLabel.setFont(new Font("Inter", Font.BOLD, 24));
        refundLabel.setForeground(new Color(220, 53, 69)); // Red for refund

        JButton confirmButton = new JButton("CONFIRMAR DEVOLUCIÓN");
        confirmButton.setFont(new Font("Inter", Font.BOLD, 18));
        confirmButton.setBackground(new Color(0, 123, 255)); // Blue
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setPreferredSize(new Dimension(280, 60)); // Big touch button
        
        confirmButton.addActionListener(e -> processReturnAction());

        panel.add(refundLabel, BorderLayout.WEST);
        panel.add(confirmButton, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Recalculates the total refund amount based on user selection.
     */
    private void recalculateTotalRefund() {
        double totalRefund = 0;
        for (ReturnItemRow row : itemRows) {
            totalRefund += row.getReturnAmount();
        }
        // Apply tax to refund
        totalRefund = totalRefund * 1.13;
        refundLabel.setText(String.format("Reembolso Total: ₡%.2f", totalRefund));
    }

    /**
     * Processes the return for all rows with quantity > 0.
     */
    private void processReturnAction() {
        StringBuilder log = new StringBuilder("Devolución procesada:\n\n");
        boolean anyReturn = false;

        for (ReturnItemRow row : itemRows) {
            int qty = row.getReturnQuantity();
            if (qty > 0) {
                // Call backend WITH INVOICE ID
                boolean success = system.processReturn(invoice.getId(), row.getProductId(), qty);
                if (success) {
                    log.append("• ").append(row.getProductName())
                       .append(": ").append(qty).append(" unidades.\n");
                    anyReturn = true;
                }
            }
        }

        if (anyReturn) {
            JOptionPane.showMessageDialog(this, log.toString(), "Devolución Exitosa", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No has seleccionado ningún producto para devolver.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =================================================================================
    // INNER CLASS: Touch-Friendly Row
    // =================================================================================
    
    /**
     * A custom JPanel representing a single product row with touch controls.
     */
    private class ReturnItemRow extends JPanel {
        
        private final InvoiceItem item;
        private int returnQty = 0;
        private final int maxQty;
        private final JLabel qtyDisplay;
        
        /**
         * Constructs a row for a specific item.
         * @param item The invoice item to display.
         */
        public ReturnItemRow(InvoiceItem item) {
            this.item = item;
            this.maxQty = item.quantity;
            
            setLayout(new BorderLayout(15, 0));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(15, 20, 15, 20));
            setPreferredSize(new Dimension(0, 100)); // Fixed height for touch consistency
            setMaximumSize(new Dimension(9999, 100));

            // --- LEFT: Product Info ---
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setBackground(Color.WHITE);
            
            JLabel nameLabel = new JLabel(item.productName);
            nameLabel.setFont(new Font("Inter", Font.BOLD, 18));
            
            JLabel detailLabel = new JLabel(String.format("Vendidos: %d  |  Precio: ₡%.2f", item.quantity, item.unitPrice));
            detailLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            detailLabel.setForeground(Color.GRAY);
            
            infoPanel.add(nameLabel);
            infoPanel.add(detailLabel);
            
            add(infoPanel, BorderLayout.CENTER);
            
            // --- RIGHT: Touch Controls ---
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            controlPanel.setBackground(Color.WHITE);
            
            JButton minusBtn = createControlButton("-", new Color(240, 240, 240), Color.BLACK);
            JButton plusBtn = createControlButton("+", MainFrame.COLOR_PRIMARY_BLUE, Color.WHITE);
            
            qtyDisplay = new JLabel("0", SwingConstants.CENTER);
            qtyDisplay.setFont(new Font("Inter", Font.BOLD, 22));
            qtyDisplay.setPreferredSize(new Dimension(50, 50)); // Fixed width for number
            
            // Button Logic
            minusBtn.addActionListener(e -> {
                if (returnQty > 0) {
                    returnQty--;
                    updateState();
                }
            });
            
            plusBtn.addActionListener(e -> {
                if (returnQty < maxQty) {
                    returnQty++;
                    updateState();
                }
            });

            controlPanel.add(minusBtn);
            controlPanel.add(qtyDisplay);
            controlPanel.add(plusBtn);
            
            add(controlPanel, BorderLayout.EAST);
        }
        
        /**
         * Helper to create styled buttons.
         * @param text Button text.
         * @param bg Background color.
         * @param fg Foreground color.
         * @return The styled JButton.
         */
        private JButton createControlButton(String text, Color bg, Color fg) {
            JButton btn = new JButton(text);
            btn.setPreferredSize(new Dimension(50, 50)); // Big square button
            btn.setFont(new Font("Inter", Font.BOLD, 24));
            btn.setBackground(bg);
            btn.setForeground(fg);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            return btn;
        }
        
        /**
         * Updates the UI state of the row.
         */
        private void updateState() {
            qtyDisplay.setText(String.valueOf(returnQty));
            
            // Visual feedback
            if (returnQty > 0) {
                qtyDisplay.setForeground(new Color(220, 53, 69));
            } else {
                qtyDisplay.setForeground(Color.BLACK);
            }
            
            // Notify parent
            recalculateTotalRefund();
        }
        
        // --- Accessors for Parent ---
        
        /** @return The Product ID. */
        public String getProductId() { return item.productId; }
        /** @return The Product Name. */
        public String getProductName() { return item.productName; }
        /** @return The user-selected quantity to return. */
        public int getReturnQuantity() { return returnQty; }
        /** @return The monetary value of the return selection. */
        public double getReturnAmount() { return returnQty * item.unitPrice; }
    }
}