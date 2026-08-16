package vista;
import controlador.ControladorPedido;
import controlador.ControladorProducto;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalTime;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Categoria;
import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Producto;
import modelo.Usuario;
import utilidades.Sesion;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement;
import java.time.LocalTime;
/**
 *
 * @author dantj
 */
public class FrmRealizarPedido extends javax.swing.JInternalFrame {

    /**
     * Creates new form FrmRealizarPedido
     */
    private ControladorPedido controladorPedido = new ControladorPedido();
private ControladorProducto controladorProducto = new ControladorProducto();
private Pedido carritoActual = new Pedido();
private Usuario usuarioActivo;

private static final Color MARRON_GUINDA = new Color(88, 17, 36);
    private static final Color VERDE_DESCUENTO = new Color(0, 130, 60);
    public FrmRealizarPedido() {
    initComponents();
   this.usuarioActivo = Sesion.getUsuario();
        cargarCategorias();
        cargarTablaCatalogo(null);
        actualizarCarrito();
    }
    
 public static TimePickerSettings crearAjustesHora() {
    // Usamos Locale.ENGLISH para evitar desfases con el texto "a. m. / p. m."
    com.github.lgooddatepicker.components.TimePickerSettings settings = 
            new com.github.lgooddatepicker.components.TimePickerSettings(java.util.Locale.ENGLISH);
    
    // Formato de 12 horas AM/PM
    java.time.format.DateTimeFormatter formato12h = 
            java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ENGLISH);
    settings.setFormatForDisplayTime(formato12h);
    settings.setFormatForMenuTimes(formato12h);
    
    // Horario escolar: 08:00 AM a 03:00 PM (menos elementos evita el fallo de la lista)
    settings.generatePotentialMenuTimes(
            com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement.FifteenMinutes,
            java.time.LocalTime.of(8, 0),
            java.time.LocalTime.of(15, 0)
    );
    
    // Permitir escribir directamente con teclado
    settings.setAllowKeyboardEditing(false);
    
    // Estilos visuales
    settings.fontValidTime = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
    settings.setColor(com.github.lgooddatepicker.components.TimePickerSettings.TimeArea.TimePickerTextValidTime, 
                      new java.awt.Color(88, 17, 36));
    // Iniciar con el campo en blanco para exigir selección manual
    settings.initialTime = null;
    
    return settings;
}
    
    private void cargarCategorias() {
    cbxCategoria.removeAllItems();
    List<Categoria> categorias = controladorProducto.listarCategorias();
    for (Categoria c : categorias) {
        cbxCategoria.addItem(c);
    }
    cbxCategoria.setSelectedIndex(-1); // Inicia sin selección para mostrar todo
}

private void cargarTablaCatalogo(String nombreCategoriaFiltro) {
    DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false; // Desactiva edición de celdas
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            // Le indica a Java Swing que la columna 1 dibuje una imagen
            if (columnIndex == 1) {
                return javax.swing.ImageIcon.class;
            }
            return Object.class;
        }
    };

    // 1. Estructura de columnas con "Foto" incluida
    modelo.addColumn("ID");
    modelo.addColumn("Foto");
    modelo.addColumn("Platillo / Bebida");
    modelo.addColumn("Categoría");
    modelo.addColumn("Precio ($)");
    tblCatalogo.setModel(modelo);

    // 2. Altura de fila adecuada para que luzca la foto
    tblCatalogo.setRowHeight(52);

    // 3. Ancho de columnas
    if (tblCatalogo.getColumnModel().getColumnCount() > 0) {
        tblCatalogo.getColumnModel().getColumn(0).setPreferredWidth(35);  // ID
        tblCatalogo.getColumnModel().getColumn(1).setPreferredWidth(55);  // Foto
        tblCatalogo.getColumnModel().getColumn(2).setPreferredWidth(170); // Nombre
        tblCatalogo.getColumnModel().getColumn(3).setPreferredWidth(100); // Categoría
        tblCatalogo.getColumnModel().getColumn(4).setPreferredWidth(75);  // Precio
    }

    // 4. Llenar filas desde MySQL
    List<Producto> disponibles = controladorProducto.listarDisponibles();
    for (Producto p : disponibles) {
        if (nombreCategoriaFiltro != null
                && !"Todas".equals(nombreCategoriaFiltro)
                && !p.getCategoria().getNombreCategoria().equalsIgnoreCase(nombreCategoriaFiltro)) {
            continue;
        }

        // Obtener miniatura del platillo
        javax.swing.ImageIcon foto = obtenerFotoProducto(p.getNombre());

        Object[] fila = {
            p.getIdProducto(),
            foto,
            p.getNombre(),
            p.getCategoria().getNombreCategoria(),
            String.format("%.2f", p.getPrecio())
        };
        modelo.addRow(fila);
    }

    // 5. Estilos de encabezado
    tblCatalogo.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    tblCatalogo.getTableHeader().setForeground(java.awt.Color.BLACK);
    tblCatalogo.getTableHeader().setBackground(new java.awt.Color(88, 17, 36));

    // Centrar columnas de texto (ID, Categoría y Precio)
    javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    tblCatalogo.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    tblCatalogo.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
    tblCatalogo.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
}

