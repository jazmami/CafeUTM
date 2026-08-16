/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vista;
import controlador.ControladorProducto; 
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Categoria;               
import modelo.Producto;                
/**
 *
 * @author dantj
 */
public class FrmGestionProductos extends javax.swing.JInternalFrame {

    /**
     * Creates new form FrmGestionProductos
     */
    private ControladorProducto controlador = new ControladorProducto();
private int idProductoSeleccionado = 0;
    public FrmGestionProductos() {
        initComponents();
        lblTituloVentana.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 40));
        pnlGestionProductos.revalidate();
        cargarCategorias();
        listarProductos();
    }

    private void cargarCategorias() {
    cbxCategoria.removeAllItems();
    List<Categoria> categorias = controlador.listarCategorias();
    for (Categoria c : categorias) {
        cbxCategoria.addItem(c);
    }
}

private void listarProductos() {
    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("ID");
    modelo.addColumn("Platillo / Bebida");
    modelo.addColumn("Precio ($)");
    modelo.addColumn("Categoria");
    modelo.addColumn("Disponibilidad");
    tblProductos.setModel(modelo);

    List<Producto> lista = controlador.listar();
    for (Producto p : lista) {
        Object[] fila = {
            p.getIdProducto(),
            p.getNombre(),
            p.getPrecio(),
            p.getCategoria().getNombreCategoria(),
            p.isDisponible() ? "Disponible" : "No disponible"
        };
        modelo.addRow(fila);
    }
    lblTotalProductos.setText("Total de productos: " + lista.size());
    // Estilo guinda para el encabezado de la tabla
tblProductos.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
tblProductos.getTableHeader().setForeground(new java.awt.Color(122, 15, 42)); // Guinda
tblProductos.getTableHeader().setBackground(java.awt.Color.WHITE);

// Anchos de columna proporcionales
tblProductos.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
tblProductos.getColumnModel().getColumn(1).setPreferredWidth(260); // Platillo / Bebida
tblProductos.getColumnModel().getColumn(2).setPreferredWidth(110); // Precio ($)
tblProductos.getColumnModel().getColumn(3).setPreferredWidth(140); // Categoría
tblProductos.getColumnModel().getColumn(4).setPreferredWidth(130); // Disponibilidad

// Colorear el texto de disponibilidad (Verde: Disponible / Rojo: No disponible) y alineación
tblProductos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Centrar columnas excepto el Nombre del Producto (Columna 1)
        if (column == 0 || column == 2 || column == 3 || column == 4) {
            setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }

        // Colorear disponibilidad (Columna 4)
        if (column == 4 && value != null) {
            if ("Disponible".equals(value.toString())) {
                setForeground(new java.awt.Color(0, 128, 0)); // Verde
                setFont(getFont().deriveFont(java.awt.Font.BOLD));
            } else {
                setForeground(new java.awt.Color(192, 0, 0)); // Rojo
                setFont(getFont().deriveFont(java.awt.Font.BOLD));
            }
        } else if (!isSelected) {
            setForeground(java.awt.Color.BLACK);
            setFont(getFont().deriveFont(java.awt.Font.PLAIN));
        }

        return c;
    }
});
}

private void limpiar() {
    txtNombre.setText("");
    txtPrecio.setText("");
    cbxCategoria.setSelectedIndex(-1);
    chkDisponible.setSelected(true);
    idProductoSeleccionado = 0;
    tblProductos.clearSelection();
}

