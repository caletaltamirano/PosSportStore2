package IGU;

import possportstore.StSystem;
import possportstore.User;
import possportstore.User.Role;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

// Import de vistas (asumo que existen)
import IGU.DashboardView; 
import IGU.ProductosView;
import IGU.VentasView; 
import IGU.FacturasView; 
import IGU.UsuariosView; 
import IGU.LoginUI;

public class MainFrame extends JFrame {

    // -------------------------------------------------------------
    // Variables principales
    // -------------------------------------------------------------
    private final StSystem system;
    private final User authenticatedUser;
    private JPanel mainContentPanel;
    private Map<String, JButton> navButtons; 

    // -------------------------------------------------------------
    // Paleta de colores (Azul Marino)
    // -------------------------------------------------------------
    public static final Color COLOR_PRIMARY_BLUE = new Color(0, 51, 102);
    public static final Color COLOR_PRIMARY_NAVY = new Color(0, 51, 102); 
    public static final Color COLOR_SIDEBAR_FG = Color.WHITE; 
    public static final Color COLOR_BACKGROUND_MAIN = new Color(248, 248, 248); 
    public static final Color COLOR_HOVER_DARK = new Color(0, 70, 140); 
    public static final Color COLOR_ACTIVE_LINE = new Color(50, 150, 250); 
    public static final Color COLOR_TEXT_DARK = Color.BLACK;
    public static final Color COLOR_TEXT_MUTE = new Color(150, 150, 150); 

    // -------------------------------------------------------------
    // Tipografías
    // -------------------------------------------------------------
    public static final Font FONT_SIDEBAR = new Font("Inter", Font.PLAIN, 16);
    public static final Font FONT_TITLE = new Font("Inter", Font.BOLD, 32);

    // -------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------
    public MainFrame(StSystem system, User authenticatedUser) {

        this.system = system;
        this.authenticatedUser = authenticatedUser;
        this.navButtons = new HashMap<>();

        setTitle("POSSPORT STORE - TPV (" + authenticatedUser.getRole() + ")");
        setSize(1280, 800); 
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND_MAIN);
        
        // Iniciar en pantalla completa
        setExtendedState(JFrame.MAXIMIZED_BOTH); 

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addCloseListener();

        setLayout(new BorderLayout());

