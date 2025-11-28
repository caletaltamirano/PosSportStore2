package possportstore;

/**
 * Represents a generic product in the inventory.
 * <p>
 * This class serves as the base entity for specific product categories
 * such as {@link Shoe}, {@link Clothe}, and {@link Accessories}.
 * </p>
 */
public class Product {

    private String idProduct;
    private String name;
    private double price;
    private int stock;
    private String description;

    /**
     * Default constructor. Initializes a product with placeholder values.
     */
    public Product() {
        this("0", "Generic Product", 0.0, 0, "Default description");
    }

    /**
     * Constructs a new Product with the specified details.
     *
     * @param idProduct   The unique identifier for the product.
     * @param name        The display name of the product.
     * @param price       The unit price of the product.
     * @param stock       The initial quantity in stock.
     * @param description A brief description of the product.
     */
    public Product(String idProduct, String name, double price, int stock, String description) {
        this.idProduct = idProduct;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    // --- Business Logic ---

    /**
     * Reduces the stock of the product by a specified quantity.
     * <p>
     * This method is typically called after a sale is finalized.
     * </p>
     *
     * @param quantity The amount of units to subtract from the stock.
     * @throws IllegalArgumentException if the resulting stock would be negative.
     */
    public void reduceStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
        } else {
            throw new IllegalArgumentException("Insufficient stock for product: " + this.name);
        }
    }

    /**
     * Increases the stock of the product.
     * <p>
     * This method is used when processing returns or restocking.
     * </p>
     *
     * @param quantity The amount to add back to stock.
     */
    public void increaseStock(int quantity) {
        if (quantity > 0) {
            this.stock += quantity;
        }
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique product ID.
     * @return The product ID string.
     */
    public String getIdProduct() { return idProduct; }

    /**
     * Sets the unique product ID.
     * @param idProduct The new ID string.
     */
    public void setIdProduct(String idProduct) { this.idProduct = idProduct; }

    /**
     * Gets the product name.
     * @return The name of the product.
     */
    public String getName() { return name; }

    /**
     * Sets the product name.
     * @param name The new name.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the unit price.
     * @return The price as a double.
     */
    public double getPrice() { return price; }

    /**
     * Sets the unit price.
     * @param price The new price.
     */
    public void setPrice(double price) { this.price = price; }

    /**
     * Gets the current stock quantity.
     * @return The number of units in stock.
     */
    public int getStock() { return stock; }

    /**
     * Sets the stock quantity.
     * @param stock The new stock level.
     */
    public void setStock(int stock) { this.stock = stock; }

    /**
     * Gets the product description.
     * @return A string description of the product.
     */
    public String getDescription() { return description; }

    /**
     * Sets the product description.
     * @param description The new description text.
     */
    public void setDescription(String description) { this.description = description; }
}