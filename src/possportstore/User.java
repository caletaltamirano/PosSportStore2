package possportstore;

public class User {
    
    public enum Role {
        ADMIN,
        CAJERO
        // Otros roles
    }

    // El ID debe ser final ya que es la clave única (no puede cambiar)
    private final int id; 
    
    // Estos campos DEBEN ser mutables para poder usar setUsername y setRole
    private String username;
    private String passwordHash; // Lo renombramos para reflejar que es el hash/contraseña
    private Role role;

    // ----------------------------------------------------------------------
    // CONSTRUCTOR ACTUALIZADO (Añade ID y usa el hash para la contraseña)
    // ----------------------------------------------------------------------
    public User(int id, String username, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    
    // ----------------------------------------------------------------------
    // GETTERS REQUERIDOS POR EL SISTEMA Y LA VISTA
    // ----------------------------------------------------------------------
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() { // Usamos Hash para seguridad
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    // ----------------------------------------------------------------------
    // SETTERS REQUERIDOS POR EL MÉTODO updateUser()
    // ----------------------------------------------------------------------
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Establece la nueva contraseña (asumiendo que viene ya hasheada o se hashea aquí).
     */
    public void setPasswordHash(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    // ----------------------------------------------------------------------
    // toString para Guardar en Archivo
    // ----------------------------------------------------------------------
    @Override
    public String toString() {
        // CRÍTICO: Añadir el ID al formato de archivo
        return id + ";" + username + ";" + passwordHash + ";" + role.name();
    }
}