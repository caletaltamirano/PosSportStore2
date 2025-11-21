package IGU;

import possportstore.StSystem;
import possportstore.Sale.Invoice;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class FacturasView extends JPanel {

    private final StSystem system;
    private DefaultTableModel invoiceTableModel;
    private JTable invoiceTable;

    // Labels para la vista de detalles
    private JLabel detailIdLabel;
    private JLabel detailTotalLabel;
    private JLabel detailDateLabel;
    private JLabel detailCashierLabel;

    public FacturasView(StSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        setupInvoiceTable();
        JPanel detailPanel = createDetailPanel();

        // Panel con lista y detalles
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(invoiceTable), detailPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        add(splitPane, BorderLayout.CENTER);
        loadInvoicesData();
    }

    // -------------------------------------------------------------
    // Tabla de facturas
    // -------------------------------------------------------------
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

    // -------------------------------------------------------------
    // Panel de detalles de factura
    // -------------------------------------------------------------
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
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // -------------------------------------------------------------
    // Carga de datos a la tabla
    // -------------------------------------------------------------
    private void loadInvoicesData() {

        invoiceTableModel.setRowCount(0);
        Invoice[] invoices = system.getInvoices();

        if (invoices.length == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron facturas registradas.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Invoice invoice : invoices) {

            double subtotal = invoice.total;
            double finalTotal = subtotal * 1.13; // IVA aplicado

            invoiceTableModel.addRow(new Object[]{
                    invoice.id,
                    String.format("₡%.2f", finalTotal),
                    invoice.date,
                    invoice.cashier
            });
        }
    }

    // -------------------------------------------------------------
    // Mostrar detalle seleccionado
    // -------------------------------------------------------------
    private void showInvoiceDetails(int rowIndex) {

        Invoice invoice = system.getInvoices()[rowIndex];

        double subtotal = invoice.total;
        double iva = subtotal * 0.13;
        double finalTotal = subtotal * 1.13;

        detailIdLabel.setText("ID de Factura: " + invoice.id);
        detailTotalLabel.setText(String.format(
                "Total: Subtotal ₡%.2f + IVA ₡%.2f = Total Final ₡%.2f",
                subtotal, iva, finalTotal));

        detailDateLabel.setText("Fecha: " + invoice.date);
        detailCashierLabel.setText("Cajero: " + invoice.cashier);
    }
}
