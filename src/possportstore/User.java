package possportstore;

/**
 * Represents a system user (e.g., Administrator or Cashier).
 * Contains authentication credentials and role-based access information.
 */
public class User {

    /**
     * Defines the available roles within the system.
     */
    public enum Role {
        ADMIN, CAJERO
    }

    private final int id;
    private String username;
    private String passwordHash; 
    private Role role;

    /**
     * Constructs a new User.
     *
     * @param id           The unique user ID.
     * @param username     The login username.
     * @param passwordHash The hashed password (or plain text for this prototype).
     * @param role         The user's role ({@link Role}).
     */
    public User(int id, String username, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Gets the user ID.
     * @return The unique ID integer.
     */
    public int getId() { return id; }

    /**
     * Gets the username.
     * @return The username string.
     */
    public String getUsername() { return username; }

    /**
     * Sets the username.
     * @param username The new username.
     */
    public void setUsername(String username) { this.username = username; }
    
    /**
     * Gets the stored password (hash).
     * @return The password string.
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Sets the stored password.
     * @param passwordHash The new password string.
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Gets the user's role.
     * @return The {@link Role} enum.
     */
    public Role getRole() { return role; }

    /**
     * Sets the user's role.
     * @param role The new role.
     */
    public void setRole(Role role) { this.role = role; }

    /**
     * Formats the user data for file persistence.
     * @return A semicolon-separated string: ID;Username;Password;Role
     */
    @Override
    public String toString() {
        return id + ";" + username + ";" + passwordHash + ";" + role.name();
    }
}