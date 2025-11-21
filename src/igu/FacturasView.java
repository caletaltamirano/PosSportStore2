package IGU;

import possportstore.StSystem;
import possportstore.Sale.Invoice; // Importamos la clase Invoice
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class FacturasView extends JPanel {

    private final StSystem system;
    private DefaultTableModel invoiceTableModel;
    private JTable invoiceTable;
    
    // Labels para la vista de detalle
    private JLabel detailIdLabel;
    private JLabel detailTotalLabel;
    private JLabel detailDateLabel;
    private JLabel detailCashierLabel;

    public FacturasView(StSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Inicializar el modelo y la tabla
        setupInvoiceTable();
        
        // Crear el panel de detalles
        JPanel detailPanel = createDetailPanel();
        
        // Usar JSplitPane para dividir la lista y el detalle
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(invoiceTable), detailPanel);
        splitPane.setDividerLocation(350); // Ajusta la división inicial
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        add(splitPane, BorderLayout.CENTER);
        
        // Cargar los datos al inicializar
        loadInvoicesData();
    }
    
    // --- Configuración de la Tabla de Facturas ---
    
    private void setupInvoiceTable() {
        // Columnas visibles en la tabla de facturas
        invoiceTableModel = new DefaultTableModel(new Object[]{"ID", "Total Final (€)", "Fecha", "Cajero"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        invoiceTable = new JTable(invoiceTableModel);
        invoiceTable.setRowHeight(30);
        invoiceTable.setFont(new Font("Inter", Font.PLAIN, 14));
        invoiceTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        
        // Añadir Listener para mostrar el detalle al seleccionar una fila
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() != -1) {
                showInvoiceDetails(invoiceTable.getSelectedRow());
            }
        });
    }

    // --- Panel de Detalles de Factura ---
    
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            "Detalle de Factura Seleccionada", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Inter", Font.BOLD, 16)
        ));
        panel.setBackground(Color.WHITE);

        Font labelFont = new Font("Inter", Font.PLAIN, 16);
        Font valueFont = new Font("Inter", Font.BOLD, 16);
        
        detailIdLabel = new JLabel("ID de Factura: -");
        detailTotalLabel = new JLabel("Total (Subtotal): -");
        detailDateLabel = new JLabel("Fecha: -");
        detailCashierLabel = new JLabel("Cajero: -");
        
        // Aplicar fuentes y añadir al panel
        detailIdLabel.setFont(valueFont);
        detailTotalLabel.setFont(valueFont);
        detailDateLabel.setFont(valueFont);
        detailCashierLabel.setFont(valueFont);

        panel.add(detailIdLabel);
        panel.add(detailTotalLabel);
        panel.add(detailDateLabel);
        panel.add(detailCashierLabel);
        
        // Placeholder para llenar espacio
        panel.add(Box.createVerticalGlue()); 

        return panel;
    }

    // --- Lógica de Carga y Visualización de Datos ---
    
    private void loadInvoicesData() {
        invoiceTableModel.setRowCount(0); // Limpiar la tabla
        
        // Asegúrate de que StSystem tiene un método para obtener las facturas, 
        // asumiendo que llama a possportstore.Sale.getInvoicesForDisplay()
        Invoice[] invoices = system.getInvoices(); 
        
        if (invoices.length == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron facturas registradas.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Invoice invoice : invoices) {
            // El campo 'total' de la factura almacena el SUBTOTA, por eso calculamos el total final aquí.
            double subtotal = invoice.total; 
            double finalTotal = subtotal * 1.13; // Aplicamos el 13% de IVA
            
            invoiceTableModel.addRow(new Object[]{
                invoice.id,
                String.format("€%.2f", finalTotal), // Mostramos el Total Final
                invoice.date,
                invoice.cashier
            });
        }
    }
    
    private void showInvoiceDetails(int rowIndex) {
        Invoice invoice = system.getInvoices()[rowIndex];
        
        double subtotal = invoice.total;
        double iva = subtotal * 0.13;
        double finalTotal = subtotal * 1.13;

        detailIdLabel.setText("ID de Factura: " + invoice.id);
        
        // Detalle completo del monto
        detailTotalLabel.setText(String.format("Total: Subtotal €%.2f + IVA €%.2f = Total Final €%.2f", 
                                                subtotal, iva, finalTotal));
                                                
        detailDateLabel.setText("Fecha: " + invoice.date);
        detailCashierLabel.setText("Cajero: " + invoice.cashier);
    }
}