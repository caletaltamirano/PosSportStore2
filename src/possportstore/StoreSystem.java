package possportstore;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import possportstore.CurrentSale.CartItem;
import possportstore.Sale.Invoice;
import possportstore.User.Role;

/**
 * Main Controller (Facade) for the Application.
 * <p>
 * This class coordinates interactions between the UI, the User Manager, the Sale Manager,
 * and the Inventory system.
 * </p>
 */
public class StoreSystem {
    
    private Product[] products;
    private int productCount; 
    
    private final Sale saleManager; 
    private final UserManager userManager;

    /**
     * Initializes the StoreSystem, loads resources, and prepares sub-managers.
     */
    public StoreSystem() {
        this.products = new Product[100]; 
        this.productCount = 0;
        this.saleManager = new Sale(); 
        this.userManager = new UserManager();
        
        loadProducts(); 
        Sale.loadInvoicesFromFile(); 
    }

    // --- System Getters ---

    /**
     * Gets the current product inventory.
     * @return An array of all products.
     */
    public Product[] getProducts() { return getInventoryForDisplay(); }

    /**
     * Gets the total count of products in memory.
     * @return The product count.
     */
    public int getProductCount() { return productCount; }

    /**
     * Gets the Sale Manager instance.
     * @return The {@link Sale} object.
     */
    public Sale getSaleManager() { return saleManager; }

    /**
     * Gets the User Manager instance.
     * @return The {@link UserManager} object.
     */
    public UserManager getUserManager() { return userManager; }

    /**
     * Generates the next Product ID based on current count.
     * @return The next ID as a String.
     */
    public String getNextProductId() { return String.valueOf(productCount + 1); }

    /**
     * Gets the currently logged-in user.
     * @return The authenticated {@link User}.
     */
    public User getAuthenticatedUser() { return userManager.getCurrentUser(); }

    // --- User Management Delegation ---

    /**
     * Gets the list of users for display.
     * @return An array of {@link User} objects.
     */
    public User[] getUsers() { return userManager.getUsersForDisplay(); }

    /**
     * Adds a new user to the system.
     * @param username The new username.
     * @param password The new password.
     * @param role The user role.
     * @return true if successful.
     */
    public boolean addUser(String username, String password, Role role) { return userManager.addUser(username, password, role); }

    /**
     * Updates an existing user.
     * @param id The user ID.
     * @param u The new username.
     * @param p The new password.
     * @param r The new role.
     * @return true if successful.
     */
    public boolean updateUser(int id, String u, String p, Role r) { return userManager.updateUser(id, u, p, r); }

    /**
     * Deletes a user by ID.
     * @param id The ID of the user to delete.
     * @return true if successful.
     */
    public boolean deleteUser(int id) { return userManager.deleteUser(id); }
    
    // --- Inventory Management ---
    
