package possportstore;

public class Shoe extends Product {

    private String size;
    private String color;
    private TypeShoe type;

    // Enum (Asegúrate de que este Enum exista)
    public enum TypeShoe {
        RUNNING, BASKETBALL, CASUAL, BOTAS
    }

    public Shoe() {
    }

    public Shoe(String size, String color, TypeShoe type) {
        this.size = size;
        this.color = color;
        this.type = type;
    }

    public Shoe(String size, String color, TypeShoe type, double price,
            int stock, String description, String idProduct, String name) {
        super(price, stock, description, idProduct, name);
        this.size = size;
        this.color = color;
        this.type = type;
    }

    // Getters y Setters
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public TypeShoe getType() { return type; }
    public void setType(TypeShoe type) { this.type = type; }

    @Override
    public String toString() {
        return "Shoe{" + "size=" + size + ", color=" + color + ", type=" + type + '}';
    }
}