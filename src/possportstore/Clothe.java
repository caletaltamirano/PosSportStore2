package possportstore;

public class Clothe extends Product {

    private String size;
    private String color;
    private TypeClothe type;

    // Enum (Asegúrate de que este Enum exista)
    public enum TypeClothe {
        CAMISA, PANTALON, CHAQUETA, CALCETINES
    }

    public Clothe() {
    }

    public Clothe(String size, String color, TypeClothe type) {
        this.size = size;
        this.color = color;
        this.type = type;
    }

    public Clothe(String size, String color, TypeClothe type, double price,
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
    public TypeClothe getType() { return type; }
    public void setType(TypeClothe type) { this.type = type; }

    @Override
    public String toString() {
        return "Clothe{" + "size=" + size + ", color=" + color + ", type="
                + type + '}';
    }
}