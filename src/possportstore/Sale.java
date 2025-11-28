package possportstore;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import possportstore.CurrentSale.CartItem;

/**
 * Manages the history of sales invoices and their file persistence.
 * <p>
 * This class handles the creation, storage, retrieval, and modification 
 * of {@link Invoice} objects. It uses fixed arrays to store invoices and invoice items.
 * </p>
 */
public class Sale {
    
    private static final String FILE_NAME = "invoices.txt";
    private static final int MAX_INVOICES = 100;
    
    private static Invoice[] invoices = new Invoice[MAX_INVOICES]; 
    private static int invoiceCount = 0;
    private static int nextInvoiceId = 1;
    
    /**
     * Inner class representing a single sales invoice.
     * Contains header information, global discount info, and a fixed array of sold items.
     */
    public static class Invoice {
        public final int id;
        public final double total;
        public final String date;
        public final String cashier;
        public final double globalDiscount; // Stored as percentage (e.g., 0.10)
        public final InvoiceItem[] items; 

        /**
         * Constructs a new Invoice.
         */
        public Invoice(int id, double total, String date, String cashier, double globalDiscount, InvoiceItem[] items) {
            this.id = id;
            this.total = total;
            this.date = date;
            this.cashier = cashier;
            this.globalDiscount = globalDiscount;
            this.items = items;
        }

        public double getTotal() { return total; }
        public int getId() { return id; }
        public String getCashier() { return cashier; }
        public String getDate() { return date; }
        public double getGlobalDiscount() { return globalDiscount; }
        public InvoiceItem[] getItems() { return items; }

        /**
         * Formats the invoice for file storage.
         * Format: id;total;date;cashier;globalDisc;itemId:qty:price:name:itemDisc|...
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(id).append(";")
              .append(total).append(";")
              .append(date).append(";")
              .append(cashier).append(";")
              .append(globalDiscount).append(";");
            
            if (items != null) {
                for (int i = 0; i < items.length; i++) {
                    InvoiceItem item = items[i];
                    if (item != null) {
                        sb.append(item.productId).append(":")
                          .append(item.quantity).append(":")
                          .append(item.unitPrice).append(":")
                          .append(item.productName).append(":")
                          .append(item.discountPercent);
                        
                        if (i < items.length - 1) {
                            sb.append("|");
                        }
                    }
                }
            }
            return sb.toString();
        }
    }

    /**
     * Helper class to store a snapshot of a sold item within an invoice.
     */
    public static class InvoiceItem {
        public String productId;
        public String productName;
        public int quantity;
        public double unitPrice; // This is the EFFECTIVE price paid per unit
        public double discountPercent; // The specific item discount

        public InvoiceItem(String productId, String productName, int quantity, double unitPrice, double discountPercent) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.discountPercent = discountPercent;
        }
    }