    /**
     * Adds a new product to the inventory.
     * @param p The product to add.
     * @return true if added successfully, false if inventory is full.
     */
    public boolean addProduct(Product p) {
        if (productCount < products.length) {
            products[productCount++] = p;
            saveProducts();
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Inventory full.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Finds a product by its unique ID.
     * @param idProduct The ID to search for.
     * @return The {@link Product} if found, null otherwise.
     */
    public Product findProductById(String idProduct) {
        for (int i = 0; i < productCount; i++) {
            if (products[i] != null && products[i].getIdProduct().equals(idProduct)) {
                return products[i];
            }
        }
        return null;
    }

    /**
     * Searches for a product by ID or Name (case insensitive).
     * @param search The search query.
     * @return The {@link Product} if found, null otherwise.
     */
    public Product searchProduct(String search) {
        for (int i = 0; i < productCount; i++) {
            Product product = products[i];
            if (product != null) {
                if (product.getIdProduct().equalsIgnoreCase(search) || product.getName().equalsIgnoreCase(search)) {
                    return product;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns a safe copy of the inventory array.
     * @return An array of products.
     */
    public Product[] getInventoryForDisplay() {
        return Arrays.stream(products)
                     .filter(Objects::nonNull)
                     .toArray(Product[]::new);
    }

    /**
     * Updates the stock of a specific product.
     * @param idProduct The ID of the product.
     * @param newStock  The new stock quantity.
     * @return true if updated, false if product not found.
     */
    public boolean updateProductStock(String idProduct, int newStock) {
        Product productToUpdate = findProductById(idProduct);
        if (productToUpdate != null) {
            productToUpdate.setStock(newStock);
            saveProducts(); 
            return true;
        }
        return false;
    }

    /**
     * Deletes a product from the inventory.
     * @param idProduct The ID of the product to delete.
     * @return true if deleted, false if not found.
     */
    public boolean deleteProduct(String idProduct) {
        int indexToDelete = -1;
        for (int i = 0; i < productCount; i++) {
            if (products[i] != null && products[i].getIdProduct().equals(idProduct)) {
                indexToDelete = i;
                break;
            }
        }
        
        if (indexToDelete == -1) return false;

        for (int i = indexToDelete; i < productCount - 1; i++) {
            products[i] = products[i + 1];
        }
        
        products[productCount - 1] = null;
        productCount--;
        saveProducts();
        return true;
    }
    
    // --- Persistence (Products) ---
    
    /**
     * Saves the current inventory to the products file.
     */
    public void saveProducts() {
        try (PrintWriter writer = new PrintWriter("productos.txt")) {
            for (int i = 0; i < productCount; i++) {
                Product p = products[i];
                if (p == null) continue;

                if (p instanceof Shoe s) {
                    writer.println("Shoe;" + s.getIdProduct() + ";" + s.getName() + ";" + s.getDescription() + ";"
                            + s.getPrice() + ";" + s.getStock() + ";" + s.getSize() + ";" + s.getColor() + ";" + s.getType());
                } else if (p instanceof Clothe c) {
                    writer.println("Clothe;" + c.getIdProduct() + ";" + c.getName() + ";" + c.getDescription() + ";"
                            + c.getPrice() + ";" + c.getStock() + ";" + c.getSize() + ";" + c.getColor() + ";" + c.getType());
                } else if (p instanceof Accessories a) {
                    writer.println("Accessories;" + a.getIdProduct() + ";" + a.getName() + ";" + a.getDescription() + ";"
                            + a.getPrice() + ";" + a.getStock() + ";" + a.getBrand() + ";" + a.getType());
                }
            }
        } catch (Exception e) {
             System.err.println("Error saving products: " + e.getMessage());
        }
    }

    /**
     * Loads products from the text file at startup.
     */
    public void loadProducts() {
        File file = new File("productos.txt");
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine() && productCount < products.length) {
                String line = fileScanner.nextLine();
                try {
                    String[] data = line.split(";");
                    
                    if (data.length < 7) continue; 
                    
                    String type = data[0];
                    String id = data[1]; 
                    String name = data[2];
                    String desc = data[3];
                    double price = Double.parseDouble(data[4]);
                    int stock = Integer.parseInt(data[5]);

                    switch (type) {
                        case "Shoe" -> products[productCount++] = new Shoe(id, name, price, stock, desc, data[6], data[7], Shoe.TypeShoe.valueOf(data[8]));
                        case "Clothe" -> products[productCount++] = new Clothe(id, name, price, stock, desc, data[6], data[7], Clothe.TypeClothe.valueOf(data[8]));
                        case "Accesories", "Accessories" -> products[productCount++] = new Accessories(id, name, price, stock, desc, data[6], Accessories.TypeAccessories.valueOf(data[7]));
                    }
                } catch (Exception e) {
                    System.err.println("Skipping invalid product line: " + line);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading products file: " + e.getMessage());
        }
    }
    
    // --- Sales Logic ---

    /**
     * Finalizes the current sale transaction.
     * Updates stock, creates invoice with discounts, and saves data.
     *
     * @param cart The current shopping cart.
     * @return The generated {@link Invoice}, or null if cart is empty.
     */
    public Sale.Invoice finalizeSale(CurrentSale cart) {
        if (cart.getItems().length == 0) return null;

        String cashierName = cart.getAuthenticatedUser().getUsername();
        double totalAmount = cart.getCurrentTotal();
        double globalDisc = cart.getGlobalDiscountPercent();
        
        for (CartItem item : cart.getItems()) {
            item.getProduct().reduceStock(item.getQuantity()); 
        }
        
        // Pass global discount to creator
        Sale.Invoice finalInvoice = Sale.createInvoice(totalAmount, cashierName, cart.getItems(), globalDisc); 
        saveProducts(); 
        return finalInvoice;
    }
    
    // --- Returns Logic ---
    
    /**
     * Process a return for a specific product.
     * Increases the inventory stock for the returned item, updates the invoice,
     * and persists the changes.
     *
     * @param invoiceId        The ID of the invoice containing the item.
     * @param productId        The ID of the product being returned.
     * @param quantityToReturn The quantity to return to stock.
     * @return true if the product was found and updated, false otherwise.
     */
    public boolean processReturn(int invoiceId, String productId, int quantityToReturn) {
        Product product = findProductById(productId);
        
        // 1. Update Inventory
        if (product != null) {
            product.increaseStock(quantityToReturn);
            saveProducts(); // Persist stock changes
        } else {
            return false; // Product not found in inventory (rare inconsistency)
        }
        
        // 2. Update Invoice Record
        boolean invoiceUpdated = Sale.processItemReturn(invoiceId, productId, quantityToReturn);
        
        return invoiceUpdated;
    }
    
    // --- Dashboard Metrics ---

    /**
     * Gets the total number of invoices in the system.
     * @return The total invoice count.
     */
    public int getTotalInvoicesCount() { return Sale.getInvoicesForDisplay().length; }

    /**
     * Calculates the total revenue from all sales, including VAT.
     * @return The total revenue as a double.
     */
    public double getTotalSalesRevenue() {
        double total = 0;
        for (Sale.Invoice invoice : Sale.getInvoicesForDisplay()) {
            total += invoice.getTotal() * 1.13; 
        }
        return total;
    }

    /**
     * Counts the number of products with low stock (<= 5).
     * @return The count of low stock items.
     */
    public int countLowStockProducts() {
        int lowCount = 0;
        for (int i = 0; i < productCount; i++) {
            if (products[i] != null && products[i].getStock() <= 5) {
                lowCount++;
            }
        }
        return lowCount;
    }

    /**
     * Retrieves the specific list of products with low stock.
     * @return Array of products with stock <= 5.
     */
    public Product[] getLowStockProducts() {
        int count = countLowStockProducts();
        Product[] lowStock = new Product[count];
        int idx = 0;
        for (int i = 0; i < productCount; i++) {
            if (products[i] != null && products[i].getStock() <= 5) {
                lowStock[idx++] = products[i];
            }
        }
        return lowStock;
    }

    /**
     * Calculates the total number of units in inventory.
     * @return The total unit count.
     */
    public int getTotalUnitsInStock() {
        int totalUnits = 0;
        for (int i = 0; i < productCount; i++) {
            if (products[i] != null) {
                totalUnits += products[i].getStock();
            }
        }
        return totalUnits;
    }
    

    // --- Printing Logic ---

    /**
     * Sends an invoice to the default printer using the ReceiptPrinter utility.
     * @param invoice The invoice to print.
     */
    public void printInvoice(Sale.Invoice invoice) {
        if (invoice != null) {
            // Run in a separate thread to not block the UI
            new Thread(() -> {
                ui.ReceiptPrinter printer = new ui.ReceiptPrinter(invoice);
                printer.print();
            }).start();
        }
    }
}
