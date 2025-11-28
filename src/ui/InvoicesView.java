package ui;

import possportstore.StoreSystem;
import possportstore.Sale.Invoice;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.TitledBorder;

/**
 * View for displaying historical sales invoices.
 */
public class InvoicesView extends JPanel {

    private final StoreSystem system;
    private DefaultTableModel invoiceTableModel;
    private JTable invoiceTable;

    private JLabel detailIdLabel;
    private JLabel detailTotalLabel;
    private JLabel detailDateLabel;
    private JLabel detailCashierLabel;
    
    // New button
    private JButton returnButton; 

    /**
     * Initializes the Invoices view.
     * @param system The main system controller.
     */
    public InvoicesView(StoreSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        setupInvoiceTable();
        JPanel detailPanel = createDetailPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(invoiceTable), detailPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        add(splitPane, BorderLayout.CENTER);
        loadInvoicesData();
    }

    /**
     * Configures the invoice list table.
     */
    private void setupInvoiceTable() {
        invoiceTableModel = new DefaultTableModel(new Object[]{"ID", "Total Final (₡)", "Fecha", "Cajero"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        invoiceTable = new JTable(invoiceTableModel);
        invoiceTable.setRowHeight(25);
        invoiceTable.setFont(new Font("Inter", Font.PLAIN, 12));
        invoiceTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));

        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() != -1) {
                showInvoiceDetails(invoiceTable.getSelectedRow());
            }
        });
    }

    /**
     * Creates the side panel showing details of the selected invoice.
     * @return The configured JPanel.
     */
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Detalle de Factura Seleccionada",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Inter", Font.BOLD, 14)
        ));

        panel.setBackground(Color.WHITE);
        Font valueFont = new Font("Inter", Font.BOLD, 14);

        detailIdLabel = new JLabel("ID de Factura: -");
        detailTotalLabel = new JLabel("Total (Subtotal): -");
        detailDateLabel = new JLabel("Fecha: -");
        detailCashierLabel = new JLabel("Cajero: -");

        detailIdLabel.setFont(valueFont);
        detailTotalLabel.setFont(valueFont);
        detailDateLabel.setFont(valueFont);
        detailCashierLabel.setFont(valueFont);

        panel.add(detailIdLabel);
        panel.add(detailTotalLabel);
        panel.add(detailDateLabel);
        panel.add(detailCashierLabel);
        
        // --- NEW: Return Button ---
        returnButton = new JButton("Gestionar Devolución");
        returnButton.setBackground(new Color(220, 53, 69)); // Red color
        returnButton.setForeground(Color.WHITE);
        returnButton.setFont(new Font("Inter", Font.BOLD, 12));
        returnButton.setEnabled(false); // Disabled until an invoice is selected
        
        returnButton.addActionListener(e -> openReturnDialog());
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(returnButton);
        // --------------------------

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Populates the table with data from the system.
     */
    private void loadInvoicesData() {
        invoiceTableModel.setRowCount(0);
        Invoice[] invoices = system.getSaleManager().getInvoicesForDisplay();

        if (invoices.length == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron facturas registradas.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Invoice invoice : invoices) {
            if (invoice != null) { // Defensive check
                double subtotal = invoice.total;
                double finalTotal = subtotal * 1.13; // VAT applied

                invoiceTableModel.addRow(new Object[]{
                        invoice.id,
                        String.format("₡%.2f", finalTotal),
                        invoice.date,
                        invoice.cashier
                });
            }
        }
    }

    /**
     * Updates the detail panel when a user selects a row.
     * @param rowIndex The selected row index.
     */
    private void showInvoiceDetails(int rowIndex) {
        Invoice[] invoices = system.getSaleManager().getInvoicesForDisplay();
        if (rowIndex >= 0 && rowIndex < invoices.length) {
            Invoice invoice = invoices[rowIndex];

            if (invoice != null) {
                double subtotal = invoice.total;
                double iva = subtotal * 0.13;
                double finalTotal = subtotal * 1.13;

                detailIdLabel.setText("ID de Factura: " + invoice.id);
                detailTotalLabel.setText(String.format(
                        "Total: Subtotal ₡%.2f + IVA ₡%.2f = Total Final ₡%.2f",
                        subtotal, iva, finalTotal));

                detailDateLabel.setText("Fecha: " + invoice.date);
                detailCashierLabel.setText("Cajero: " + invoice.cashier);
                
                // Enable button when invoice is selected
                returnButton.setEnabled(true);
            }
        }
    }
    
    /**
     * Opens the Return Dialog for the selected invoice.
     */
    private void openReturnDialog() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow != -1) {
            Invoice[] invoices = system.getSaleManager().getInvoicesForDisplay();
            Invoice invoice = invoices[selectedRow];
            
            // Check if invoice has item details (compatibility check)
            if (invoice != null && (invoice.getItems() == null || invoice.getItems().length == 0)) {
                JOptionPane.showMessageDialog(this, "Esta factura es antigua y no tiene detalle de productos para devolución.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ReturnDialog dialog = new ReturnDialog(parentFrame, system, this, invoice);
            dialog.setVisible(true);
        }
    }
}