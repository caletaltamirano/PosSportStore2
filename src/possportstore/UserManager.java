package possportstore;

import java.io.*;
import java.util.Scanner;
import possportstore.User.Role;
import javax.swing.JOptionPane;
import java.util.Arrays;

public class UserManager {
    
    private User[] users;
    private int userCount;
    private int nextUserId; // 🔑 NUEVO: Contador para generar IDs secuenciales
    private static final int MAX_USERS = 10;
    private static final String FILE_NAME = "users.txt";
    
    private User currentUser; 

    public UserManager() {
        this.users = new User[MAX_USERS];
        this.userCount = 0;
        this.nextUserId = 1; // Empieza a contar IDs desde 1
        cargarUsuarios(); 
    }
    
    public User getCurrentUser() {
        return this.currentUser;
    }
    
    /**
     * Retorna un sub-arreglo (copia) que contiene solo los usuarios válidos.
     * Este es el método que usa UsuariosView para llenar la tabla.
     */
    public User[] getUsersForDisplay() {
        return Arrays.copyOf(users, userCount);
    }

    // --- Lógica de Login (Usando Hash/PasswordHash si la clase User lo tiene) ---
    public User authenticate(String username, String password) {
        for (int i = 0; i < userCount; i++) {
            User user = users[i];
            // 🔑 USAMOS getPasswordHash() o getPassword() de la clase User
            if (user.getUsername().equals(username) && user.getPasswordHash().equals(password)) {
                this.currentUser = user; 
                return user;
            }
        }
        this.currentUser = null;
        return null;
    }
    
    // --- Lógica de CRUD para UsuariosView ---

    /**
     * Añade un nuevo usuario con un ID generado automáticamente.
     */
    public boolean addUser(String username, String password, Role role) {
        if (userCount >= MAX_USERS) {
            JOptionPane.showMessageDialog(null, "Límite de usuarios alcanzado.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Verificar duplicados por nombre
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUsername().equals(username)) {
                JOptionPane.showMessageDialog(null, "El nombre de usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // 🔑 USAMOS nextUserId y pasamos la contraseña
        User newUser = new User(nextUserId++, username, password, role);
        users[userCount++] = newUser;
        guardarUsuarios();
        return true;
    }
    
    /**
     * Actualiza los datos de un usuario existente.
     */
    public boolean updateUser(int id, String newUsername, String newPassword, Role newRole) {
        for (int i = 0; i < userCount; i++) {
            User u = users[i];
            
            // 1. Encontrar el usuario por ID
            if (u.getId() == id) {
                // 2. Verificar duplicidad de nombre con OTROS usuarios
                for (int j = 0; j < userCount; j++) {
                    if (i != j && users[j].getUsername().equals(newUsername)) {
                        JOptionPane.showMessageDialog(null, "El nombre de usuario ya está en uso.", "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                
                // 3. Aplicar Setters (requiere la clase User corregida)
                u.setUsername(newUsername);
                u.setRole(newRole);
                
                // Si la contraseña no es nula ni vacía, actualizar
                if (newPassword != null && !newPassword.isEmpty()) {
                    u.setPasswordHash(newPassword); // Asume que ya está hasheada o se hashea en User
                }
                
                guardarUsuarios();
                return true;
            }
        }
        return false; // Usuario no encontrado
    }
    
    /**
     * Elimina un usuario por su ID.
     */
    public boolean deleteUser(int id) {
        if (id == 1) { // Proteger al primer administrador
            JOptionPane.showMessageDialog(null, "No se puede eliminar al usuario principal (ID 1).", "Error", JOptionPane.ERROR_MESSAGE);
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

        // 1. Desplazar elementos para llenar el hueco
        for (int i = indexToRemove; i < userCount - 1; i++) {
            users[i] = users[i + 1];
        }
        
        // 2. Reducir el contador y limpiar la última posición (opcional, pero buena práctica)
        users[--userCount] = null; 
        
        guardarUsuarios();
        return true;
    }
    
    // --- Persistencia ---
    
    /**
     * Carga usuarios desde el archivo. Si no existe, crea los usuarios por defecto.
     */
    private void cargarUsuarios() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
    // 🔑 Aquí addUser DEBE usar el nextUserId=1 para el admin   
    addUser("admin", "123", Role.ADMIN); 
    addUser("cajero", "456", Role.CAJERO);
    guardarUsuarios(); 
    return;
}

        int maxId = 0;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine() && userCount < MAX_USERS) {
                String line = fileScanner.nextLine();
                // 🔑 CRÍTICO: El formato DEBE ser ahora ID;username;password;ROLE
                String[] data = line.split(";"); 
                
                // data.length debe ser 4
                if (data.length == 4) { 
                    int id = Integer.parseInt(data[0]);
                    String username = data[1];
                    String passwordHash = data[2];
                    Role role = Role.valueOf(data[3]);
                    
                    // 🔑 USAMOS EL CONSTRUCTOR DE 4 PARÁMETROS
                    users[userCount++] = new User(id, username, passwordHash, role);
                    
                    // Rastreamos el ID más alto para mantener la secuencia
                    if (id > maxId) maxId = id; 
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR al cargar usuarios: " + e.getMessage());
        }
        
        // 🔑 Aseguramos que el próximo ID a asignar continúe la secuencia
        this.nextUserId = maxId + 1;
    }
    
    /**
     * Guarda todos los usuarios del arreglo fijo en el archivo.
     */
    public void guardarUsuarios() {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            for (int i = 0; i < userCount; i++) {
                // Se asume que User.toString() devuelve: ID;username;password;ROLE
                writer.println(users[i].toString()); 
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR al guardar usuarios: " + e.getMessage());
        }
    }
}