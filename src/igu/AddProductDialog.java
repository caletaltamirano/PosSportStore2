package IGU;

import possportstore.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class AddProductDialog extends JDialog {

    private final StSystem system;
    private final ProductosView parentView;

    // Campos de entrada
    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;
    private JTextField descriptionField; 
    private JComboBox<String> categoryComboBox;
    
    // Campos específicos de texto
    private JTextField specificField1;
    private JTextField specificField2;
    
    // Componente para el campo de TIPO (JComboBox con los Enums)
    private Component specificTypeInput; 
    
    // Contenedor para campos dinámicos
    private JPanel dynamicFieldsPanel; 

    // Dimensión de entrada para controles más grandes
    private static final Dimension LARGE_INPUT_SIZE = new Dimension(200, 35); 

    public AddProductDialog(JFrame parent, StSystem system, ProductosView parentView) {
        super(parent, "Añadir Nuevo Producto", true);
        this.system = system;
        this.parentView = parentView;

        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Aumentamos el tamaño base para que la ventana sea más grande.
        setSize(550, 680); 
        
        // Estilo general: un panel contenedor con un EmptyBorder
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20)); 
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25)); 

        contentPanel.add(createFormPanel(), BorderLayout.NORTH);
        contentPanel.add(createButtonPanel(), BorderLayout.SOUTH); // Llama al método corregido
        
        add(contentPanel, BorderLayout.CENTER);
        
        updateDynamicFields((String) categoryComboBox.getSelectedItem()); 
        
        pack(); 
        setLocationRelativeTo(parent);
    }

    private String[] getEnumNames(Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                     .map(Enum::name)
                     .toArray(String[]::new);
    }


    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20)); 
        
        JPanel fixedFieldsPanel = new JPanel(new GridLayout(4, 2, 15, 15)); 
        
        nameField = new JTextField();
        priceField = new JTextField();
        stockField = new JTextField();
        descriptionField = new JTextField();
        categoryComboBox = new JComboBox<>(new String[]{"Zapato", "Ropa", "Accesorio"});

        nameField.setPreferredSize(LARGE_INPUT_SIZE);
        priceField.setPreferredSize(LARGE_INPUT_SIZE);
        stockField.setPreferredSize(LARGE_INPUT_SIZE);
        categoryComboBox.setPreferredSize(LARGE_INPUT_SIZE);


        categoryComboBox.addActionListener(e -> updateDynamicFields((String) categoryComboBox.getSelectedItem()));

        fixedFieldsPanel.add(new JLabel("Nombre:"));
        fixedFieldsPanel.add(nameField);

        fixedFieldsPanel.add(new JLabel("Precio (€/₡):"));
        fixedFieldsPanel.add(priceField);

        fixedFieldsPanel.add(new JLabel("Stock Inicial:"));
        fixedFieldsPanel.add(stockField);

        fixedFieldsPanel.add(new JLabel("Categoría:"));
        fixedFieldsPanel.add(categoryComboBox);
        
        mainPanel.add(fixedFieldsPanel, BorderLayout.NORTH);
        
        dynamicFieldsPanel = new JPanel(new GridLayout(3, 2, 15, 15)); 
        dynamicFieldsPanel.setBorder(BorderFactory.createTitledBorder("Detalles Específicos"));
        mainPanel.add(dynamicFieldsPanel, BorderLayout.CENTER);
        
        JTextArea descriptionArea = new JTextArea(4, 20); 
        descriptionArea.setText(descriptionField.getText());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Descripción"));
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        mainPanel.add(descPanel, BorderLayout.SOUTH);
        
        descriptionArea.getDocument().addDocumentListener((SimpleDocumentListener) e -> descriptionField.setText(descriptionArea.getText()));

        return mainPanel;
    }
    
    private void updateDynamicFields(String category) {
        dynamicFieldsPanel.removeAll();
        specificField1 = new JTextField(); 
        specificField2 = new JTextField(); 
        specificTypeInput = new JPanel(); 
        
        specificField1.setPreferredSize(LARGE_INPUT_SIZE);
        specificField2.setPreferredSize(LARGE_INPUT_SIZE);

        if (category.equals("Zapato") || category.equals("Ropa")) {
            
            Class<?> enumClass = (category.equals("Zapato")) ? Shoe.TypeShoe.class : Clothe.TypeClothe.class;
            JComboBox<String> typeComboBox = new JComboBox<>(getEnumNames((Class<? extends Enum<?>>) enumClass));
            typeComboBox.setPreferredSize(LARGE_INPUT_SIZE);
            specificTypeInput = typeComboBox;

            dynamicFieldsPanel.add(new JLabel("Talla:"));
            dynamicFieldsPanel.add(specificField1);
            dynamicFieldsPanel.add(new JLabel("Color:"));
            dynamicFieldsPanel.add(specificField2);
            dynamicFieldsPanel.add(new JLabel("Tipo:"));
            dynamicFieldsPanel.add(specificTypeInput); 

        } else if (category.equals("Accesorio")) {
            
            JComboBox<String> typeComboBox = new JComboBox<>(getEnumNames(Accesories.TypeAccesories.class));
            typeComboBox.setPreferredSize(LARGE_INPUT_SIZE);
            specificTypeInput = typeComboBox;

            dynamicFieldsPanel.add(new JLabel("Marca:"));
            dynamicFieldsPanel.add(specificField1);
            dynamicFieldsPanel.add(new JLabel("Tipo:"));
            dynamicFieldsPanel.add(specificTypeInput); 
            
            dynamicFieldsPanel.add(new JPanel()); 
            dynamicFieldsPanel.add(new JPanel()); 
        }

        dynamicFieldsPanel.revalidate();
        dynamicFieldsPanel.repaint();
    }


    /**
     * MÉTODO CORREGIDO: Los botones Guardar y Cancelar ahora tienen una altura de 40px.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Nueva dimensión para los botones de acción (150 ancho x 40 alto)
        Dimension buttonSize = new Dimension(150, 40); 

        JButton saveButton = new JButton("Guardar Producto");
        saveButton.setFont(new Font("Inter", Font.BOLD, 14));
        saveButton.setBackground(MainFrame.COLOR_PRIMARY_BLUE);
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(buttonSize); // APLICACIÓN DE TAMAÑO
        saveButton.setBorderPainted(false); 
        saveButton.setFocusPainted(false); 
        saveButton.addActionListener(e -> saveProduct());

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Inter", Font.BOLD, 14));
        cancelButton.setPreferredSize(buttonSize); // APLICACIÓN DE TAMAÑO
        cancelButton.addActionListener(e -> dispose());
        
        // Agregar un pequeño borde superior para separarlo del panel de descripción
        panel.setBorder(new EmptyBorder(10, 0, 0, 0)); 

        panel.add(cancelButton);
        panel.add(saveButton);
        return panel;
    }

    private void saveProduct() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = (String) categoryComboBox.getSelectedItem();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, rellena todos los campos obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            if (price <= 0 || stock < 0) {
                 JOptionPane.showMessageDialog(this, "Precio debe ser positivo y Stock no negativo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String newId = system.getNextProductId(); 
            Product newProduct = null;
            
            switch (category) {
                case "Zapato" -> {
                    if (specificField1.getText().isEmpty() || specificField2.getText().isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Faltan detalles de Zapato (Talla o Color).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    String typeValue = (String) ((JComboBox<?>) specificTypeInput).getSelectedItem(); 
                    
                    newProduct = new Shoe(
                         specificField1.getText(), 
                         specificField2.getText(), 
                         Shoe.TypeShoe.valueOf(typeValue), 
                         price, stock, description, newId, name
                    );
                }
                case "Ropa" -> {
                    if (specificField1.getText().isEmpty() || specificField2.getText().isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Faltan detalles de Ropa (Talla o Color).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    String typeValue = (String) ((JComboBox<?>) specificTypeInput).getSelectedItem();
                    
                    newProduct = new Clothe(
                         specificField1.getText(), 
                         specificField2.getText(), 
                         Clothe.TypeClothe.valueOf(typeValue), 
                         price, stock, description, newId, name
                    );
                }
                case "Accesorio" -> {
                    if (specificField1.getText().isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Falta la Marca del Accesorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    String typeValue = (String) ((JComboBox<?>) specificTypeInput).getSelectedItem();
                    
                    newProduct = new Accesories(
                         specificField1.getText(), 
                         Accesories.TypeAccesories.valueOf(typeValue), 
                         price, stock, description, newId, name
                    );
                }
            }

            if (newProduct != null) {
                boolean added = system.addProduct(newProduct);

                if (added) {
                    JOptionPane.showMessageDialog(this, "Producto '" + name + "' (ID: " + newId + ") añadido con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    parentView.refreshView(); 
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo añadir el producto. Inventario lleno.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio y Stock deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
             JOptionPane.showMessageDialog(this, "Error de tipo/enum: No se pudo obtener el tipo de producto. Intente de nuevo.", "Error Interno", JOptionPane.ERROR_MESSAGE);
        }
    }
}


@FunctionalInterface
interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
    void update(javax.swing.event.DocumentEvent e);

    @Override
    default void insertUpdate(javax.swing.event.DocumentEvent e) {
        update(e);
    }

    @Override
    default void removeUpdate(javax.swing.event.DocumentEvent e) {
        update(e);
    }

    @Override
    default void changedUpdate(javax.swing.event.DocumentEvent e) {
        update(e);
    }
}