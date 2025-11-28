package possportstore;

/**
 * Represents a shoe product within the inventory.
 * Extends the {@link Product} class with specific attributes like size, color, and shoe type.
 */
public class Shoe extends Product {

    /**
     * Enumeration defining the specific categories of shoes available.
     */
    public enum TypeShoe {
        RUNNING, BASKETBALL, CASUAL, BOOTS
    }

    private String size;
    private String color;
    private TypeShoe type;

    /**
     * Default constructor. Creates an empty Shoe instance.
     */
    public Shoe() {
        super();
    }

    /**
     * Constructs a new Shoe product with all details.
     *
     * @param idProduct   The unique identifier.
     * @param name        The product name.
     * @param price       The unit price.
     * @param stock       The initial stock quantity.
     * @param description The product description.
     * @param size        The shoe size (e.g., "42", "9.5 US").
     * @param color       The primary color of the shoe.
     * @param type        The category of the shoe (from {@link TypeShoe}).
     */
    public Shoe(String idProduct, String name, double price, int stock, String description, 
                String size, String color, TypeShoe type) {
        super(idProduct, name, price, stock, description);
        this.size = size;
        this.color = color;
        this.type = type;
    }

    // --- Getters and Setters ---

    /**
     * Gets the shoe size.
     * @return The size string.
     */
    public String getSize() { return size; }

    /**
     * Sets the shoe size.
     * @param size The new size.
     */
    public void setSize(String size) { this.size = size; }

    /**
     * Gets the shoe color.
     * @return The color string.
     */
    public String getColor() { return color; }

    /**
     * Sets the shoe color.
     * @param color The new color.
     */
    public void setColor(String color) { this.color = color; }

    /**
     * Gets the category type of the shoe.
     * @return The {@link TypeShoe} enum value.
     */
    public TypeShoe getType() { return type; }

    /**
     * Sets the category type of the shoe.
     * @param type The new {@link TypeShoe} value.
     */
    public void setType(TypeShoe type) { this.type = type; }

    /**
     * Returns a string representation of the Shoe object.
     * @return A formatted string containing size, color, and type.
     */
    @Override
    public String toString() {
        return "Shoe{" + "size=" + size + ", color=" + color + ", type=" + type + '}';
    }
}