package IGU;

import possportstore.StSystem;
import possportstore.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI extends JFrame {

    private final StSystem system;
    private JTextField userField;
    private JPasswordField passField;

    // Colores y Fuentes de MainFrame (se asume que son accesibles)
    private static final Color COLOR_PRIMARY = MainFrame.COLOR_PRIMARY_BLUE;
    private static final Color COLOR_BACKGROUND_MAIN = MainFrame.COLOR_BACKGROUND_MAIN;
    private static final Color COLOR_TEXT_DARK = MainFrame.COLOR_TEXT_DARK;
    private static final Font FONT_FIELD_LABEL = new Font("Inter", Font.BOLD, 18);
    private static final Font FONT_FIELD_INPUT = new Font("Inter", Font.PLAIN, 20);
    private static final Font FONT_BUTTON = new Font("Inter", Font.BOLD, 22);

    public LoginUI(StSystem system) {
        this.system = system;
        setTitle("POSSPORT STORE - Iniciar Sesión");
        
        // 🔑 Aumento de tamaño para un mejor diseño y touch friendly
        setSize(480, 420); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana
        setResizable(false);
        setLayout(new BorderLayout());

        // -------------------------------------------------------------
        // 1. Encabezado (Visual)
        // -------------------------------------------------------------
        add(createHeaderPanel(), BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Formulario (Touch Friendly)
        // -------------------------------------------------------------
        add(createFormPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel title = new JLabel("ACCESO AL TPV");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        
        headerPanel.add(title);
        return headerPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BACKGROUND_MAIN);
        mainPanel.setBorder(new EmptyBorder(25, 30, 30, 30));
        
        JPanel gridPanel = new JPanel(new GridLayout(3, 1, 0, 15)); // Usar 3 filas para mayor claridad
        gridPanel.setBackground(COLOR_BACKGROUND_MAIN);

        // --- Campo Usuario ---
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(FONT_FIELD_LABEL);
        
        userField = new JTextField(15);
        userField.setFont(FONT_FIELD_INPUT);
        // 🔑 Aumento de altura para touch friendly
        userField.setPreferredSize(new Dimension(300, 45)); 
        
        JPanel userContainer = new JPanel(new BorderLayout());
        userContainer.setBackground(COLOR_BACKGROUND_MAIN);
        userContainer.add(userLabel, BorderLayout.NORTH);
        userContainer.add(userField, BorderLayout.CENTER);
        
        // --- Campo Contraseña ---
        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setFont(FONT_FIELD_LABEL);
        
        passField = new JPasswordField(15);
        passField.setFont(FONT_FIELD_INPUT);
        // 🔑 Aumento de altura para touch friendly
        passField.setPreferredSize(new Dimension(300, 45)); 

        JPanel passContainer = new JPanel(new BorderLayout());
        passContainer.setBackground(COLOR_BACKGROUND_MAIN);
        passContainer.add(passLabel, BorderLayout.NORTH);
        passContainer.add(passField, BorderLayout.CENTER);

        // --- Botón Ingresar ---
        JButton loginButton = new JButton("INGRESAR");
        loginButton.setFont(FONT_BUTTON);
        loginButton.setBackground(COLOR_PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorderPainted(false);
        // 🔑 Aumento de altura para touch friendly
        loginButton.setPreferredSize(new Dimension(300, 50)); 
        loginButton.addActionListener(e -> attemptLogin());
        
        // Permite ingresar presionando ENTER en los campos de texto
        userField.addActionListener(e -> attemptLogin());
        passField.addActionListener(e -> attemptLogin());

        // --- Montar el Grid ---
        gridPanel.add(userContainer);
        gridPanel.add(passContainer);
        
        // Contenedor para centrar el botón
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setBackground(COLOR_BACKGROUND_MAIN);
        buttonWrapper.add(loginButton);
        
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(buttonWrapper, BorderLayout.SOUTH);

        return mainPanel;
    }
    
    private void attemptLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        
        // Asumiendo que system.getUserManager().authenticate() existe y funciona
        User authenticatedUser = system.getUserManager().authenticate(username, password);

        if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(this, 
                "Bienvenido, " + authenticatedUser.getUsername() + " (" + authenticatedUser.getRole() + ")", 
                "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
            
            // 1. Cerrar la ventana de login
            dispose(); 
            
            // 2. Abrir el Dashboard principal
            MainFrame dashboard = new MainFrame(this.system, authenticatedUser); 
            dashboard.setVisible(true);  
        } else {
            JOptionPane.showMessageDialog(this, 
                "Credenciales incorrectas. Intente de nuevo.", 
                "Error de Login", JOptionPane.ERROR_MESSAGE);
            
            // Limpiar la contraseña para reintentar
            passField.setText("");
        }
    }
}