package IGU;

import possportstore.StSystem;
import javax.swing.SwingUtilities;

/**
 * Clase principal que inicializa el sistema POSSPORT STORE.
 * Ahora inicia la ventana de Login.
 */
public class MainApp {

    public static void main(String[] args) {

        // 1. Inicializar la Lógica de Negocio (El controlador central)
        final StSystem system = new StSystem();

        System.out.println("BIENVENIDO A POSSPORT STORE - GUI Mode");

        // 2. Ejecutar la Interfaz Gráfica de Login en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Inicia la ventana de Login, que a su vez
                // abrirá MainFrame si el login es exitoso.
                LoginUI login = new LoginUI(system); 
                login.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Error al iniciar la aplicación: " + e.getMessage(), 
                    "Error Crítico", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}