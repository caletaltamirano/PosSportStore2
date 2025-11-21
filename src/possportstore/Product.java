package possportstore;

public class Product {

    private double price;
    private int stock;
    private String description;
    private String idProduct;
    private String name;
    private String newId;

    
    public Product() {
        this("0", "Producto Genérico", 0.0, 0, "Descripción por defecto");
    }
    
    public Product(String newId, String name1, double price1, int stock1, String description1) {
    }

    public Product(double price, int stock, String description, String idProduct, String name) {
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.idProduct = idProduct;
        this.name = name;
    }

    // Getters y Setters
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIdProduct() { return idProduct; }
    public void setIdProduct(String idProduct) { this.idProduct = idProduct; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // Método para reducir el stock después de una venta
    public void reduceStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
        } else {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + this.name);
        }
    }
}