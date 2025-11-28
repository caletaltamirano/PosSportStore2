package possportstore;

/**
 * Represents an accessory product within the inventory.
 * Extends the {@link Product} class with specific attributes like brand and accessory type.
 */
public class Accessories extends Product {

    /**
     * Enumeration defining the specific categories of accessories available.
     */
    public enum TypeAccessories {
        CAP, GLOVES, BAG, WATCH
    }

    private String brand;
    private TypeAccessories type;

    /**
     * Default constructor. Creates an empty Accessories instance.
     */
    public Accessories() {
        super();
    }

    /**
     * Constructs a new Accessory product with all details.
     *
     * @param idProduct   The unique identifier.
     * @param name        The product name.
     * @param price       The unit price.
     * @param stock       The initial stock quantity.
     * @param description The product description.
     * @param brand       The brand name of the accessory.
     * @param type        The category of the accessory (from {@link TypeAccessories}).
     */
    public Accessories(String idProduct, String name, double price, int stock, String description,
                       String brand, TypeAccessories type) {
        super(idProduct, name, price, stock, description);
        this.brand = brand;
        this.type = type;
    }

    // --- Getters and Setters ---

    /**
     * Gets the brand of the accessory.
     * @return The brand name.
     */
    public String getBrand() { return brand; }

    /**
     * Sets the brand of the accessory.
     * @param brand The new brand name.
     */
    public void setBrand(String brand) { this.brand = brand; }

    /**
     * Gets the category type of the accessory.
     * @return The {@link TypeAccessories} enum value.
     */
    public TypeAccessories getType() { return type; }

    /**
     * Sets the category type of the accessory.
     * @param type The new {@link TypeAccessories} value.
     */
    public void setType(TypeAccessories type) { this.type = type; }

    /**
     * Returns a string representation of the Accessories object.
     * @return A formatted string containing brand and type.
     */
    @Override
    public String toString() {
        return "Accessories{" + "brand=" + brand + ", type=" + type + '}';
    }
}