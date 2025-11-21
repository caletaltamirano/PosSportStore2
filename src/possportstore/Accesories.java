package possportstore;

public class Accesories extends Product {

    private String brand;
    private TypeAccesories type;

    // Enum (Asegúrate de que este Enum exista)
    public enum TypeAccesories {
        GORRA, GUANTE, BOLSO, RELOJ
    }

    public Accesories() {
    }

    public Accesories(String brand, TypeAccesories type) {
        this.brand = brand;
        this.type = type;
    }

    public Accesories(String brand, TypeAccesories type, double price,
            int stock, String description, String idProduct, String name) {
        super(price, stock, description, idProduct, name);
        this.brand = brand;
        this.type = type;
    }

    // Getters y Setters
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public TypeAccesories getType() { return type; }
    public void setType(TypeAccesories type) { this.type = type; }

    @Override
    public String toString() {
        return "Accesories{" + "brand=" + brand + ", type=" + type + '}';
    }
}