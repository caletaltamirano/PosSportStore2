package IGU;

import possportstore.StSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardView extends JPanel {

    private final StSystem system;

    public DashboardView(StSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Color de fondo suave
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Título del Dashboard
        JLabel title = new JLabel("Resumen de Tienda (Dashboard)", SwingConstants.CENTER);
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setBorder(new EmptyBorder(0, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // Panel para las métricas clave (Grid de 2x2)
        JPanel metricsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        metricsPanel.setBackground(new Color(245, 245, 245));
        
        // Cargar los datos y crear las tarjetas
        loadDashboardMetrics(metricsPanel);
        
        add(metricsPanel, BorderLayout.CENTER);
    }

    /**
     * Carga los datos del sistema y crea los componentes de la tarjeta (Card).
     */
    private void loadDashboardMetrics(JPanel panel) {
        // Formato para moneda (asumiendo moneda local, ej. USD, puedes cambiar Locale)
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CR"));

        // -------------------------------------------------------------
        // MÉTRICAS OBTENIDAS DEL STSYSTEM
        // -------------------------------------------------------------
        
        double totalRevenue = system.getTotalSalesRevenue();
        int totalInvoices = system.getTotalInvoicesCount();
        int totalProducts = system.getProductCount();
        int lowStockCount = system.countLowStockProducts();
        int totalUnits = system.getTotalUnitsInStock();

        // -------------------------------------------------------------
        // CREACIÓN DE TARJETAS
        // -------------------------------------------------------------

        // Tarjeta 1: Ingresos Totales
        panel.add(createMetricCard(
                "Ingresos Totales (Ventas)",
                currencyFormat.format(totalRevenue),
                new Color(40, 167, 69) // Verde
        ));

        // Tarjeta 2: Cantidad de Facturas
        panel.add(createMetricCard(
                "Facturas Emitidas",
                String.valueOf(totalInvoices),
                new Color(0, 123, 255) // Azul
        ));

        // Tarjeta 3: Productos en Stock Bajo
        panel.add(createMetricCard(
                "Alerta: Stock Bajo (<= 5)",
                String.valueOf(lowStockCount),
                (lowStockCount > 0) ? new Color(220, 53, 69) : new Color(255, 193, 7) // Rojo si hay alerta, Amarillo si no
        ));

        // Tarjeta 4: Total de Unidades en Inventario
        panel.add(createMetricCard(
                "Unidades Totales en Inventario",
                String.valueOf(totalUnits) + " unidades",
                new Color(108, 117, 125) // Gris
        ));
    }

    /**
     * Crea un panel estilizado para mostrar una métrica clave.
     */
    private JPanel createMetricCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2), // Borde de color
                new EmptyBorder(20, 20, 20, 20) // Relleno interno
        ));
        
        // Título de la métrica
        JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(51, 51, 51)); // Gris oscuro

        // Valor de la métrica (Grande y centrado)
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 40));
        valueLabel.setForeground(color); // Color del valor

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}