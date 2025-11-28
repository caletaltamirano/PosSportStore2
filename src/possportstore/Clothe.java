package possportstore;

/**
 * Represents a clothing product within the inventory.
 * Extends the {@link Product} class with specific attributes like size, color, and clothing type.
 */
public class Clothe extends Product {

    /**
     * Enumeration defining the specific categories of clothing available.
     */
    public enum TypeClothe {
        SHIRT, PANTS, JACKET, SOCKS
    }

    private String size;
    private String color;
    private TypeClothe type;

    /**
     * Default constructor. Creates an empty Clothe instance.
     */
    public Clothe() {
        super();
    }

    /**
     * Constructs a new Clothe product with all details.
     *
     * @param idProduct   The unique identifier.
     * @param name        The product name.
     * @param price       The unit price.
     * @param stock       The initial stock quantity.
     * @param description The product description.
     * @param size        The clothing size (e.g., "M", "L", "XL").
     * @param color       The primary color of the item.
     * @param type        The category of the clothing (from {@link TypeClothe}).
     */
    public Clothe(String idProduct, String name, double price, int stock, String description,
                  String size, String color, TypeClothe type) {
        super(idProduct, name, price, stock, description);
        this.size = size;
        this.color = color;
        this.type = type;
    }

    // --- Getters and Setters ---

    /**
     * Gets the clothing size.
     * @return The size string.
     */
    public String getSize() { return size; }

    /**
     * Sets the clothing size.
     * @param size The new size.
     */
    public void setSize(String size) { this.size = size; }

    /**
     * Gets the clothing color.
     * @return The color string.
     */
    public String getColor() { return color; }

    /**
     * Sets the clothing color.
     * @param color The new color.
     */
    public void setColor(String color) { this.color = color; }

    /**
     * Gets the category type of the clothing.
     * @return The {@link TypeClothe} enum value.
     */
    public TypeClothe getType() { return type; }

    /**
     * Sets the category type of the clothing.
     * @param type The new {@link TypeClothe} value.
     */
    public void setType(TypeClothe type) { this.type = type; }

    /**
     * Returns a string representation of the Clothe object.
     * @return A formatted string containing size, color, and type.
     */
    @Override
    public String toString() {
        return "Clothe{" + "size=" + size + ", color=" + color + ", type=" + type + '}';
    }
}