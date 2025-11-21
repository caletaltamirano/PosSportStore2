package possportstore;

import java.io.*;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.Arrays; // Asegúrate de que java.util.Arrays está importado

public class Sale {
    
    // El arreglo fijo para almacenar las facturas
    private static Invoice[] invoices = new Invoice[100]; // Límite de 100 facturas
    private static int invoiceCount = 0;
    private static int nextInvoiceId = 1;
    private static final String FILE_NAME = "invoices.txt";
    
    // Clase interna para la factura
    public static class Invoice {
        public final int id;
        public final double total; // Contiene el monto total de la venta
        public final String date;
        public final String cashier;
        

        public Invoice(int id, double total, String date, String cashier) {
            this.id = id;
            this.total = total;
            this.date = date;
            this.cashier = cashier;
        }

        // 🔑 MÉTODO AGREGADO: Necesario para el Dashboard (getTotalSalesRevenue)
        public double getTotal() {
            return total;
        }

        public int getId() {
            return id;
        }
        
        public String getCashier() {
            return cashier;
        }

        public String getDate() {
            return date;
        }

        @Override
        public String toString() {
            return id + ";" + total + ";" + date + ";" + cashier;
        }
    }

    // --------------------------------------------------------------
    //  NUEVO MÉTODO PARA CREAR FACTURA DESDE UN TOTAL (Reemplaza processSale)
    // --------------------------------------------------------------
    /**
     * Crea una nueva factura con el total y los datos del cajero, y la registra.
     */
    public static Invoice createInvoiceFromTotal(double total, String cashierName) {
        if (invoiceCount >= invoices.length) {
            JOptionPane.showMessageDialog(null, "Límite de facturas alcanzado.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String date = new Date().toString(); // Usamos java.util.Date como su código anterior
        
        Invoice newInvoice = new Invoice(nextInvoiceId++, total, date, cashierName);
        
        invoices[invoiceCount++] = newInvoice; // Añadir al arreglo fijo
        
        guardarFacturasEnArchivo();
        
        return newInvoice;
    }
    
    // --- Persistencia de Facturas (Lógica de Carga) ---
    
    public static void cargarFacturasDesdeArchivo() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        
        int maxId = 0;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine() && invoiceCount < invoices.length) {
                String line = fileScanner.nextLine();
                String[] data = line.split(";");
                
                if (data.length == 4) {
                    int id = Integer.parseInt(data[0]);
                    double total = Double.parseDouble(data[1]);
                    String date = data[2];
                    String cashier = data[3];
                    
                    invoices[invoiceCount++] = new Invoice(id, total, date, cashier);
                    
                    // Actualizar el nextInvoiceId para continuar la secuencia
                    if (id > maxId) {
                        maxId = id;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
             System.err.println("ERROR al cargar facturas: " + e.getMessage());
        }
        // Aseguramos que el próximo ID sea el siguiente al más alto cargado
        nextInvoiceId = maxId + 1;
    }
    
    // --- Persistencia de Facturas (Lógica de Guardado) ---
    
    public static void guardarFacturasEnArchivo() {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            for (int i = 0; i < invoiceCount; i++) {
                writer.println(invoices[i].toString());
            }
        } catch (FileNotFoundException e) {
             System.err.println("ERROR al guardar facturas: " + e.getMessage());
        }
    }
    
    /**
     * Retorna una copia del arreglo de facturas para visualización.
     */
    public static Invoice[] getInvoicesForDisplay() {
        return java.util.Arrays.copyOf(invoices, invoiceCount);
    }
}