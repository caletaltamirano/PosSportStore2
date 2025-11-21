package possportstore;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import possportstore.CurrentSale;
import possportstore.CurrentSale.CartItem;
import possportstore.Sale.Invoice;
import possportstore.User.Role; // Importar Role para los métodos de gestión de usuarios

public class StSystem {
    
    private Product[] products;
    private int customerCount;
    private int productCount; // Usado como contador y como siguiente ID
    
    // Gestores centralizados
    private final Sale saleManager; 
    private final UserManager userManager;

    public StSystem() {
        // Aumentamos el tamaño del inventario para mayor flexibilidad
        this.products = new Product[100]; 
        this.customerCount = 0;
        this.productCount = 0;
        this.saleManager = new Sale(); 
        this.userManager = new UserManager(); // Inicialización crítica para el login
        
        cargarProductos(); 
        Sale.cargarFacturasDesdeArchivo(); 
    }

    // -----------------------------------------------------------------
    // --- Getters del Sistema ---
    // -----------------------------------------------------------------
    
    public Product[] getProducts() { 
        return getInventoryForDisplay(); 
    }
    
    public int getProductCount() { return productCount; }
    public Sale getSaleManager() { return saleManager; }
    public UserManager getUserManager() { return userManager; }
    
    /**
     * Genera y retorna el próximo ID de producto como String.
     */
    public String getNextProductId() {
        return String.valueOf(productCount + 1);
    }
    
    public User getAuthenticatedUser() {
        return userManager.getCurrentUser(); 
    }

    // -----------------------------------------------------------------
    // --- Lógica de Gestión de Usuarios (Delegada en UserManager) 🔑 ---
    // -----------------------------------------------------------------

    /**
     * Obtiene el arreglo de usuarios para mostrar en la tabla.
     */
    public User[] getUsers() {
        return userManager.getUsersForDisplay();
    }

