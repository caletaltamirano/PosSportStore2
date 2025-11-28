package ui;

import possportstore.StoreSystem;
import possportstore.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Authentication window.
 * Handles user login and transitions to the MainFrame.
 */
public class LoginUI extends JFrame {

    private final StoreSystem system;
    private JTextField userField;
    private JPasswordField passField;

    // UI Constants
    private static final Color COLOR_PRIMARY = MainFrame.COLOR_PRIMARY_BLUE;
    private static final Color COLOR_BACKGROUND_MAIN = MainFrame.COLOR_BACKGROUND_MAIN;
    private static final Font FONT_FIELD_LABEL = new Font("Inter", Font.BOLD, 18);
    private static final Font FONT_FIELD_INPUT = new Font("Inter", Font.PLAIN, 20);
    private static final Font FONT_BUTTON = new Font("Inter", Font.BOLD, 22);

    public LoginUI(StoreSystem system) {
        this.system = system;
        setTitle("POSSPORT STORE - Iniciar Sesión");
        
        setSize(480, 420); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
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
        
        JPanel gridPanel = new JPanel(new GridLayout(3, 1, 0, 15)); 
        gridPanel.setBackground(COLOR_BACKGROUND_MAIN);

        // --- User Field ---
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(FONT_FIELD_LABEL);
        
        userField = new JTextField(15);
        userField.setFont(FONT_FIELD_INPUT);
        userField.setPreferredSize(new Dimension(300, 45)); 
        
        JPanel userContainer = new JPanel(new BorderLayout());
        userContainer.setBackground(COLOR_BACKGROUND_MAIN);
        userContainer.add(userLabel, BorderLayout.NORTH);
        userContainer.add(userField, BorderLayout.CENTER);
        
        // --- Password Field ---
        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setFont(FONT_FIELD_LABEL);
        
        passField = new JPasswordField(15);
        passField.setFont(FONT_FIELD_INPUT);
        passField.setPreferredSize(new Dimension(300, 45)); 

        JPanel passContainer = new JPanel(new BorderLayout());
        passContainer.setBackground(COLOR_BACKGROUND_MAIN);
        passContainer.add(passLabel, BorderLayout.NORTH);
        passContainer.add(passField, BorderLayout.CENTER);

        // --- Login Button ---
        JButton loginButton = new JButton("INGRESAR");
        loginButton.setFont(FONT_BUTTON);
        loginButton.setBackground(COLOR_PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(300, 50)); 
        loginButton.addActionListener(e -> attemptLogin());
        
        // Allow ENTER key submission
        userField.addActionListener(e -> attemptLogin());
        passField.addActionListener(e -> attemptLogin());

        gridPanel.add(userContainer);
        gridPanel.add(passContainer);
        
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
        
        User authenticatedUser = system.getUserManager().authenticate(username, password);

        if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(this, 
                "Bienvenido, " + authenticatedUser.getUsername() + " (" + authenticatedUser.getRole() + ")", 
                "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
            
            dispose(); 
            
            // Launch Main Frame
            MainFrame dashboard = new MainFrame(this.system, authenticatedUser); 
            dashboard.setVisible(true);  
        } else {
            JOptionPane.showMessageDialog(this, 
                "Credenciales incorrectas. Intente de nuevo.", 
                "Error de Login", JOptionPane.ERROR_MESSAGE);
            
            passField.setText("");
        }
    }
}