/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vista;
import controlador.ControladorUsuario; 
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Rol;                    
import modelo.Usuario;                
/**
 *
 * @author dantj
 */
public class FrmGestionUsuarios extends javax.swing.JInternalFrame {
    /**
     * Creates new form FrmGestionUsuarios
     */
    private ControladorUsuario controlador = new ControladorUsuario();
private int idUsuarioSeleccionado = 0; // 0 = ningún registro seleccionado
private String contrasenaActualDelSeleccionado = ""; // Para conservar la contraseña al editar sin capturarla de nuevo
    public FrmGestionUsuarios() {
       initComponents();
    cargarRoles();
    listarUsuarios();
    }
    
    private void cargarRoles() {
    cbxRol.removeAllItems();
    List<Rol> roles = controlador.listarRoles();
    for (Rol rol : roles) {
        cbxRol.addItem(rol);
    }
}

private void listarUsuarios() {
    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("ID");
    modelo.addColumn("Matrícula");
    modelo.addColumn("Nombre");
    modelo.addColumn("Rol");
    modelo.addColumn("Beca");
    modelo.addColumn("Estado");
    tblUsuarios.setModel(modelo);
    // Configuración de estilo para el encabezado de la tabla
tblUsuarios.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
tblUsuarios.getTableHeader().setForeground(new java.awt.Color(122, 15, 42)); // Texto Guinda
tblUsuarios.getTableHeader().setBackground(java.awt.Color.WHITE);

// Anchos de columna proporcionales
tblUsuarios.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
tblUsuarios.getColumnModel().getColumn(1).setPreferredWidth(120); // Matrícula
tblUsuarios.getColumnModel().getColumn(2).setPreferredWidth(260); // Nombre
tblUsuarios.getColumnModel().getColumn(3).setPreferredWidth(110); // Rol
tblUsuarios.getColumnModel().getColumn(4).setPreferredWidth(80);  // Beca
tblUsuarios.getColumnModel().getColumn(5).setPreferredWidth(90);  // Estado

// Renderizador para colorear el estado (Verde para Activa, Rojo para Inactiva) y centrar columnas
tblUsuarios.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Centrar texto en ID, Matrícula, Rol, Beca y Estado
        if (column == 0 || column == 1 || column == 3 || column == 4 || column == 5) {
            setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }

        // Colorear el texto de la columna Estado (columna 5)
        if (column == 5 && value != null) {
            if ("Activa".equals(value.toString())) {
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

    List<Usuario> lista = controlador.listar();
    for (Usuario u : lista) {
        Object[] fila = {
            u.getIdUsuario(),
            u.getMatricula(),
            u.getNombre(),
            u.getRol().getNombreRol(),
            u.getPorcentajeBeca(),
            u.isActivo() ? "Activa" : "Inactiva"
        };
        modelo.addRow(fila);
    }
}

private void limpiar() {
    txtMatricula.setText("");
    txtNombre.setText("");
    txpContrasena.setText("");
    cbxRol.setSelectedIndex(-1);
    txtPorcentajeBeca.setText("0");
    chkActivo.setSelected(true);
    idUsuarioSeleccionado = 0;
    contrasenaActualDelSeleccionado = "";
    tblUsuarios.clearSelection();
}

private boolean validarCampos(boolean esNuevoRegistro) {
    if (txtMatricula.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe capturar la matrícula/usuario.");
        txtMatricula.requestFocus();
        return false;
    }
    if (txtNombre.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe capturar el nombre completo.");
        txtNombre.requestFocus();
        return false;
    }
    if (cbxRol.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Debe seleccionar un rol.");
        return false;
    }
    if (esNuevoRegistro && txpContrasena.getPassword().length == 0) {
        JOptionPane.showMessageDialog(this, "Debe capturar una contraseña.");
        txpContrasena.requestFocus();
        return false;
    }
    try {
        double porcentaje = Double.parseDouble(txtPorcentajeBeca.getText().trim());
        if (porcentaje < 0 || porcentaje > 100) {
            JOptionPane.showMessageDialog(this, "El porcentaje de beca debe estar entre 0 y 100.");
            txtPorcentajeBeca.requestFocus();
            return false;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "El porcentaje de beca debe ser un número válido.");
        txtPorcentajeBeca.requestFocus();
        return false;
    }
    return true;
}

private Usuario construirUsuarioDesdeFormulario() {
    Usuario usuario = new Usuario();
    String matricula = txtMatricula.getText().trim();
    usuario.setMatricula(matricula);
    usuario.setNombreUsuario(matricula);
    usuario.setContrasena(String.valueOf(txpContrasena.getPassword()));
    usuario.setNombre(txtNombre.getText().trim());

    Rol rolSeleccionado = (Rol) cbxRol.getSelectedItem();
    usuario.setRol(rolSeleccionado);
    usuario.setTipoUsuario(rolSeleccionado.getNombreRol());

    double porcentaje = Double.parseDouble(txtPorcentajeBeca.getText().trim());
    usuario.setPorcentajeBeca(porcentaje);
    usuario.setBecado(porcentaje > 0);
    usuario.setActivo(chkActivo.isSelected());

    return usuario;
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        pnlGestionUsuarios = new javax.swing.JPanel();
        lblTituloVentana = new javax.swing.JLabel();
        pnlDatos = new javax.swing.JPanel();
        lblMatricula = new javax.swing.JLabel();
        txtMatricula = new javax.swing.JTextField();
        lblRol = new javax.swing.JLabel();
        cbxRol = new javax.swing.JComboBox<>();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblContrasena = new javax.swing.JLabel();
        txpContrasena = new javax.swing.JPasswordField();
        lblPorcentajeBeca = new javax.swing.JLabel();
        txtPorcentajeBeca = new javax.swing.JTextField();
        chkActivo = new javax.swing.JCheckBox();
        pnlAcciones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setBackground(new java.awt.Color(245, 245, 245));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestión del Catálogo de Usuarios");
        setPreferredSize(new java.awt.Dimension(880, 620));

        lblTituloVentana.setBackground(new java.awt.Color(122, 15, 42));
        lblTituloVentana.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTituloVentana.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloVentana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/usuarios.png"))); // NOI18N
        lblTituloVentana.setText("   Gestión del catálogo de usuarios (Administrador, Alumno, Docente)");
        lblTituloVentana.setToolTipText("");
        lblTituloVentana.setIconTextGap(12);
        lblTituloVentana.setOpaque(true);
        lblTituloVentana.setPreferredSize(new java.awt.Dimension(850, 40));

        pnlDatos.setBackground(new java.awt.Color(255, 255, 255));
        pnlDatos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));

        lblMatricula.setForeground(new java.awt.Color(51, 51, 51));
        lblMatricula.setText("Matricula / Usuario:");

        txtMatricula.setPreferredSize(new java.awt.Dimension(200, 30));

        lblRol.setText("Rol:");

        cbxRol.setPreferredSize(new java.awt.Dimension(220, 30));

        lblNombre.setForeground(new java.awt.Color(51, 51, 51));
        lblNombre.setText("Nombre Completo:");

        txtNombre.setPreferredSize(new java.awt.Dimension(350, 30));

        lblContrasena.setForeground(new java.awt.Color(51, 51, 51));
        lblContrasena.setText("Contraseña:");

        lblPorcentajeBeca.setForeground(new java.awt.Color(51, 51, 51));
        lblPorcentajeBeca.setText("Porcentaje de Beca:");

        txtPorcentajeBeca.setText("0");
        txtPorcentajeBeca.setPreferredSize(new java.awt.Dimension(180, 30));

        chkActivo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        chkActivo.setForeground(new java.awt.Color(122, 15, 42));
        chkActivo.setSelected(true);
        chkActivo.setText("Cuenta Activa");

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblContrasena)
                                .addGap(52, 52, 52)))
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txpContrasena)
                            .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblMatricula)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(53, 53, 53)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPorcentajeBeca)
                    .addComponent(lblRol))
                .addGap(18, 18, 18)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPorcentajeBeca, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkActivo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblMatricula)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(txtMatricula, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(12, 12, 12)))
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addGap(20, 20, 20))
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRol))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPorcentajeBeca)
                            .addComponent(txtPorcentajeBeca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblContrasena)
                    .addComponent(txpContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(chkActivo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pnlAcciones.setBackground(new java.awt.Color(255, 255, 255));
        pnlAcciones.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(122, 15, 42));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/guardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardar.setIconTextGap(8);
        btnGuardar.setPreferredSize(new java.awt.Dimension(120, 38));
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
        btnModificar.setPreferredSize(new java.awt.Dimension(120, 38));
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
        btnEliminar.setPreferredSize(new java.awt.Dimension(120, 38));
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
        btnBuscar.setPreferredSize(new java.awt.Dimension(120, 38));
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
        btnLimpiar.setPreferredSize(new java.awt.Dimension(120, 38));
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
                .addGap(60, 60, 60)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        pnlAccionesLayout.setVerticalGroup(
            pnlAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccionesLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(pnlAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Matricula", "Nombre", "Rol", "Beca", "Estado"
            }
        ));
        tblUsuarios.setGridColor(new java.awt.Color(230, 230, 230));
        tblUsuarios.setRowHeight(30);
        tblUsuarios.setShowHorizontalLines(true);
        tblUsuarios.setShowVerticalLines(true);
        tblUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblUsuarios);

        javax.swing.GroupLayout pnlGestionUsuariosLayout = new javax.swing.GroupLayout(pnlGestionUsuarios);
        pnlGestionUsuarios.setLayout(pnlGestionUsuariosLayout);
        pnlGestionUsuariosLayout.setHorizontalGroup(
            pnlGestionUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGestionUsuariosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGestionUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloVentana, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlGestionUsuariosLayout.createSequentialGroup()
                        .addGroup(pnlGestionUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlAcciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addContainerGap())))
        );
        pnlGestionUsuariosLayout.setVerticalGroup(
            pnlGestionUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGestionUsuariosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTituloVentana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGestionUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGestionUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        if (!validarCampos(true)) {
    return;
}

Usuario usuario = construirUsuarioDesdeFormulario();
boolean guardado = controlador.registrar(usuario);

if (guardado) {
    JOptionPane.showMessageDialog(this, "Usuario guardado correctamente.");
    listarUsuarios();
    limpiar();
} else {
    JOptionPane.showMessageDialog(this,
            "No se pudo guardar el usuario. Verifique que la matrícula no esté repetida.",
            "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
        if (idUsuarioSeleccionado == 0) {
    JOptionPane.showMessageDialog(this, "Seleccione primero un usuario de la tabla.");
    return;
}

if (!validarCampos(false)) {
    return;
}

Usuario usuario = construirUsuarioDesdeFormulario();
usuario.setIdUsuario(idUsuarioSeleccionado);

// Si la contraseña se dejó en blanco, se conserva la que ya tenía
if (usuario.getContrasena().isEmpty()) {
    usuario.setContrasena(contrasenaActualDelSeleccionado);
}

boolean actualizado = controlador.actualizar(usuario);

if (actualizado) {
    JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
    listarUsuarios();
    limpiar();
} else {
    JOptionPane.showMessageDialog(this, "No se pudo actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (idUsuarioSeleccionado == 0) {
    JOptionPane.showMessageDialog(this, "Seleccione primero un usuario de la tabla.");
    return;
}

int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Está seguro de eliminar este usuario? También se eliminarán todos sus pedidos.",
        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

if (confirmacion == JOptionPane.YES_OPTION) {
    boolean eliminado = controlador.eliminar(idUsuarioSeleccionado);
    if (eliminado) {
        JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
        listarUsuarios();
        limpiar();
    } else {
        JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        String texto = JOptionPane.showInputDialog(this, "Escriba la matrícula o el nombre a buscar:");
if (texto == null || texto.trim().isEmpty()) {
    return;
}

DefaultTableModel modelo = new DefaultTableModel();
modelo.addColumn("ID");
modelo.addColumn("Matrícula");
modelo.addColumn("Nombre");
modelo.addColumn("Rol");
modelo.addColumn("Beca");
modelo.addColumn("Estado");
tblUsuarios.setModel(modelo);

List<Usuario> lista = controlador.listar();
for (Usuario u : lista) {
    if (u.getMatricula().toLowerCase().contains(texto.toLowerCase())
            || u.getNombre().toLowerCase().contains(texto.toLowerCase())) {
        Object[] fila = {
            u.getIdUsuario(),
            u.getMatricula(),
            u.getNombre(),
            u.getRol().getNombreRol(),
            u.getPorcentajeBeca(),
            u.isActivo() ? "Activa" : "Inactiva"
        };
        modelo.addRow(fila);
    }
}
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void tblUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuariosMouseClicked
        // TODO add your handling code here:
        int fila = tblUsuarios.getSelectedRow();
    if (fila >= 0) {
        idUsuarioSeleccionado = (int) tblUsuarios.getValueAt(fila, 0);

        // Se recupera el registro completo desde el controlador
        Usuario u = controlador.buscarPorId(idUsuarioSeleccionado);
        if (u == null) {
            return;
        }

        txtMatricula.setText(u.getMatricula());
        txtNombre.setText(u.getNombre());
        txtPorcentajeBeca.setText(String.valueOf(u.getPorcentajeBeca()));
        chkActivo.setSelected(u.isActivo());
        txpContrasena.setText(""); // Por seguridad no se muestra la contraseña
        contrasenaActualDelSeleccionado = u.getContrasena();

        for (int i = 0; i < cbxRol.getItemCount(); i++) {
            if (cbxRol.getItemAt(i).getIdRol() == u.getRol().getIdRol()) {
                cbxRol.setSelectedIndex(i);
                break;
            }
        }
    }
    }//GEN-LAST:event_tblUsuariosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JComboBox<Rol> cbxRol;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblContrasena;
    private javax.swing.JLabel lblMatricula;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPorcentajeBeca;
    private javax.swing.JLabel lblRol;
    private javax.swing.JLabel lblTituloVentana;
    private javax.swing.JPanel pnlAcciones;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlGestionUsuarios;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JPasswordField txpContrasena;
    private javax.swing.JTextField txtMatricula;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPorcentajeBeca;
    // End of variables declaration//GEN-END:variables
}