    /**
     * Creates a new invoice from the transaction total and cart items.
     * <p>
     * IMPORTANT: This calculates the effective unit price for history.
     * Effective Price = Base Price * (1 - ItemDisc) * (1 - GlobalDisc).
     * This ensures refunds are accurate to what was paid.
     * </p>
     *
     * @param total          The total sale amount.
     * @param cashierName    The name of the cashier.
     * @param cartItems      The array of items from the current cart.
     * @param globalDiscount The global discount percentage applied.
     * @return The created {@link Invoice} object.
     */
    public static Invoice createInvoice(double total, String cashierName, CartItem[] cartItems, double globalDiscount) {
        if (invoiceCount >= invoices.length) {
            JOptionPane.showMessageDialog(null, "Invoice limit reached.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String date = new Date().toString();
        
        int itemCount = (cartItems != null) ? cartItems.length : 0;
        InvoiceItem[] invoiceItems = new InvoiceItem[itemCount];
        
        if (cartItems != null) {
            for (int i = 0; i < itemCount; i++) {
                CartItem ci = cartItems[i];
                
                // Calculate the final price the customer paid per unit
                // Base * ItemDisc * GlobalDisc
                double effectivePrice = ci.getProduct().getPrice() * (1.0 - ci.getDiscountPercent()) * (1.0 - globalDiscount);
                
                invoiceItems[i] = new InvoiceItem(
                    ci.getProduct().getIdProduct(),
                    ci.getProduct().getName(),
                    ci.getQuantity(),
                    effectivePrice,
                    ci.getDiscountPercent()
                );
            }
        }

        Invoice newInvoice = new Invoice(nextInvoiceId++, total, date, cashierName, globalDiscount, invoiceItems);
        
        invoices[invoiceCount++] = newInvoice;
        saveInvoicesToFile();
        
        return newInvoice;
    }
    
    /**
     * Processes a return by updating the specific invoice.
     * Same logic as before, but preserving the new structure.
     */
    public static boolean processItemReturn(int invoiceId, String productId, int returnedQty) {
        int invoiceIndex = -1;
        for (int i = 0; i < invoiceCount; i++) {
            if (invoices[i].getId() == invoiceId) {
                invoiceIndex = i;
                break;
            }
        }
        
        if (invoiceIndex == -1) return false;
        
        Invoice oldInvoice = invoices[invoiceIndex];
        InvoiceItem[] oldItems = oldInvoice.getItems();
        
        int itemIndex = -1;
        int activeItemCount = 0;
        
        for (int i = 0; i < oldItems.length; i++) {
            if (oldItems[i].productId.equals(productId)) {
                itemIndex = i;
            }
            if (oldItems[i] != null) activeItemCount++; 
        }
        
        if (itemIndex == -1) return false;
        
        InvoiceItem targetItem = oldItems[itemIndex];
        int newQuantity = targetItem.quantity - returnedQty;
        
        int newSize = (newQuantity <= 0) ? activeItemCount - 1 : activeItemCount;
        InvoiceItem[] newItems = new InvoiceItem[newSize];
        
        int k = 0;
        double newTotal = 0.0;
        
        for (int i = 0; i < oldItems.length; i++) {
            if (i == itemIndex) {
                if (newQuantity > 0) {
                    InvoiceItem updatedItem = new InvoiceItem(
                        targetItem.productId, 
                        targetItem.productName, 
                        newQuantity, 
                        targetItem.unitPrice,
                        targetItem.discountPercent
                    );
                    newItems[k++] = updatedItem;
                    newTotal += updatedItem.quantity * updatedItem.unitPrice;
                }
            } else {
                newItems[k++] = oldItems[i];
                newTotal += oldItems[i].quantity * oldItems[i].unitPrice;
            }
        }
        
        Invoice updatedInvoice = new Invoice(
            oldInvoice.id, 
            newTotal, 
            oldInvoice.date, 
            oldInvoice.cashier,
            oldInvoice.globalDiscount,
            newItems
        );
        
        invoices[invoiceIndex] = updatedInvoice;
        saveInvoicesToFile();
        
        return true;
    }
    
    /**
     * Loads invoices from the persistence file at startup.
     */
    public static void loadInvoicesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        
        int maxId = 0;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine() && invoiceCount < invoices.length) {
                String line = fileScanner.nextLine();
                String[] data = line.split(";");
                
                // Now expecting 6 parts for header due to globalDiscount
                // id;total;date;cashier;globalDisc;items
                if (data.length >= 5) {
                    int id = Integer.parseInt(data[0]);
                    double total = Double.parseDouble(data[1]);
                    String date = data[2];
                    String cashier = data[3];
                    
                    // Check if format has global discount (backward compatibility check)
                    double globalDisc = 0.0;
                    String itemsString = "";
                    
                    // Logic to handle old format vs new format
                    if (data.length >= 6) {
                        // New format
                        try {
                            globalDisc = Double.parseDouble(data[4]);
                        } catch(Exception e) { globalDisc = 0.0; }
                        itemsString = data[5];
                    } else {
                        // Old format (header length 5) - items are at index 4
                        itemsString = data[4];
                    }
                    
                    InvoiceItem[] items = new InvoiceItem[0];
                    
                    if (!itemsString.isEmpty()) {
                        String[] itemsRaw = itemsString.split("\\|");
                        items = new InvoiceItem[itemsRaw.length];
                        
                        for (int i = 0; i < itemsRaw.length; i++) {
                            String[] parts = itemsRaw[i].split(":");
                            // id:qty:price:name:disc
                            if (parts.length >= 4) {
                                double itemDisc = (parts.length > 4) ? Double.parseDouble(parts[4]) : 0.0;
                                items[i] = new InvoiceItem(
                                    parts[0], 
                                    parts[3], 
                                    Integer.parseInt(parts[1]), 
                                    Double.parseDouble(parts[2]),
                                    itemDisc
                                );
                            }
                        }
                    }
                    
                    invoices[invoiceCount++] = new Invoice(id, total, date, cashier, globalDisc, items);
                    if (id > maxId) maxId = id;
                }
            }
        } catch (Exception e) {
             System.err.println("ERROR loading invoices: " + e.getMessage());
        }
        nextInvoiceId = maxId + 1;
    }
    
    public static void saveInvoicesToFile() {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            for (int i = 0; i < invoiceCount; i++) {
                writer.println(invoices[i].toString());
            }
        } catch (FileNotFoundException e) {
             System.err.println("ERROR saving invoices: " + e.getMessage());
        }
    }
    
    public static Invoice[] getInvoicesForDisplay() {
        return Arrays.copyOf(invoices, invoiceCount);
    }
    
    public static Invoice findInvoiceById(int id) {
        for(int i=0; i<invoiceCount; i++) {
            if(invoices[i].getId() == id) return invoices[i];
        }
        return null;
    }
}