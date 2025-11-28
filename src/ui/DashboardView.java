package ui;

import possportstore.*;
import possportstore.Sale.Invoice;
import possportstore.Sale.InvoiceItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Advanced Dashboard Panel for the Point of Sale System.
 * <p>
 * Optimized for 1024x768 resolution. Displays KPIs, Charts, and Data Tables.
 * </p>
 */
public class DashboardView extends JPanel {

    private final StoreSystem system;
    
    // UI Components
    private JPanel cardsPanel;
    private JPanel analysisPanel;
    private DefaultTableModel recentSalesModel;
    private DefaultTableModel lowStockModel; 
    private JLabel lastUpdateLabel;

    /**
     * Constructs the Dashboard view.
     * @param system The main system controller used to fetch data.
     */
    public DashboardView(StoreSystem system) {
        this.system = system;
        setLayout(new BorderLayout(15, 15)); // Reduced gap
        setBackground(new Color(240, 242, 245)); 
        setBorder(new EmptyBorder(15, 15, 15, 15)); // Reduced borders

        // 1. Header
        add(createHeader(), BorderLayout.NORTH);

        // 2. Main Scrollable Content Area
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBackground(new Color(240, 242, 245));

        // --- Section A: KPI Cards ---
        cardsPanel = new JPanel(new GridLayout(1, 4, 10, 0)); // Smaller gap
        cardsPanel.setBackground(new Color(240, 242, 245));
        // Removed setMaximumSize to allow expansion on small screens if needed
        contentContainer.add(cardsPanel);
        contentContainer.add(Box.createVerticalStrut(15)); 

        // --- Section B: Detailed Analysis ---
        analysisPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        analysisPanel.setBackground(new Color(240, 242, 245));
        // Removed fixed height constraint
        contentContainer.add(analysisPanel);
        contentContainer.add(Box.createVerticalStrut(15)); 

        // --- Section C: Tables ---
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesPanel.setBackground(new Color(240, 242, 245));
        tablesPanel.add(createLowStockSection()); 
        tablesPanel.add(createRecentSalesSection());
        
        // Ensure tables have a minimum height so they don't collapse
        tablesPanel.setPreferredSize(new Dimension(0, 300));
        contentContainer.add(tablesPanel);

        // Scroll Pane with faster scrolling for touch/mouse wheel
        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // Faster scroll
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);

