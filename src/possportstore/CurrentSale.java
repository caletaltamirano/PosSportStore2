package possportstore;

import javax.swing.JOptionPane;
import java.util.Arrays; // Necesario para Arrays.copyOf

public class CurrentSale {

    // -------------------------------------------------------------
    // CLASE INTERNA: Item en el Carrito (Arreglo fijo)
    // -------------------------------------------------------------
    public static class CartItem {
        private Product product;
        private int quantity;
        
        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        
        public double getTotalPrice() {
            return product.getPrice() * quantity; 
        }
    }
    
    // -------------------------------------------------------------
    // PROPIEDADES DEL CARRITO (ARREGLOS FIJOS)
    // -------------------------------------------------------------
    private static final int MAX_ITEMS = 50; 
    private CartItem[] items = new CartItem[MAX_ITEMS];
    private int itemCount = 0;
    private double currentTotal = 0.0;
    private final User authenticatedUser; 

    public CurrentSale(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    // Getters para la vista
    public User getAuthenticatedUser() { return authenticatedUser; }
    public double getCurrentTotal() { return currentTotal; }
    
    /**
     * Devuelve un sub-arreglo (copia) que contiene solo los items válidos.
     */
    public CartItem[] getItems() {
        return Arrays.copyOf(items, itemCount);
    }

    // --- Lógica del Carrito ---
    
    public void addItem(Product product, int quantity) {
        int existingIndex = -1;
        for (int i = 0; i < itemCount; i++) {
            if (items[i].getProduct().equals(product)) {
                existingIndex = i;
                break;
            }
        }
        
        if (existingIndex != -1) {
            items[existingIndex].setQuantity(items[existingIndex].getQuantity() + quantity);
        } else {
            if (itemCount < MAX_ITEMS) {
                items[itemCount] = new CartItem(product, quantity);
                itemCount++;
            } else {
                JOptionPane.showMessageDialog(null, "Límite de artículos en el carrito alcanzado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        updateTotal();
    }
    
    public void removeItem(Product product) {
        int indexToRemove = -1;
        for (int i = 0; i < itemCount; i++) {
            if (items[i].getProduct().equals(product)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove != -1) {
            int newQuantity = items[indexToRemove].getQuantity() - 1;
            
            if (newQuantity <= 0) {
                // Mover el resto del array para eliminar el hueco
                for (int i = indexToRemove; i < itemCount - 1; i++) {
                    items[i] = items[i + 1];
                }
                items[itemCount - 1] = null; 
                itemCount--;
            } else {
                items[indexToRemove].setQuantity(newQuantity);
            }
        }
        
        updateTotal();
    }
    
    public void updateTotal() {
        currentTotal = 0.0;
        for (int i = 0; i < itemCount; i++) {
            currentTotal += items[i].getTotalPrice();
        }
    }
    
    public void clear() {
        for (int i = 0; i < itemCount; i++) {
            items[i] = null;
        }
        itemCount = 0;
        currentTotal = 0.0;
    }
}