        // Construcción de UI
        add(createSidebar(), BorderLayout.WEST);
        setupMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);

        // Pantalla inicial: Navegar al Dashboard y activar el botón
        if (navButtons.containsKey("Dashboard")) {
            handleNavigation("Dashboard", navButtons.get("Dashboard"));
        } else {
             setMainContent(createWelcomePanel("Bienvenido"));
        }
    }

    private void addCloseListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "¿Seguro que deseas salir? Se guardarán los cambios.",
                        "Cerrar Aplicación",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    system.guardarProductos();
                    System.exit(0);
                }
            }
        });
    }

    // -------------------------------------------------------------
    // Sidebar (SIN ENCABEZADO)
    // -------------------------------------------------------------
    private JPanel createSidebar() {
        // Mantenemos el ancho para los botones de 80x80
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(90, getHeight())); 
        sidebar.setBackground(COLOR_PRIMARY_NAVY);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));

        // Solo el centro (botones) y el sur (logout)
        JPanel centerPanel = createSidebarButtons();
        JPanel bottomPanel = createSidebarFooter();

        sidebar.add(centerPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    // 🔑 Eliminamos createSidebarHeader()

    private JPanel createSidebarButtons() {
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(COLOR_PRIMARY_NAVY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Espaciado vertical, añadimos un poco más de espacio arriba al inicio para centrar visualmente
        gbc.insets = new Insets(20, 0, 10, 0); 

        // Mapeo de texto a un 'icono' simple
        Map<String, String> buttonIcons = Map.of(
            "Dashboard", "🏠",
            "Productos", "📦",
            "Ventas", "🛒",
            "Facturas", "🧾",
            "Gestión de usuarios", "👥"
        );

        String[] buttons = {"Dashboard", "Productos", "Ventas", "Facturas", "Gestión de usuarios"};

        for (String text : buttons) {
            JButton btn = createMinimalSidebarButton(text, buttonIcons.get(text));
            navButtons.put(text, btn);
            menuPanel.add(btn, gbc);
        }
        
        // Spacer (empuja los botones hacia arriba)
        gbc.weighty = 1.0; 
        menuPanel.add(new JPanel(), gbc); 

        return menuPanel;
    }

    private JPanel createSidebarFooter() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(COLOR_PRIMARY_NAVY);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JButton logoutBtn = createMinimalSidebarButton("Logout", "🚪");
        // Ajustamos la fuente y tamaño manualmente ya que se llama a la función de botones grandes.
        logoutBtn.setToolTipText("Cerrar sesión");
        navButtons.put("Logout", logoutBtn);
        
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(COLOR_TEXT_MUTE);
        separator.setBackground(COLOR_TEXT_MUTE);
        
        bottomPanel.add(separator, BorderLayout.NORTH);
        bottomPanel.add(logoutBtn, BorderLayout.CENTER);
        
        return bottomPanel;
    }

    // Botón del Sidebar (80x80)
    private JButton createMinimalSidebarButton(String text, String iconText) {
        JButton btn = new JButton(iconText);
        btn.setToolTipText(text); 
        
        // Botones grandes (80x80)
        btn.setPreferredSize(new Dimension(80, 80)); 
        btn.setMaximumSize(new Dimension(80, 80));
        // Ícono más grande (36 puntos)
        btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 36)); 
        
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setContentAreaFilled(false); 

        // Estado inicial
        btn.setBackground(COLOR_PRIMARY_NAVY);
        btn.setForeground(COLOR_TEXT_MUTE); 
        btn.setOpaque(true);

        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.getBackground().equals(COLOR_PRIMARY_NAVY)) {
                    btn.setBackground(COLOR_HOVER_DARK);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.getBackground().equals(COLOR_HOVER_DARK)) {
                    btn.setBackground(COLOR_PRIMARY_NAVY);
                }
            }
        });

        // Acción
        btn.addActionListener(e -> handleNavigation(text, btn));

        return btn;
    }

    // -------------------------------------------------------------
    // Lógica de Navegación y Vistas
    // -------------------------------------------------------------
    private void resetAllButtons() {
        for (JButton btn : navButtons.values()) {
            btn.setBackground(COLOR_PRIMARY_NAVY);
            btn.setForeground(COLOR_TEXT_MUTE);
            btn.setBorderPainted(false);
            btn.setBorder(new EmptyBorder(0, 0, 0, 0)); 
        }
    }
    
    private void handleNavigation(String text, JButton clickedButton) {

        resetAllButtons(); 
        
        // Activar el botón clicado
        clickedButton.setBackground(COLOR_HOVER_DARK); 
        clickedButton.setForeground(COLOR_SIDEBAR_FG); 
        // Indicador de activo: línea azul brillante
        clickedButton.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, COLOR_ACTIVE_LINE)); 

        switch (text) {
            case "Dashboard" -> {
                // 🚨 Asumimos que esta clase existe
                setMainContent(new DashboardView(system));
            }
            case "Productos" ->
                // 🚨 Asumimos que esta clase existe
                setMainContent(new ProductosView(system));
            case "Ventas" -> {
                // 🚨 Asumimos que esta clase existe
                setMainContent(new VentasView(system));
            }
            case "Facturas" -> {
                // 🚨 Asumimos que esta clase existe
                setMainContent(new FacturasView(system));
            }

            case "Gestión de usuarios" -> {
                if (authenticatedUser.getRole() == Role.ADMIN) {
                    // 🚨 Asumimos que esta clase existe
                    setMainContent(new UsuariosView(system));
                } else {
                    // Restablecer el botón visualmente si el acceso es denegado
                    clickedButton.setBackground(COLOR_PRIMARY_NAVY); 
                    clickedButton.setForeground(COLOR_TEXT_MUTE);
                    clickedButton.setBorderPainted(false);
                    
                    JOptionPane.showMessageDialog(this, "Acceso restringido a administradores", "Error de Permisos", JOptionPane.ERROR_MESSAGE);
                }
            }

            case "Logout" ->
                logout();
        }
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            system.guardarProductos();
            dispose();
            new LoginUI(system).setVisible(true);
        }
    }

    // -------------------------------------------------------------
    // Contenido Principal
    // -------------------------------------------------------------
    private void setupMainContentPanel() {
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(COLOR_BACKGROUND_MAIN);
        mainContentPanel.setBorder(new EmptyBorder(30, 30, 30, 30)); 
    }

    public void setMainContent(JPanel newContent) {
        mainContentPanel.removeAll();
        mainContentPanel.add(newContent, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    
    // Panel de Bienvenida
    private JPanel createWelcomePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND_MAIN);
        panel.setBorder(new EmptyBorder(50, 0, 0, 0)); 

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(COLOR_BACKGROUND_MAIN);
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("Selecciona una opción en la barra lateral.");
        desc.setFont(new Font("Inter", Font.PLAIN, 18));
        desc.setForeground(COLOR_TEXT_DARK);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(titleLbl);
        content.add(Box.createVerticalStrut(10));
        content.add(desc);

        panel.add(content, BorderLayout.NORTH);
        return panel;
    }
}