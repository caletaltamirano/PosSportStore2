package possportstore;

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import javax.swing.JOptionPane;
import possportstore.User.Role;

/**
 * Manages User entities, including authentication, CRUD operations, and file persistence.
 */
public class UserManager {
    
    private static final int MAX_USERS = 10;
    private static final String FILE_NAME = "users.txt";
    
    private User[] users;
    private int userCount;
    private int nextUserId;
    private User currentUser; 

    /**
     * Initializes the UserManager, allocates memory for users, and loads data from the file.
     */
    public UserManager() {
        this.users = new User[MAX_USERS];
        this.userCount = 0;
        this.nextUserId = 1; 
        loadUsers(); 
    }
    
    /**
     * Gets the currently logged-in user.
     * @return The authenticated {@link User} object, or null if no user is logged in.
     */
    public User getCurrentUser() {
        return this.currentUser;
    }
    
    /**
     * Returns a copy of the active users array, trimmed to the actual count.
     * Used for displaying users in tables.
     * @return An array of {@link User} objects.
     */
    public User[] getUsersForDisplay() {
        return Arrays.copyOf(users, userCount);
    }

    /**
     * Authenticates a user against the stored records.
     *
     * @param username The input username.
     * @param password The input password.
     * @return The authenticated {@link User} if credentials match, null otherwise.
     */
    public User authenticate(String username, String password) {
        for (int i = 0; i < userCount; i++) {
            User user = users[i];
            if (user.getUsername().equals(username) && user.getPasswordHash().equals(password)) {
                this.currentUser = user; 
                return user;
            }
        }
        this.currentUser = null;
        return null;
    }
    
    // --- CRUD Operations ---

    /**
     * Adds a new user to the system.
     * Checks for username uniqueness before adding.
     *
     * @param username The new username.
     * @param password The new password.
     * @param role     The role assigned to the new user.
     * @return true if the user was successfully added, false otherwise.
     */
    public boolean addUser(String username, String password, Role role) {
        if (userCount >= MAX_USERS) {
            JOptionPane.showMessageDialog(null, "User limit reached.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUsername().equals(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        User newUser = new User(nextUserId++, username, password, role);
        users[userCount++] = newUser;
        saveUsers();
        return true;
    }
    
    /**
     * Updates an existing user's information.
     *
     * @param id          The ID of the user to update.
     * @param newUsername The new username (must be unique).
     * @param newPassword The new password (if null or empty, password remains unchanged).
     * @param newRole     The new role.
     * @return true if the update was successful, false if the user was not found or username conflict occurred.
     */
    public boolean updateUser(int id, String newUsername, String newPassword, Role newRole) {
        for (int i = 0; i < userCount; i++) {
            User u = users[i];
            
            if (u.getId() == id) {
                // Check for duplicate username in other records
                for (int j = 0; j < userCount; j++) {
                    if (i != j && users[j].getUsername().equals(newUsername)) {
                        JOptionPane.showMessageDialog(null, "Username already in use.", "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                
                u.setUsername(newUsername);
                u.setRole(newRole);
                
                if (newPassword != null && !newPassword.isEmpty()) {
                    u.setPasswordHash(newPassword);
                }
                
                saveUsers();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Deletes a user from the system by ID.
     * Prevents deletion of the root administrator (ID 1).
     *
     * @param id The ID of the user to delete.
     * @return true if the user was deleted, false otherwise.
     */
    public boolean deleteUser(int id) {
        if (id == 1) { // Protect Root Admin
            JOptionPane.showMessageDialog(null, "Cannot delete root admin (ID 1).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        int indexToRemove = -1;
        for (int i = 0; i < userCount; i++) {
            if (users[i].getId() == id) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) return false;

        // Shift array to fill the gap
        for (int i = indexToRemove; i < userCount - 1; i++) {
            users[i] = users[i + 1];
        }
        users[--userCount] = null; 
        
        saveUsers();
        return true;
    }
    
    // --- Persistence ---
    
    /**
     * Loads users from the text file.
     * If the file does not exist, it creates default users.
     */
    private void loadUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            addUser("admin", "123", Role.ADMIN); 
            addUser("cashier", "456", Role.CAJERO);
            saveUsers(); 
            return;
        }

        int maxId = 0;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine() && userCount < MAX_USERS) {
                String line = fileScanner.nextLine();
                String[] data = line.split(";"); 
                
                if (data.length == 4) { 
                    int id = Integer.parseInt(data[0]);
                    String username = data[1];
                    String passwordHash = data[2];
                    Role role = Role.valueOf(data[3]);
                    
                    users[userCount++] = new User(id, username, passwordHash, role);
                    
                    if (id > maxId) maxId = id; 
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR loading users: " + e.getMessage());
        }
        this.nextUserId = maxId + 1;
    }
    
    /**
     * Saves the current list of users to the text file.
     */
    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            for (int i = 0; i < userCount; i++) {
                writer.println(users[i].toString()); 
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR saving users: " + e.getMessage());
        }
    }
}