    /**
     * Añade un nuevo usuario.
     */
    public boolean addUser(String username, String password, Role role) {
        return userManager.addUser(username, password, role);
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    public boolean updateUser(int id, String newUsername, String newPassword, Role newRole) {
        return userManager.updateUser(id, newUsername, newPassword, newRole);
    }

    /**
     * Elimina un usuario por ID.
     */
    public boolean deleteUser(int id) {
        return userManager.deleteUser(id);
    }
    
    // -----------------------------------------------------------------
    // --- Métodos de Inventario ---
    // -----------------------------------------------------------------
    
    public boolean addProduct(Product p) {
        if (productCount < products.length) {
            products[productCount++] = p;
            guardarProductos();
            return true; // Producto agregado con éxito
        } else {
            JOptionPane.showMessageDialog(null, 
                "Inventario lleno, no se pueden agregar más productos.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false; // Inventario lleno
        }
    }

    /**
     * Busca un producto por ID. Útil para Modificar/Eliminar Stock.
     */
    public Product findProductById(String idProduct) {
        // Reutilizamos la lógica de búsqueda por ID/Nombre, buscando solo por ID para seguridad
        for (int i = 0; i < productCount; i++) {
            if (products[i].getIdProduct().equals(idProduct)) {
                return products[i];
            }
        }
        return null;
    }

    public Product buscarProductoPorIdONombre(String search) {
        for (int i = 0; i < productCount; i++) {
            Product product = products[i];
            if (product.getIdProduct().equalsIgnoreCase(search)
                    || product.getName().equalsIgnoreCase(search)) {
                return product;
            }
        }
        return null;
    }
    
    public Product[] getInventoryForDisplay() {
        return Arrays.copyOf(products, productCount);
    }
    
    // --- NUEVOS MÉTODOS DE MODIFICACIÓN Y ELIMINACIÓN DE PRODUCTOS ---

    /**
     * Actualiza el stock de un producto existente y persiste el cambio.
     */
    public boolean updateProductStock(String idProduct, int newStock) {
        Product productToUpdate = findProductById(idProduct);
        
        if (productToUpdate != null) {
            productToUpdate.setStock(newStock); // Asegúrese que Product tiene setStock()
            guardarProductos(); 
            return true;
        }
        return false;
    }

    /**
     * Busca y elimina un producto del array por su ID.
     */
    public boolean deleteProduct(String idProduct) {
        int indexToDelete = -1;
        
        // 1. Buscar el índice del producto por ID
        for (int i = 0; i < productCount; i++) {
            if (products[i].getIdProduct().equals(idProduct)) {
                indexToDelete = i;
                break;
            }
        }
        
        if (indexToDelete == -1) {
            return false; // Producto no encontrado
        }

        // 2. Eliminar el producto moviendo los elementos siguientes (sobreescribiendo)
        for (int i = indexToDelete; i < productCount - 1; i++) {
            products[i] = products[i + 1];
        }
        
        // 3. Reducir el contador y anular la última referencia
        products[productCount - 1] = null;
        productCount--;
        
        // 4. Guardar los cambios en el archivo de texto
        guardarProductos();
        
        return true;
    }
    
    // -----------------------------------------------------------------
    // --- Persistencia de Productos ---
    // -----------------------------------------------------------------
    
    public void guardarProductos() {
        try (PrintWriter writer = new PrintWriter("productos.txt")) {
            for (int i = 0; i < productCount; i++) {
                Product p = products[i];
                
                if (p instanceof Shoe shoe) {
                    writer.println("Shoe;" + shoe.getIdProduct() + ";" + shoe.getName() + ";" + shoe.getDescription() + ";"
                            + shoe.getPrice() + ";" + shoe.getStock() + ";" + shoe.getSize() + ";" + shoe.getColor() + ";"
                            + shoe.getType());
                            
                } else if (p instanceof Clothe clothe) {
                    writer.println("Clothe;" + clothe.getIdProduct() + ";" + clothe.getName() + ";" + clothe.getDescription() + ";"
                            + clothe.getPrice() + ";" + clothe.getStock() + ";" + clothe.getSize() + ";" + clothe.getColor() + ";"
                            + clothe.getType());
                            
                } else if (p instanceof Accesories acc) {
                    writer.println("Accesories;" + acc.getIdProduct() + ";" + acc.getName() + ";" + acc.getDescription() + ";"
                            + acc.getPrice() + ";" + acc.getStock() + ";" + acc.getBrand() + ";" + acc.getType());
                } else {
                    // Ignorar productos base sin tipo específico
                }
            }
        } catch (Exception e) {
             System.err.println(" Error al guardar productos: " + e.getMessage());
        }
    }

    public void cargarProductos() {
        File file = new File("productos.txt");
        if (!file.exists()) {
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine() && productCount < products.length) {
                String line = fileScanner.nextLine();
                String[] data = line.split(";");
                
                if (data.length < 7) continue; // Validación básica
                
                String tipo = data[0];
                String id = data[1]; 
                String name = data[2];
                String description = data[3];
                double price = Double.parseDouble(data[4]);
                int stock = Integer.parseInt(data[5]);

                switch (tipo) {
                    case "Shoe" -> {
                        String size = data[6];
                        String color = data[7];
                        Shoe.TypeShoe type = Shoe.TypeShoe.valueOf(data[8]); 
                        products[productCount++] = new Shoe(size, color, type, price, stock, description, id, name);
                    }
                    case "Clothe" -> {
                        String size = data[6];
                        String color = data[7];
                        Clothe.TypeClothe type = Clothe.TypeClothe.valueOf(data[8]);
                        products[productCount++] = new Clothe(size, color, type, price, stock, description, id, name);
                    }
                    case "Accesories" -> {
                        String brand = data[6];
                        Accesories.TypeAccesories type = Accesories.TypeAccesories.valueOf(data[7]);
                        products[productCount++] = new Accesories(brand, type, price, stock, description, id, name);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(" Error al cargar productos: " + e.getMessage());
        }
    }
    
    // -----------------------------------------------------------------
    // --- Lógica de Venta y Facturas ---
    // -----------------------------------------------------------------

    public Sale.Invoice finalizeSale(CurrentSale cart) {
        if (cart.getItems().length == 0) {
            JOptionPane.showMessageDialog(null, "El carrito está vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return null; 
        }

        String cashierName = cart.getAuthenticatedUser().getUsername();
        double totalAmount = cart.getCurrentTotal();
        
        // 1. Reducir Stock para cada producto en el carrito
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

            product.reduceStock(quantity); 
        }
        
        // 2. Crear la factura única usando la clase Sale (Invoice Manager)
        Sale.Invoice finalInvoice = possportstore.Sale.createInvoiceFromTotal(
            totalAmount, 
            cashierName
        ); 

        // 3. Guardar el estado de los productos (Stock)
        guardarProductos(); 
        
        return finalInvoice;
    }
    
    /**
     * Obtiene el arreglo de todas las facturas del Sale Manager.
     */
    public Invoice[] getInvoices() {
        return possportstore.Sale.getInvoicesForDisplay(); 
    }
    // --- Métodos de resumen necesarios en StSystem.java ---

// --- Métodos de resumen necesarios en StSystem.java ---

// Obtiene la cantidad total de facturas (Ventas totales)
public int getTotalInvoicesCount() {
    // 🔑 CORRECCIÓN: Usar la clase Sale directamente
    return possportstore.Sale.getInvoicesForDisplay().length;
}

// Calcula el valor total de todas las ventas (Ingresos Brutos)
public double getTotalSalesRevenue() {
    double total = 0;
    // 🔑 CORRECCIÓN: Usar la clase Sale directamente
    for (Sale.Invoice invoice : possportstore.Sale.getInvoicesForDisplay()) {
        total += invoice.getTotal(); 
    }
    return total;
}

// Cuenta la cantidad de productos con stock bajo (ej. stock <= 5)
public int countLowStockProducts() {
    int lowCount = 0;
    for (Product p : getInventoryForDisplay()) {
        // Asegúrate de que el método getStock() existe en Product
        if (p.getStock() <= 5) {
            lowCount++;
        }
    }
    return lowCount;
}

// Calcula la cantidad total de unidades en el inventario
public int getTotalUnitsInStock() {
    int totalUnits = 0;
    for (Product p : getInventoryForDisplay()) {
        totalUnits += p.getStock();
    }
    return totalUnits;
}


}