/**
 * Recalcula el carrito aplicando el descuento de beca ÚNICAMENTE a los 
 * productos que correspondan a Paquetes / Desayunos / Comidas del día.
 */
private void actualizarCarrito() {
    DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    modelo.addColumn("Cantidad");
    modelo.addColumn("Producto");
    modelo.addColumn("Precio");
    modelo.addColumn("Subtotal");
    tblCarrito.setModel(modelo);

    double subtotalGeneral = 0.0;
    double subtotalAplicaBeca = 0.0;

    for (DetallePedido d : carritoActual.getDetalles()) {
        d.calcularSubtotal();
        subtotalGeneral += d.getSubtotal();

        // VALIDACIÓN: Verificar si el producto es un paquete/desayuno/comida elegible para beca
        String catNombre = (d.getProducto() != null && d.getProducto().getCategoria() != null)
                ? d.getProducto().getCategoria().getNombreCategoria().toLowerCase()
                : "";
        String prodNombre = (d.getProducto() != null)
                ? d.getProducto().getNombre().toLowerCase()
                : "";

        if (catNombre.contains("paquete") || catNombre.contains("desayuno") || catNombre.contains("comida")
                || prodNombre.contains("paquete") || prodNombre.contains("desayuno del día") || prodNombre.contains("comida del día")) {
            subtotalAplicaBeca += d.getSubtotal();
        }

        Object[] fila = {
            d.getCantidad(),
            d.getProducto().getNombre(),
            String.format("$%.2f", d.getPrecioUnitario()),
            String.format("$%.2f", d.getSubtotal())
        };
        modelo.addRow(fila);
    }

    // 1. Mostrar Subtotal General
    lblSubtotalValor.setText(String.format("$%.2f", subtotalGeneral));

// 2. Calcular Descuento por Beca (Solo sobre productos que sean Paquetes / Desayunos)
    double descuento = 0.0;
    if (usuarioActivo != null && usuarioActivo.isBecado() && usuarioActivo.getPorcentajeBeca() > 0) {
        double porcentaje = usuarioActivo.getPorcentajeBeca(); // Ej. 50.0 o 100.0
        
        // Solo aplica descuento si en el carrito hay al menos un paquete elegible
        if (subtotalAplicaBeca > 0) {
            descuento = subtotalAplicaBeca * (porcentaje / 100.0);
            lblDescuentoBecaTexto.setVisible(true);
            lblDescuentoBecaValor.setVisible(true);
            lblDescuentoBecaValor.setText(String.format("-$%.2f (%.0f%%)", descuento, porcentaje));
        } else {
            lblDescuentoBecaTexto.setVisible(false);
            lblDescuentoBecaValor.setVisible(false);
        }
    } else {
        lblDescuentoBecaTexto.setVisible(false);
        lblDescuentoBecaValor.setVisible(false);
    }

    // 3. Calcular Total Final
        double totalFinal = subtotalGeneral - descuento;
        if (totalFinal < 0) {
            totalFinal = 0.0;
        }
        
        // ASIGNAR EL TOTAL CON DESCUENTO DIRECTAMENTE AL PEDIDO
        carritoActual.setTotal(totalFinal);
        
        lblTotalPagarValor.setText(String.format("$%.2f", totalFinal));
}

