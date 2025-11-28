package ui;

import possportstore.StoreSystem;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Main application entry point.
 * Initializes the backend system and launches the Login interface.
 */
public class MainApp {

    public static void main(String[] args) {

        // 1. Initialize Business Logic (Facade)
        final StoreSystem system = new StoreSystem();

        System.out.println("BIENVENIDO A POSSPORT STORE - GUI Mode");

        // 2. Launch GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Open Login Window
                LoginUI login = new LoginUI(system); 
                login.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error al iniciar la aplicación: " + e.getMessage(), 
                    "Error Crítico", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}