private boolean validarCampos() {
    if (txtNombre.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe capturar el nombre del producto.");
        txtNombre.requestFocus();
        return false;
    }
    try {
        double precio = Double.parseDouble(txtPrecio.getText().trim());
        if (precio < 0) {
            JOptionPane.showMessageDialog(this, "El precio no puede ser negativo.");
            txtPrecio.requestFocus();
            return false;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.");
        txtPrecio.requestFocus();
        return false;
    }
    if (cbxCategoria.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Debe seleccionar una categoría.");
        return false;
    }
    return true;
}

private Producto construirProductoDesdeFormulario() {
    Producto producto = new Producto();
    producto.setNombre(txtNombre.getText().trim());
    producto.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
    producto.setCategoria((Categoria) cbxCategoria.getSelectedItem());
    producto.setDisponible(chkDisponible.isSelected());
    return producto;
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGestionProductos = new javax.swing.JPanel();
        lblTituloVentana = new javax.swing.JLabel();
        pnlDatos = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblPrecio = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        lblCategoria = new javax.swing.JLabel();
        cbxCategoria = new javax.swing.JComboBox<>();
        chkDisponible = new javax.swing.JCheckBox();
        pnlAcciones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        lblTotalProductos = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administración del Menú y Productos");

        lblTituloVentana.setBackground(new java.awt.Color(122, 15, 42));
        lblTituloVentana.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTituloVentana.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloVentana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/productos.png"))); // NOI18N
        lblTituloVentana.setText("  Administración del Menú y Productos");
        lblTituloVentana.setIconTextGap(12);
        lblTituloVentana.setMaximumSize(new java.awt.Dimension(32767, 40));
        lblTituloVentana.setOpaque(true);
        lblTituloVentana.setPreferredSize(new java.awt.Dimension(100, 40));

        pnlDatos.setBackground(new java.awt.Color(255, 255, 255));
        pnlDatos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información del Producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(122, 15, 42))); // NOI18N

        lblNombre.setForeground(new java.awt.Color(51, 51, 51));
        lblNombre.setText("Nombre del Producto:");

        txtNombre.setPreferredSize(new java.awt.Dimension(320, 30));

        lblPrecio.setForeground(new java.awt.Color(51, 51, 51));
        lblPrecio.setText("Precio ($):");

        txtPrecio.setPreferredSize(new java.awt.Dimension(160, 30));

        lblCategoria.setForeground(new java.awt.Color(51, 51, 51));
        lblCategoria.setText("Categoria:");

        cbxCategoria.setPreferredSize(new java.awt.Dimension(220, 30));

        chkDisponible.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        chkDisponible.setForeground(new java.awt.Color(122, 15, 42));
        chkDisponible.setSelected(true);
        chkDisponible.setText("Disponible Hoy");

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombre)
                    .addComponent(lblCategoria))
                .addGap(18, 18, 18)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblPrecio)
                        .addGap(47, 47, 47)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkDisponible))
                .addGap(39, 39, 39))
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPrecio)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblNombre))
                .addGap(18, 18, 18)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCategoria)
                    .addComponent(chkDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlAcciones.setBackground(new java.awt.Color(255, 255, 255));

        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(122, 15, 42));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/guardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardar.setIconTextGap(8);
        btnGuardar.setMaximumSize(new java.awt.Dimension(120, 35));
        btnGuardar.setPreferredSize(new java.awt.Dimension(120, 35));
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnModificar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnModificar.setForeground(new java.awt.Color(122, 15, 42));
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/modificar.png"))); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModificar.setIconTextGap(8);
        btnModificar.setMaximumSize(new java.awt.Dimension(120, 35));
        btnModificar.setPreferredSize(new java.awt.Dimension(120, 35));
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(122, 15, 42));
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/eliminar.png"))); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.setIconTextGap(8);
        btnEliminar.setMaximumSize(new java.awt.Dimension(120, 35));
        btnEliminar.setPreferredSize(new java.awt.Dimension(120, 35));
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(122, 15, 42));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar.png"))); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBuscar.setIconTextGap(8);
        btnBuscar.setMaximumSize(new java.awt.Dimension(120, 35));
        btnBuscar.setPreferredSize(new java.awt.Dimension(120, 35));
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(122, 15, 42));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/limpiar.png"))); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLimpiar.setIconTextGap(8);
        btnLimpiar.setMaximumSize(new java.awt.Dimension(120, 35));
        btnLimpiar.setPreferredSize(new java.awt.Dimension(120, 35));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAccionesLayout = new javax.swing.GroupLayout(pnlAcciones);
        pnlAcciones.setLayout(pnlAccionesLayout);
        pnlAccionesLayout.setHorizontalGroup(
            pnlAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccionesLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(50, 50, 50)
                .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addGap(50, 50, 50)
                .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(50, 50, 50)
                .addComponent(btnBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(50, 50, 50)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );
        pnlAccionesLayout.setVerticalGroup(
            pnlAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAccionesLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(pnlAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Platillo / Bebida", "Precio ($)", "Categoria", "Disponibilidad"
            }
        ));
        tblProductos.setGridColor(new java.awt.Color(230, 230, 230));
        tblProductos.setRowHeight(30);
        tblProductos.setShowHorizontalLines(true);
        tblProductos.setShowVerticalLines(true);
        tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProductos);

        lblTotalProductos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotalProductos.setText("Total de productos: 0");

        javax.swing.GroupLayout pnlGestionProductosLayout = new javax.swing.GroupLayout(pnlGestionProductos);
        pnlGestionProductos.setLayout(pnlGestionProductosLayout);
        pnlGestionProductosLayout.setHorizontalGroup(
            pnlGestionProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGestionProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGestionProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(pnlAcciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotalProductos)
                    .addComponent(lblTituloVentana, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGestionProductosLayout.setVerticalGroup(
            pnlGestionProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGestionProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTituloVentana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalProductos)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGestionProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlGestionProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        String nombre = txtNombre.getText().trim();

    if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El nombre del producto no puede estar vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // VALIDACIÓN: Evita que se registre un producto con un nombre que ya existe
    if (controlador.existePorNombre(nombre)) {
        JOptionPane.showMessageDialog(this,
                "⚠️ Ya existe un producto registrado con el nombre: \"" + nombre + "\".\n"
                + "No se permiten nombres duplicados en el catálogo.",
                "Producto Duplicado", JOptionPane.WARNING_MESSAGE);
        txtNombre.requestFocus();
        return; // Detiene el guardado
    }
        if (!validarCampos()) {
    return;
}

Producto producto = construirProductoDesdeFormulario();
boolean guardado = controlador.registrar(producto);

if (guardado) {
    JOptionPane.showMessageDialog(this, "Producto guardado correctamente.");
    listarProductos();
    limpiar();
} else {
    JOptionPane.showMessageDialog(this, "No se pudo guardar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        String nombre = txtNombre.getText().trim();

    // VALIDACIÓN: Verifica si otro producto diferente ya tiene ese mismo nombre
    if (controlador.existePorNombre(nombre, idProductoSeleccionado)) {
        JOptionPane.showMessageDialog(this,
                "⚠️ Ya existe otro producto registrado con el nombre: \"" + nombre + "\".\n"
                + "Por favor, elija un nombre diferente.",
                "Nombre no Disponible", JOptionPane.WARNING_MESSAGE);
        txtNombre.requestFocus();
        return; // Detiene la actualización
    }
        if (idProductoSeleccionado == 0) {
    JOptionPane.showMessageDialog(this, "Seleccione primero un producto de la tabla.");
    return;
}

if (!validarCampos()) {
    return;
}

Producto producto = construirProductoDesdeFormulario();
producto.setIdProducto(idProductoSeleccionado);

boolean actualizado = controlador.actualizar(producto);

if (actualizado) {
    JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
    listarProductos();
    limpiar();
} else {
    JOptionPane.showMessageDialog(this, "No se pudo actualizar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (idProductoSeleccionado == 0) {
    JOptionPane.showMessageDialog(this, "Seleccione primero un producto de la tabla.");
    return;
}

int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Está seguro de eliminar este producto?\n"
        + "Nota: si el producto ya tiene pedidos registrados, no podrá eliminarse;\n"
        + "en ese caso márquelo como \"No disponible\".",
        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

if (confirmacion == JOptionPane.YES_OPTION) {
    boolean eliminado = controlador.eliminar(idProductoSeleccionado);
    if (eliminado) {
        JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
        listarProductos();
        limpiar();
    } else {
        JOptionPane.showMessageDialog(this,
                "No se pudo eliminar el producto (probablemente ya tiene pedidos asociados).",
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        String texto = JOptionPane.showInputDialog(this, "Escriba el nombre del producto a buscar:");
if (texto == null || texto.trim().isEmpty()) {
    return;
}

DefaultTableModel modelo = new DefaultTableModel();
modelo.addColumn("ID");
modelo.addColumn("Platillo / Bebida");
modelo.addColumn("Precio ($)");
modelo.addColumn("Categoria");
modelo.addColumn("Disponibilidad");
tblProductos.setModel(modelo);

List<Producto> lista = controlador.listar();
int coincidencias = 0;

for (Producto p : lista) {
    if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
        Object[] fila = {
            p.getIdProducto(),
            p.getNombre(),
            p.getPrecio(),
            p.getCategoria().getNombreCategoria(),
            p.isDisponible() ? "Disponible" : "No disponible"
        };
        modelo.addRow(fila);
        coincidencias++;
    }
}
lblTotalProductos.setText("Total de productos: " + coincidencias);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        limpiar();
        listarProductos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void tblProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductosMouseClicked
        // TODO add your handling code here:
        int fila = tblProductos.getSelectedRow();
if (fila >= 0) {
    idProductoSeleccionado = (int) tblProductos.getValueAt(fila, 0);

    txtNombre.setText(tblProductos.getValueAt(fila, 1).toString());
    txtPrecio.setText(tblProductos.getValueAt(fila, 2).toString());

    String nombreCategoria = tblProductos.getValueAt(fila, 3).toString();
    for (int i = 0; i < cbxCategoria.getItemCount(); i++) {
        if (cbxCategoria.getItemAt(i).getNombreCategoria().equals(nombreCategoria)) {
            cbxCategoria.setSelectedIndex(i);
            break;
        }
    }

    chkDisponible.setSelected(tblProductos.getValueAt(fila, 4).toString().equals("Disponible"));
}
    }//GEN-LAST:event_tblProductosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JComboBox<Categoria> cbxCategoria;
    private javax.swing.JCheckBox chkDisponible;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCategoria;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblTituloVentana;
    private javax.swing.JLabel lblTotalProductos;
    private javax.swing.JPanel pnlAcciones;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlGestionProductos;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
