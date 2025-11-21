package IGU;

import possportstore.StSystem;
import possportstore.User;
import possportstore.User.Role;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

public class UsuariosView extends JPanel {

    private final StSystem system;
    private DefaultTableModel userTableModel;
    private JTable userTable;

    // Componentes del Formulario de Gestión/Detalle
    private JTextField idField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox;
    private JButton saveButton;
    private JButton deleteButton;

    // -------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------
    public UsuariosView(StSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Inicializar tabla y detalle
        setupUserTable();
        JPanel managementPanel = createManagementPanel();

        // Usar JSplitPane para dividir la lista y la gestión
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(userTable), managementPanel);
        splitPane.setDividerLocation(400); 
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        add(splitPane, BorderLayout.CENTER);

        // Cargar los datos iniciales
        loadUsersData();
        clearForm(); 
    }

    // -------------------------------------------------------------
    // CONFIGURACIÓN DE LA TABLA
    // -------------------------------------------------------------
    private void setupUserTable() {
        userTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Rol"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        userTable.setRowHeight(30);
        userTable.setFont(new Font("Inter", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));

        // Listener para cargar los datos del usuario seleccionado en el formulario
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                showUserDetails(userTable.getSelectedRow());
            }
        });
    }

    // -------------------------------------------------------------
    // FORMULARIO DE GESTIÓN DE USUARIOS (TOUCH-FRIENDLY)
    // -------------------------------------------------------------
    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Título
        JLabel title = new JLabel("Crear / Editar Usuario", SwingConstants.CENTER);
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        // Campos del Formulario (GridLayout)
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        // ID (Mostrar, no editable)
        formPanel.add(new JLabel("ID de Usuario:"));
        idField = new JTextField();
        idField.setEditable(false);
        formPanel.add(idField);

        // Nombre de Usuario
        formPanel.add(new JLabel("Nombre de Usuario:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        // Contraseña
        formPanel.add(new JLabel("Contraseña (Dejar vacío para no cambiar):"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Rol
        formPanel.add(new JLabel("Rol:"));
        roleComboBox = new JComboBox<>(Role.values());
        formPanel.add(roleComboBox);

        panel.add(formPanel, BorderLayout.CENTER);
        
        // --- Botones de Acción (Estilo TOUCH-FRIENDLY) ---
        // Aumentamos el espaciado (15, 15) y el borde (20, 20)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 15)); 
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Mayor padding
        buttonPanel.setBackground(Color.WHITE);

        // Definimos Tamaño y Fuente para mejor usabilidad táctil
        Font touchFont = new Font("Inter", Font.BOLD, 16); 
        Dimension touchSize = new Dimension(150, 50);

        // --- Botón Nuevo ---
        JButton newButton = new JButton("Nuevo");
        newButton.addActionListener(e -> clearForm());
        // Se asume la existencia de MainFrame.COLOR_PRIMARY_BLUE o se usa un color fijo
        newButton.setBackground(new Color(0, 123, 255)); // Azul estándar 
        newButton.setForeground(Color.WHITE);
        newButton.setFont(touchFont); 
        newButton.setPreferredSize(touchSize);

        // --- Botón Guardar/Actualizar ---
        saveButton = new JButton("Guardar/Actualizar");
        saveButton.addActionListener(e -> saveUser());
        saveButton.setBackground(new Color(40, 167, 69)); // Verde
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(touchFont);
        saveButton.setPreferredSize(touchSize);
        
        // --- Botón Eliminar ---
        deleteButton = new JButton("Eliminar");
        deleteButton.addActionListener(e -> deleteUser());
        deleteButton.setBackground(new Color(220, 53, 69)); // Rojo
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setEnabled(false); 
        deleteButton.setFont(touchFont);
        deleteButton.setPreferredSize(touchSize);

        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // -------------------------------------------------------------
    // LÓGICA DE DATOS Y CRUD
    // -------------------------------------------------------------
    
    private void loadUsersData() {
        userTableModel.setRowCount(0); 
        // Llama al método delegado en StSystem
        User[] users = system.getUsers(); 
        
        for (User user : users) {
            userTableModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getRole()
            });
        }
    }
    
    private void showUserDetails(int rowIndex) {
        User[] users = system.getUsers();
        if (rowIndex < 0 || rowIndex >= users.length) return;
        
        User user = users[rowIndex];

        idField.setText(String.valueOf(user.getId()));
        nameField.setText(user.getUsername());
        passwordField.setText(""); 
        roleComboBox.setSelectedItem(user.getRole());
        
        saveButton.setText("Actualizar");
        deleteButton.setEnabled(true);
    }
    
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        nameField.requestFocus(); // Enfocar el campo de nombre al limpiar
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        userTable.clearSelection();
        
        saveButton.setText("Guardar");
        deleteButton.setEnabled(false);
    }

    private void saveUser() {
        String username = nameField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleComboBox.getSelectedItem();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Si estamos creando un nuevo usuario, la contraseña es obligatoria.
        if (idField.getText().isEmpty() && password.isEmpty()) {
              JOptionPane.showMessageDialog(this, "La contraseña es obligatoria para nuevos usuarios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }


        if (idField.getText().isEmpty()) {
            // Lógica para CREAR nuevo usuario
            if (system.addUser(username, password, role)) {
                JOptionPane.showMessageDialog(this, "Usuario '" + username + "' creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUsersData();
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear el usuario. El nombre puede ya existir.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Lógica para EDITAR usuario existente
            try {
                int id = Integer.parseInt(idField.getText());
                // Si la contraseña está vacía, se pasa 'null' para no cambiarla
                String passwordToUpdate = password.isEmpty() ? null : password; 
                
                // Llama al método delegado en StSystem
                if (system.updateUser(id, username, passwordToUpdate, role)) {
                    JOptionPane.showMessageDialog(this, "Usuario ID " + id + " actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadUsersData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                  JOptionPane.showMessageDialog(this, "Error interno en el ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteUser() {
        try {
            int id = Integer.parseInt(idField.getText());
            String username = nameField.getText();

            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar al usuario '" + username + "' (ID: " + id + ")? Esta acción es irreversible.", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Llama al método delegado en StSystem
                if (system.deleteUser(id)) {
                    JOptionPane.showMessageDialog(this, "Usuario ID " + id + " eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadUsersData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(this, "Error interno en el ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}