        // Initial Data Load
        refreshDashboard();
    }

    /**
     * Creates the top header section.
     * @return The header JPanel.
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(240, 242, 245));

        JLabel title = new JLabel("Tablero de Control");
        title.setFont(new Font("Inter", Font.BOLD, 24)); // Reduced size for 1024px
        title.setForeground(MainFrame.COLOR_PRIMARY_NAVY);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(new Color(240, 242, 245));

        lastUpdateLabel = new JLabel("Actualizado: --:--");
        lastUpdateLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        lastUpdateLabel.setForeground(Color.GRAY);

        JButton refreshBtn = new JButton("↻ Actualizar");
        refreshBtn.setFont(new Font("Inter", Font.BOLD, 12));
        refreshBtn.setBackground(MainFrame.COLOR_PRIMARY_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshDashboard());

        actionPanel.add(lastUpdateLabel);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(refreshBtn);

        header.add(title, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.EAST);
        return header;
    }

    /**
     * Creates the "Low Stock Alert" section.
     * @return A JPanel containing the low stock table.
     */
    private JPanel createLowStockSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createCardBorder("⚠️ Stock Bajo"));

        String[] columns = {"ID", "Producto", "Stock"};
        lowStockModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable table = new JTable(lowStockModel);
        table.setRowHeight(25); // Compact row height
        table.setFont(new Font("Inter", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.setShowGrid(false);
        
        table.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(new Color(220, 53, 69)); // Red text
                setFont(table.getFont().deriveFont(Font.BOLD));
                return c;
            }
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the "Recent Transactions" section.
     * @return A JPanel containing the recent sales table.
     */
    private JPanel createRecentSalesSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createCardBorder("Últimas Ventas"));

        String[] columns = {"ID", "Fecha", "Total"};
        recentSalesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable table = new JTable(recentSalesModel);
        table.setRowHeight(25);
        table.setFont(new Font("Inter", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.setShowGrid(false);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Refreshes all data displayed on the dashboard.
     */
    public void refreshDashboard() {
        // 1. Calculate General Metrics from Backend
        double totalRevenue = system.getTotalSalesRevenue(); 
        int totalInvoices = system.getTotalInvoicesCount();
        int lowStockCount = system.countLowStockProducts();
        
        double avgTicket = (totalInvoices > 0) ? totalRevenue / totalInvoices : 0.0;

        // 2. Rebuild KPI Cards
        cardsPanel.removeAll();
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("es", "CR"));
        
        // Compact KPI cards for 1024px
        cardsPanel.add(createKpiCard("Ingresos", currency.format(totalRevenue), new Color(40, 167, 69), "💰"));
        cardsPanel.add(createKpiCard("Ventas", String.valueOf(totalInvoices), new Color(0, 123, 255), "🧾"));
        cardsPanel.add(createKpiCard("Stock Bajo", String.valueOf(lowStockCount), (lowStockCount > 0 ? new Color(220, 53, 69) : new Color(255, 193, 7)), "⚠️"));
        cardsPanel.add(createKpiCard("Ticket Prom.", currency.format(avgTicket), new Color(108, 117, 125), "📊"));
        
        cardsPanel.revalidate();
        cardsPanel.repaint();

        // 3. Update Analysis
        updateAnalysisPanel();

        // 4. Update Tables
        updateRecentSalesTable();
        updateLowStockTable();

        // 5. Update timestamp
        lastUpdateLabel.setText("Actualizado: " + java.time.LocalTime.now().toString().substring(0, 8));
    }
    
    /**
     * Reloads the Low Stock table.
     */
    private void updateLowStockTable() {
        lowStockModel.setRowCount(0);
        Product[] lowStockProducts = system.getLowStockProducts();
        
        for (Product p : lowStockProducts) {
            if (p != null) {
                lowStockModel.addRow(new Object[]{
                    p.getIdProduct(),
                    p.getName(),
                    p.getStock()
                });
            }
        }
    }

    /**
     * Updates the Analysis Panel (Charts).
     */
    private void updateAnalysisPanel() {
        analysisPanel.removeAll();

        double shoeSales = 0, clotheSales = 0, accSales = 0;
        String topProductName = "N/A";
        int maxSoldQty = 0;
        
        Map<String, Integer> productSalesCount = new HashMap<>();

        Invoice[] invoices = system.getSaleManager().getInvoicesForDisplay();
        for (Invoice inv : invoices) {
            if (inv != null && inv.getItems() != null) {
                for (InvoiceItem item : inv.getItems()) {
                    if (item != null) {
                        productSalesCount.put(item.productId, productSalesCount.getOrDefault(item.productId, 0) + item.quantity);
                        
                        Product p = system.findProductById(item.productId);
                        if (p != null) {
                            double amount = item.quantity * item.unitPrice;
                            if (p instanceof Shoe) shoeSales += amount;
                            else if (p instanceof Clothe) clotheSales += amount;
                            else if (p instanceof Accessories) accSales += amount;
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : productSalesCount.entrySet()) {
            if (entry.getValue() > maxSoldQty) {
                maxSoldQty = entry.getValue();
                Product p = system.findProductById(entry.getKey());
                topProductName = (p != null) ? p.getName() : "ID: " + entry.getKey();
            }
        }

        // --- Left Panel: Charts ---
        JPanel catPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        catPanel.setBorder(createCardBorder("Por Categoría"));
        catPanel.setBackground(Color.WHITE);
        
        double totalCatSales = shoeSales + clotheSales + accSales;
        if (totalCatSales == 0) totalCatSales = 1; 

        catPanel.add(createCategoryBar("Zapatos", shoeSales, totalCatSales, new Color(54, 162, 235)));
        catPanel.add(createCategoryBar("Ropa", clotheSales, totalCatSales, new Color(255, 99, 132)));
        catPanel.add(createCategoryBar("Accesorios", accSales, totalCatSales, new Color(255, 206, 86)));

        // --- Right Panel: Top Product ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(createCardBorder("Más Vendido ⭐"));
        topPanel.setBackground(Color.WHITE);

        // Responsive font size
        JLabel nameLabel = new JLabel("<html><center>" + topProductName + "</center></html>", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Inter", Font.BOLD, 18)); 
        nameLabel.setForeground(MainFrame.COLOR_PRIMARY_NAVY);
        
        JLabel detailLabel = new JLabel("Unidades: " + maxSoldQty, SwingConstants.CENTER);
        detailLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        detailLabel.setForeground(Color.GRAY);

        topPanel.add(nameLabel, BorderLayout.CENTER);
        topPanel.add(detailLabel, BorderLayout.SOUTH);

        analysisPanel.add(catPanel);
        analysisPanel.add(topPanel);
        
        analysisPanel.revalidate();
        analysisPanel.repaint();
    }

    /**
     * Creates a progress bar chart.
     * @param name Name of category.
     * @param value Value.
     * @param total Total.
     * @param color Bar color.
     * @return Panel.
     */
    private JPanel createCategoryBar(String name, double value, double total, Color color) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        
        int percent = (int) ((value / total) * 100);
        
        JLabel lbl = new JLabel(name + " (" + percent + "%)");
        lbl.setFont(new Font("Inter", Font.BOLD, 11));
        
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(percent);
        bar.setForeground(color);
        bar.setBackground(new Color(240, 240, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(100, 10)); // Slimmer bar
        
        p.add(lbl, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        return p;
    }

    /**
     * Updates recent sales table (Last 10).
     */
    private void updateRecentSalesTable() {
        recentSalesModel.setRowCount(0);
        Invoice[] allInvoices = system.getSaleManager().getInvoicesForDisplay();
        
        int count = 0;
        for (int i = allInvoices.length - 1; i >= 0 && count < 10; i--) { 
            Invoice inv = allInvoices[i];
            if (inv != null) {
                double finalTotal = inv.getTotal() * 1.13; 
                
                recentSalesModel.addRow(new Object[]{
                    "#" + inv.getId(),
                    (inv.getDate().length() > 16) ? inv.getDate().substring(0, 16) : inv.getDate(),
                    String.format("₡%.2f", finalTotal)
                });
                count++;
            }
        }
    }

    /**
     * Creates a KPI card.
     * @param title Title text.
     * @param value Value text.
     * @param color Accent color.
     * @param icon Icon emoji.
     * @return The card JPanel.
     */
    private JPanel createKpiCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color), 
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Inter", Font.BOLD, 12));
        titleLbl.setForeground(Color.GRAY);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Inter", Font.BOLD, 18)); // Smaller font to fit 1024px
        valueLbl.setForeground(MainFrame.COLOR_TEXT_DARK);
        
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLbl);
        textPanel.add(valueLbl);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLbl, BorderLayout.EAST);
        return card;
    }
    
    /**
     * Creates a styled border.
     * @param title Border title.
     * @return The Border object.
     */
    private javax.swing.border.Border createCardBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            title,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Inter", Font.BOLD, 13)
        );
    }
}