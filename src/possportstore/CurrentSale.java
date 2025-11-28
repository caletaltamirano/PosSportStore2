package possportstore;

import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 * Manages the current transaction, including the shopping cart, discounts, and total calculation.
 */
public class CurrentSale {

    /**
     * Represents an item line in the shopping cart (Product + Quantity + Discount).
     */
    public static class CartItem {
        private Product product;
        private int quantity;
        private double discountPercent; // 0.0 to 1.0 (e.g., 0.10 for 10%)
        
        /**
         * Constructs a CartItem.
         * @param product  The product to add.
         * @param quantity The quantity.
         */
        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
            this.discountPercent = 0.0;
        }

        /** Gets the product. */
        public Product getProduct() { return product; }
        /** Gets the quantity. */
        public int getQuantity() { return quantity; }
        /** Sets the quantity. */
        public void setQuantity(int quantity) { this.quantity = quantity; }
        
        /** Gets the discount percentage for this item (0.0 - 1.0). */
        public double getDiscountPercent() { return discountPercent; }
        /** Sets the discount percentage. */
        public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
        
        /** * Calculates the total price for this line item *after* item discount.
         * Note: Global discount is applied later to the subtotal.
         */
        public double getItemSubtotal() {
            double basePrice = product.getPrice() * quantity;
            return basePrice * (1.0 - discountPercent); 
        }
    }
    
    private static final int MAX_ITEMS = 50; 
    private CartItem[] items = new CartItem[MAX_ITEMS];
    private int itemCount = 0;
    private double currentTotal = 0.0;
    private double globalDiscountPercent = 0.0; // 0.0 to 1.0
    
    private final User authenticatedUser; 

    /**
     * Initializes a new sale session for a specific user.
     * @param authenticatedUser The cashier performing the sale.
     */
    public CurrentSale(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    /** Gets the user performing the sale. */
    public User getAuthenticatedUser() { return authenticatedUser; }
    
    /** Gets the current total after ALL discounts (Item + Global). */
    public double getCurrentTotal() { return currentTotal; }
    
    /** Gets the global discount percentage. */
    public double getGlobalDiscountPercent() { return globalDiscountPercent; }
    
    /** Sets the global discount percentage (0.0 to 1.0). */
    public void setGlobalDiscountPercent(double globalDiscountPercent) { 
        this.globalDiscountPercent = globalDiscountPercent;
        updateTotal();
    }
    
    /**
     * Retrieves the list of items currently in the cart.
     * @return An array of {@link CartItem}.
     */
    public CartItem[] getItems() {
        return Arrays.copyOf(items, itemCount);
    }

    /**
     * Adds a product to the cart. 
     * If the product exists, updates the quantity.
     *
     * @param product  The product to add.
     * @param quantity The quantity to add.
     */
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
                JOptionPane.showMessageDialog(null, "Cart full.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateTotal();
    }
    
    /**
     * Removes one unit of a product from the cart.
     * If quantity reaches zero, the item line is removed entirely.
     *
     * @param product The product to remove.
     */
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
                // Shift array to remove item
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
    
    /**
     * Applies a discount to a specific item in the cart.
     * @param index The index of the item.
     * @param percent The discount percentage (0.0 - 1.0).
     */
    public void setItemDiscount(int index, double percent) {
        if (index >= 0 && index < itemCount) {
            items[index].setDiscountPercent(percent);
            updateTotal();
        }
    }
    
    /**
     * Recalculates the total price of the sale.
     * Formula: Sum(ItemSubtotals) * (1 - GlobalDiscount)
     */
    private void updateTotal() {
        double subtotal = 0.0;
        for (int i = 0; i < itemCount; i++) {
            subtotal += items[i].getItemSubtotal();
        }
        // Apply global discount to the sum of discounted items
        currentTotal = subtotal * (1.0 - globalDiscountPercent);
    }
    
    /**
     * Clears all items and resets discounts.
     */
    public void clear() {
        for (int i = 0; i < itemCount; i++) {
            items[i] = null;
        }
        itemCount = 0;
        globalDiscountPercent = 0.0;
        currentTotal = 0.0;
    }
}