/**
 * Crea y devuelve la configuración del TimePicker para inicializar el componente
 * con formato 12h (AM/PM) e intervalos de 15 minutos sin desfase de índices.
 */

private void limpiarFormulario() {
    carritoActual = new Pedido();
    txtCantidad.setText("1");
    txtIndicacionesEspeciales.setText("");
    
   // Restablecer el TimePicker a estado vacío
    if (timePickerRecoleccion != null) {
        timePickerRecoleccion.setTime(null);
    }

    actualizarCarrito();
}

/**
 * Busca y escala la imagen de un platillo a 45x45 px.
 * Si no encuentra la imagen específica, carga una por defecto.
 */
/**
 * Busca y escala la imagen de un platillo a 45x45 px.
 * Soporta nombres específicos (ej. chilaquilesverdes.png, aguafrescajamaica.png),
 * nombres con guiones bajos, o variaciones sin unidades (500ml).
 */
private javax.swing.ImageIcon obtenerFotoProducto(String nombreProducto) {
    int ancho = 45;
    int alto = 45;

    // 1. Quitar acentos y convertir a minúsculas
    String limpio = nombreProducto.toLowerCase()
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");

    // 2. Quitar medidas numéricas (ej. "500ml", "1lt", etc.)
    String sinMedidas = limpio.replaceAll("\\d+.*", "").trim();

    // 3. Lista de nombres que intentará buscar en orden de especificidad
    String[] intentos = {
        limpio.replace(" ", ""),           // chilaquilesverdes.png / aguafrescajamaica500ml.png
        sinMedidas.replace(" ", ""),       // aguafrescajamaica.png
        limpio.replace(" ", "_"),          // chilaquiles_verdes.png
        sinMedidas.replace(" ", "_"),      // agua_fresca_jamaica.png
        limpio.split(" ")[0]               // chilaquiles.png / agua.png (como respaldo)
    };

    java.net.URL url = null;
    for (String intento : intentos) {
        url = getClass().getResource("/imagenes/" + intento + ".png");
        if (url != null) {
            break; // Se encontró la imagen
        }
    }

    // 4. Si ninguna coincide, carga la imagen por defecto
    if (url == null) {
        url = getClass().getResource("/imagenes/comida_default.png");
    }

    if (url != null) {
        java.awt.Image img = new javax.swing.ImageIcon(url).getImage();
        java.awt.Image escalada = img.getScaledInstance(ancho, alto, java.awt.Image.SCALE_SMOOTH);
        return new javax.swing.ImageIcon(escalada);
    }

    return null;
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlRealizarPedido = new javax.swing.JPanel();
        lblTituloVentana = new javax.swing.JLabel();
        pnlSeleccion = new javax.swing.JPanel();
        lblCategoria = new javax.swing.JLabel();
        cbxCategoria = new javax.swing.JComboBox<>();
        lblCantidad = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        btnAgregarCarrito = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCatalogo = new javax.swing.JTable();
        pnlCarrito = new javax.swing.JPanel();
        lblCarrito = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCarrito = new javax.swing.JTable();
        lblIndicaciones = new javax.swing.JLabel();
        txtIndicacionesEspeciales = new javax.swing.JTextField();
        lblHoraRecoleccion = new javax.swing.JLabel();
        timePickerRecoleccion = new com.github.lgooddatepicker.components.TimePicker(crearAjustesHora());
        btnConfirmarPedido = new javax.swing.JButton();
        pnlResumen = new javax.swing.JPanel();
        lblSubtotalTitulo = new javax.swing.JLabel();
        lblSubtotalValor = new javax.swing.JLabel();
        lblDescuentoBecaTexto = new javax.swing.JLabel();
        lblDescuentoBecaValor = new javax.swing.JLabel();
        pnlTotalPagar = new javax.swing.JLabel();
        lblTotalPagarValor = new javax.swing.JLabel();
        lblResumenPago = new javax.swing.JLabel();
        btnQuitarCarrito = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Realizar Pedido");
        setPreferredSize(new java.awt.Dimension(1020, 680));

        javax.swing.GroupLayout pnlRealizarPedidoLayout = new javax.swing.GroupLayout(pnlRealizarPedido);
        pnlRealizarPedido.setLayout(pnlRealizarPedidoLayout);
        pnlRealizarPedidoLayout.setHorizontalGroup(
            pnlRealizarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlRealizarPedidoLayout.setVerticalGroup(
            pnlRealizarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        lblTituloVentana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconopedidos.png"))); // NOI18N
        lblTituloVentana.setText("CAFETERÍA UNIVERSITARIA  |  Módulo de Pre-Pedidos");
        lblTituloVentana.setBackground(new java.awt.Color(88, 17, 36));
        lblTituloVentana.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTituloVentana.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloVentana.setIconTextGap(12);
        lblTituloVentana.setOpaque(true);
        lblTituloVentana.setPreferredSize(new java.awt.Dimension(1000, 55));

        pnlSeleccion.setBackground(new java.awt.Color(255, 255, 255));
        pnlSeleccion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Catálogo de Platillos Disponibles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(122, 15, 42))); // NOI18N
        pnlSeleccion.setPreferredSize(new java.awt.Dimension(450, 480));

        lblCategoria.setText("Categoría:");
        lblCategoria.setPreferredSize(new java.awt.Dimension(200, 30));

        cbxCategoria.setPreferredSize(new java.awt.Dimension(200, 30));
        cbxCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCategoriaActionPerformed(evt);
            }
        });

        lblCantidad.setText("Cantidad:");
        lblCantidad.setPreferredSize(new java.awt.Dimension(70, 30));

        txtCantidad.setText("1");
        txtCantidad.setPreferredSize(new java.awt.Dimension(70, 30));

        btnAgregarCarrito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/agregarcarrito.png"))); // NOI18N
        btnAgregarCarrito.setText("Agregar al Carrito");
        btnAgregarCarrito.setBackground(new java.awt.Color(88, 17, 36));
        btnAgregarCarrito.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregarCarrito.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAgregarCarrito.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarCarrito.setIconTextGap(8);
        btnAgregarCarrito.setPreferredSize(new java.awt.Dimension(180, 35));
        btnAgregarCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCarritoActionPerformed(evt);
            }
        });

        jScrollPane2.setPreferredSize(new java.awt.Dimension(420, 280));

        tblCatalogo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Foto", "ID", "Platillo / Bebida", "Categoría", "Precio ($)"
            }
        ));
        tblCatalogo.setGridColor(new java.awt.Color(230, 230, 230));
        tblCatalogo.setRowHeight(28);
        tblCatalogo.setShowHorizontalLines(true);
        tblCatalogo.setShowVerticalLines(true);
        jScrollPane2.setViewportView(tblCatalogo);

        javax.swing.GroupLayout pnlSeleccionLayout = new javax.swing.GroupLayout(pnlSeleccion);
        pnlSeleccion.setLayout(pnlSeleccionLayout);
        pnlSeleccionLayout.setHorizontalGroup(
            pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSeleccionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSeleccionLayout.createSequentialGroup()
                        .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSeleccionLayout.createSequentialGroup()
                                .addComponent(lblCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlSeleccionLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(lblCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(168, 168, 168))
                    .addGroup(pnlSeleccionLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(pnlSeleccionLayout.createSequentialGroup()
                .addGap(137, 137, 137)
                .addComponent(btnAgregarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSeleccionLayout.setVerticalGroup(
            pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSeleccionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAgregarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCarrito.setBackground(new java.awt.Color(255, 255, 255));
        pnlCarrito.setPreferredSize(new java.awt.Dimension(480, 540));

        lblCarrito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/carritocompras.png"))); // NOI18N
        lblCarrito.setText("Mi Carrito de Compras");
        lblCarrito.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblCarrito.setForeground(new java.awt.Color(122, 15, 42));
        lblCarrito.setIconTextGap(8);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(450, 130));

        tblCarrito.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        tblCarrito.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cantidad", "Producto", "Precio", "Subtotal"
            }
        ));
        tblCarrito.setRowHeight(28);
        jScrollPane1.setViewportView(tblCarrito);

        lblIndicaciones.setText("Indicaciones Especiales (ej. Sin cebolla, extra salsa):");
        lblIndicaciones.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        txtIndicacionesEspeciales.setPreferredSize(new java.awt.Dimension(450, 45));

        lblHoraRecoleccion.setText("Hora de Recolección Estimada:");
        lblHoraRecoleccion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        btnConfirmarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/enviar.png"))); // NOI18N
        btnConfirmarPedido.setText("Confirmar y Enviar Pedido");
        btnConfirmarPedido.setBackground(new java.awt.Color(88, 17, 36));
        btnConfirmarPedido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirmarPedido.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnConfirmarPedido.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmarPedido.setIconTextGap(8);
        btnConfirmarPedido.setPreferredSize(new java.awt.Dimension(450, 40));
        btnConfirmarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarPedidoActionPerformed(evt);
            }
        });

        pnlResumen.setBackground(new java.awt.Color(234, 239, 245));
        pnlResumen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 224, 233)));
        pnlResumen.setPreferredSize(new java.awt.Dimension(250, 85));

        lblSubtotalTitulo.setText("Subtotal:");
        lblSubtotalTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSubtotalTitulo.setForeground(new java.awt.Color(50, 50, 50));

        lblSubtotalValor.setText("$0.00");
        lblSubtotalValor.setForeground(new java.awt.Color(55, 55, 55));

        lblDescuentoBecaTexto.setText("Descuento por Beca:");
        lblDescuentoBecaTexto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDescuentoBecaTexto.setForeground(new java.awt.Color(0, 130, 60));

        lblDescuentoBecaValor.setText("-$0.00");
        lblDescuentoBecaValor.setForeground(new java.awt.Color(0, 130, 60));

        pnlTotalPagar.setText("Total a Pagar:");
        pnlTotalPagar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        lblTotalPagarValor.setText("$0.00");
        lblTotalPagarValor.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalPagarValor.setForeground(new java.awt.Color(88, 17, 36));

        lblResumenPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/resumenpago.png"))); // NOI18N
        lblResumenPago.setText("Resumen de Pago");
        lblResumenPago.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblResumenPago.setForeground(new java.awt.Color(122, 15, 42));
        lblResumenPago.setIconTextGap(8);

        javax.swing.GroupLayout pnlResumenLayout = new javax.swing.GroupLayout(pnlResumen);
        pnlResumen.setLayout(pnlResumenLayout);
        pnlResumenLayout.setHorizontalGroup(
            pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResumenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlResumenLayout.createSequentialGroup()
                        .addComponent(lblResumenPago)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlResumenLayout.createSequentialGroup()
                        .addGroup(pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlResumenLayout.createSequentialGroup()
                                .addComponent(pnlTotalPagar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTotalPagarValor))
                            .addGroup(pnlResumenLayout.createSequentialGroup()
                                .addGroup(pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblSubtotalTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDescuentoBecaTexto))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblSubtotalValor, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblDescuentoBecaValor, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(5, 5, 5)))
                        .addGap(19, 19, 19))))
        );
        pnlResumenLayout.setVerticalGroup(
            pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResumenLayout.createSequentialGroup()
                .addComponent(lblResumenPago)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSubtotalValor)
                    .addComponent(lblSubtotalTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescuentoBecaTexto)
                    .addComponent(lblDescuentoBecaValor))
                .addGap(18, 18, 18)
                .addGroup(pnlResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalPagarValor)
                    .addComponent(pnlTotalPagar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnQuitarCarrito.setBackground(new java.awt.Color(122, 15, 42));
        btnQuitarCarrito.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnQuitarCarrito.setForeground(new java.awt.Color(255, 255, 255));
        btnQuitarCarrito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/quitarcarrito.png"))); // NOI18N
        btnQuitarCarrito.setText("Quitar");
        btnQuitarCarrito.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuitarCarrito.setIconTextGap(8);
        btnQuitarCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarCarritoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlCarritoLayout = new javax.swing.GroupLayout(pnlCarrito);
        pnlCarrito.setLayout(pnlCarritoLayout);
        pnlCarritoLayout.setHorizontalGroup(
            pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCarritoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCarritoLayout.createSequentialGroup()
                        .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .addComponent(txtIndicacionesEspeciales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .addComponent(btnConfirmarPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlCarritoLayout.createSequentialGroup()
                                .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblIndicaciones)
                                    .addGroup(pnlCarritoLayout.createSequentialGroup()
                                        .addComponent(lblHoraRecoleccion)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(timePickerRecoleccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(pnlCarritoLayout.createSequentialGroup()
                        .addComponent(lblCarrito)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnQuitarCarrito)
                        .addContainerGap())))
        );
        pnlCarritoLayout.setVerticalGroup(
            pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCarritoLayout.createSequentialGroup()
                .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCarrito)
                    .addComponent(btnQuitarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIndicaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIndicacionesEspeciales, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHoraRecoleccion)
                    .addComponent(timePickerRecoleccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlResumen, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConfirmarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSeleccion, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTituloVentana, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlRealizarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlRealizarPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTituloVentana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlSeleccion, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                    .addComponent(pnlCarrito, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbxCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxCategoriaActionPerformed
        // TODO add your handling code here:
     Object seleccionado = cbxCategoria.getSelectedItem();
    if (seleccionado == null || "Todas".equals(seleccionado)) {
        cargarTablaCatalogo(null);
    } else {
        cargarTablaCatalogo(seleccionado.toString());
    }
    }//GEN-LAST:event_cbxCategoriaActionPerformed

    private void btnAgregarCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCarritoActionPerformed
        // TODO add your handling code here:
 // 1. Validar que se haya seleccionado un producto de la tabla
    int fila = tblCatalogo.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla del catálogo.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2. Validar que la cantidad sea un número entero válido y mayor a cero
    int cantidad;
    try {
        cantidad = Integer.parseInt(txtCantidad.getText().trim());
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Cantidad Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Ingrese una cantidad entera válida.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 3. Obtener el producto desde la base de datos usando el ID de la columna 0
    int idProducto = Integer.parseInt(tblCatalogo.getValueAt(fila, 0).toString());
    Producto productoSeleccionado = controladorProducto.obtenerPorId(idProducto);

    if (productoSeleccionado == null) {
        return;
    }

    // 4. Crear el detalle del pedido
    DetallePedido nuevo = new DetallePedido();
    nuevo.setProducto(productoSeleccionado);
    nuevo.setCantidad(cantidad);
    nuevo.setPrecioUnitario(productoSeleccionado.getPrecio());

    String nombreProd = productoSeleccionado.getNombre();

   // 5. Si es un paquete o menú del día con opciones entre paréntesis
    String nombreMin = nombreProd.toLowerCase();
    if ((nombreMin.contains("paquete") || nombreMin.contains("desayuno") || nombreMin.contains("comida"))
            && nombreProd.contains("(") && nombreProd.contains(")")) {

        // A. Extraer y separar los guisados del día
        String textoOpciones = nombreProd.substring(nombreProd.indexOf("(") + 1, nombreProd.lastIndexOf(")"));
        String[] listaGuisados = textoOpciones.split(",");

        java.util.List<javax.swing.JCheckBox> checks = new java.util.ArrayList<>();
        java.util.List<Object> panelComponentes = new java.util.ArrayList<>();
        
        panelComponentes.add("1. Selecciona los guisados para tu plato:");

        for (String guisado : listaGuisados) {
            if (!guisado.trim().isEmpty()) {
                javax.swing.JCheckBox chk = new javax.swing.JCheckBox(guisado.trim());
                checks.add(chk);
                panelComponentes.add(chk);
            }
        }

        // B. Crear el selector de bebida incluida en el paquete
        panelComponentes.add(" "); // Separador visual
        panelComponentes.add("2. Selecciona tu bebida incluida:");
        
        String[] opcionesBebida = {"Café", "Té", "Agua de Sabor del Día"};
        javax.swing.JComboBox<String> cbxBebida = new javax.swing.JComboBox<>(opcionesBebida);
        panelComponentes.add(cbxBebida);

        // C. Mostrar la ventana interactiva
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                panelComponentes.toArray(),
                "Personalizar " + productoSeleccionado.getNombre(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (respuesta == JOptionPane.OK_OPTION) {
            StringBuilder incluidos = new StringBuilder();
            StringBuilder omitidos = new StringBuilder();

            for (javax.swing.JCheckBox chk : checks) {
                if (chk.isSelected()) {
                    incluidos.append(chk.getText()).append(", ");
                } else {
                    omitidos.append(chk.getText()).append(", ");
                }
            }

            String bebidaSeleccionada = (String) cbxBebida.getSelectedItem();

            // Construir texto estructurado con guisados y bebida
            String textoFinal = "";
            if (incluidos.length() > 0) {
                textoFinal = "Lleva: " + incluidos.substring(0, incluidos.length() - 2);
            }
            
            // Añadir la bebida elegida
            textoFinal += (textoFinal.isEmpty() ? "" : " | ") + "Bebida: " + bebidaSeleccionada;

            // Añadir los guisados no seleccionados
            if (omitidos.length() > 0) {
                textoFinal += " | Sin: " + omitidos.substring(0, omitidos.length() - 2);
            }

            nuevo.setIndicacionesEspeciales(textoFinal);
        } else {
            return; // Si cancela, no se añade al carrito
        }
    }

    // 6. Agregar al carrito y refrescar la interfaz
    carritoActual.agregarDetalle(nuevo);
    actualizarCarrito();
    txtCantidad.setText("1");
    }//GEN-LAST:event_btnAgregarCarritoActionPerformed

    private void btnConfirmarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarPedidoActionPerformed
        // TODO add your handling code here:
 if (carritoActual.getDetalles().isEmpty()) {
        JOptionPane.showMessageDialog(this, "El carrito está vacío. Agregue productos antes de confirmar.");
        return;
    }

    // 1. Obtener y validar la hora del TimePicker
    LocalTime horaSeleccionada = timePickerRecoleccion.getTime();
    if (horaSeleccionada == null) {
        JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una hora válida para recolectar su pedido.",
                "Hora no seleccionada", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // VALIDACIÓN DE RANGO: Horario escolar de 08:00 AM a 03:00 PM
    LocalTime horaMinima = LocalTime.of(8, 0);
    LocalTime horaMaxima = LocalTime.of(15, 0);

    if (horaSeleccionada.isBefore(horaMinima) || horaSeleccionada.isAfter(horaMaxima)) {
        JOptionPane.showMessageDialog(this,
                "⚠️ El horario de atención de la cafetería es de 08:00 AM a 03:00 PM.\n"
                + "Por favor, elija un horario dentro de este intervalo.",
                "Horario Fuera de Servicio", JOptionPane.WARNING_MESSAGE);
        return;
    }
   
    // 2. Extraer y fusionar indicaciones especiales sin borrar los guisados elegidos
        String notaGeneral = txtIndicacionesEspeciales.getText().trim();
        for (DetallePedido d : carritoActual.getDetalles()) {
            String indicacionPrevia = d.getIndicacionesEspeciales();
            
            if (indicacionPrevia != null && !indicacionPrevia.isEmpty()) {
                if (!notaGeneral.isEmpty()) {
                    d.setIndicacionesEspeciales(indicacionPrevia + " [Nota: " + notaGeneral + "]");
                }
            } else {
                d.setIndicacionesEspeciales(notaGeneral.isEmpty() ? null : notaGeneral);
            }
        }

    // 3. Configurar datos del pedido
    carritoActual.setUsuario(Sesion.getUsuario());
    carritoActual.setEstado("Pendiente");
    carritoActual.setHoraRecoleccionEstimada(horaSeleccionada);
    // Asegurar que el total registrado sea el que se mostró en pantalla
    carritoActual.setTotal(Double.parseDouble(lblTotalPagarValor.getText().replace("$", "").trim()));

    // 4. Enviar a base de datos
    int idGenerado = controladorPedido.registrarPedido(carritoActual);
    if (idGenerado != -1) {
        // Formatear hora para el mensaje de confirmación
        String horaFormateada = timePickerRecoleccion.getTimeStringOrEmptyString();
        
        JOptionPane.showMessageDialog(this,
                "¡Pedido enviado con éxito a la cocina!\n"
                + "Folio: PED-" + String.format("%06d", idGenerado) + "\n"
                + "Total: " + lblTotalPagarValor.getText() + "\n"
                + "Hora estimada de recolección: " + horaFormateada,
                "Pedido Registrado", JOptionPane.INFORMATION_MESSAGE);
        
        limpiarFormulario();
    } else {
        JOptionPane.showMessageDialog(this, "No se pudo registrar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnConfirmarPedidoActionPerformed

    private void btnQuitarCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarCarritoActionPerformed
        // TODO add your handling code here:
        int filaSeleccionada = tblCarrito.getSelectedRow();
    
    // 1. Validar que el usuario haya seleccionado una fila del carrito
    if (filaSeleccionada < 0) {
        JOptionPane.showMessageDialog(this, 
                "Seleccione el producto que desea retirar del carrito.", 
                "Ningún producto seleccionado", 
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2. Eliminar el elemento de la lista del pedido
    carritoActual.getDetalles().remove(filaSeleccionada);

    // 3. Recalcular subtotales, beca y actualizar la tabla automáticamente
    actualizarCarrito();
    }//GEN-LAST:event_btnQuitarCarritoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarCarrito;
    private javax.swing.JButton btnConfirmarPedido;
    private javax.swing.JButton btnQuitarCarrito;
    private javax.swing.JComboBox<Categoria> cbxCategoria;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCarrito;
    private javax.swing.JLabel lblCategoria;
    private javax.swing.JLabel lblDescuentoBecaTexto;
    private javax.swing.JLabel lblDescuentoBecaValor;
    private javax.swing.JLabel lblHoraRecoleccion;
    private javax.swing.JLabel lblIndicaciones;
    private javax.swing.JLabel lblResumenPago;
    private javax.swing.JLabel lblSubtotalTitulo;
    private javax.swing.JLabel lblSubtotalValor;
    private javax.swing.JLabel lblTituloVentana;
    private javax.swing.JLabel lblTotalPagarValor;
    private javax.swing.JPanel pnlCarrito;
    private javax.swing.JPanel pnlRealizarPedido;
    private javax.swing.JPanel pnlResumen;
    private javax.swing.JPanel pnlSeleccion;
    private javax.swing.JLabel pnlTotalPagar;
    private javax.swing.JTable tblCarrito;
    private javax.swing.JTable tblCatalogo;
    private com.github.lgooddatepicker.components.TimePicker timePickerRecoleccion;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtIndicacionesEspeciales;
    // End of variables declaration//GEN-END:variables
}
