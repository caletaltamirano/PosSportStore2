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

import IGU.DashboardView;
import IGU.ProductosView;
import IGU.VentasView;
import IGU.FacturasView;
import IGU.UsuariosView;
import IGU.LoginUI;

public class MainFrame extends JFrame {

    private final StSystem system;
    private final User authenticatedUser;
    private JPanel mainContentPanel;
    private Map<String, JButton> navButtons;

    // Colores
    public static final Color COLOR_PRIMARY_BLUE = new Color(0, 51, 102);
    public static final Color COLOR_PRIMARY_NAVY = new Color(0, 51, 102);
    public static final Color COLOR_SIDEBAR_FG = Color.WHITE;
    public static final Color COLOR_BACKGROUND_MAIN = new Color(248, 248, 248);
    public static final Color COLOR_HOVER_DARK = new Color(0, 70, 140);
    public static final Color COLOR_ACTIVE_LINE = new Color(50, 150, 250);
    public static final Color COLOR_TEXT_DARK = Color.BLACK;
    public static final Color COLOR_TEXT_MUTE = new Color(150, 150, 150);

    // Tipografías
    public static final Font FONT_SIDEBAR = new Font("Inter", Font.PLAIN, 16);
    public static final Font FONT_TITLE = new Font("Inter", Font.BOLD, 32);

    public MainFrame(StSystem system, User authenticatedUser) {

        this.system = system;
        this.authenticatedUser = authenticatedUser;
        this.navButtons = new HashMap<>();

        setTitle("POSSPORT STORE - TPV (" + authenticatedUser.getRole() + ")");
        setSize(1280, 800);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND_MAIN);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addCloseListener();

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        setupMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);

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

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(90, getHeight()));
        sidebar.setBackground(COLOR_PRIMARY_NAVY);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));

        sidebar.add(createSidebarButtons(), BorderLayout.CENTER);
        sidebar.add(createSidebarFooter(), BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createSidebarButtons() {
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(COLOR_PRIMARY_NAVY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 10, 0);

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

        gbc.weighty = 1.0;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);                   
        menuPanel.add(spacer, gbc);

        return menuPanel;
    }

    private JPanel createSidebarFooter() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(COLOR_PRIMARY_NAVY);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton logoutBtn = createMinimalSidebarButton("Logout", "🚪");
        logoutBtn.setToolTipText("Cerrar sesión");
        navButtons.put("Logout", logoutBtn);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(COLOR_TEXT_MUTE);

        bottomPanel.add(separator, BorderLayout.NORTH);
        bottomPanel.add(logoutBtn, BorderLayout.CENTER);

        return bottomPanel;
    }

    private JButton createMinimalSidebarButton(String text, String iconText) {
        JButton btn = new JButton(iconText);
        btn.setToolTipText(text);
        btn.setPreferredSize(new Dimension(80, 80));
        btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);

        btn.setBackground(COLOR_PRIMARY_NAVY);
        btn.setForeground(COLOR_TEXT_MUTE);
        btn.setOpaque(true);

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

        btn.addActionListener(e -> handleNavigation(text, btn));

        return btn;
    }

    private void resetAllButtons() {
        for (JButton btn : navButtons.values()) {
            btn.setBackground(COLOR_PRIMARY_NAVY);
            btn.setForeground(COLOR_TEXT_MUTE);
            btn.setBorderPainted(false);
        }
    }

    private void handleNavigation(String text, JButton clickedButton) {

        resetAllButtons();

        clickedButton.setBackground(COLOR_HOVER_DARK);
        clickedButton.setForeground(COLOR_SIDEBAR_FG);
        clickedButton.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, COLOR_ACTIVE_LINE));

        switch (text) {
            case "Dashboard" ->
                setMainContent(new DashboardView(system));
            case "Productos" ->
                setMainContent(new ProductosView(system));
            case "Ventas" ->
                setMainContent(new VentasView(system));
            case "Facturas" ->
                setMainContent(new FacturasView(system));
            case "Gestión de usuarios" -> {
                if (authenticatedUser.getRole() == Role.ADMIN) {
                    setMainContent(new UsuariosView(system));
                } else {
                    clickedButton.setBackground(COLOR_PRIMARY_NAVY);
                    clickedButton.setForeground(COLOR_TEXT_MUTE);
                    JOptionPane.showMessageDialog(this, "Acceso restringido a administradores", "Error", JOptionPane.ERROR_MESSAGE);
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

    private JPanel createWelcomePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND_MAIN);

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
