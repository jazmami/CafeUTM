/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vista;
import controlador.ControladorPedido; 
import controlador.ControladorProducto; 
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.DetallePedido;           
import modelo.Pedido;                  
import modelo.Producto;                
import modelo.Usuario;                 
import utilidades.Sesion;               
/**
 *
 * @author dantj
 */
public class FrmGestionPedidos extends javax.swing.JInternalFrame {

    /**
     * Creates new form FrmGestionPedidos
     */
    private ControladorPedido controladorPedido = new ControladorPedido();
private ControladorProducto controladorProducto = new ControladorProducto();

// Carrito de compras del pedido que se está armando actualmente
private Pedido carritoActual = new Pedido();
private Usuario usuarioEncontrado = null;
private int idPedidoSeleccionadoEnTabla = 0;

// Franjas horarias de recolección ofrecidas por la cafetería
private static final String[] HORARIOS_DISPONIBLES = {
    "08:00", "08:15", "08:30", "08:45", "09:00", "09:15", "09:30", "09:45",
    "10:00", "10:15", "10:30", "10:45", "11:00", "11:15", "11:30", "11:45",
    "12:00", "12:15", "12:30", "12:45", "13:00", "13:15", "13:30", "13:45",
    "14:00", "14:15", "14:30", "14:45", "15:00"
};

private static final String[] ESTADOS_PEDIDO = {
    "Pendiente", "En preparación", "Listo", "Entregado", "No reclamado", "Cancelado"
};
    public FrmGestionPedidos() {
        initComponents();
    cargarProductosDisponibles();
    cargarHorarios();
    cargarEstados();
    listarPedidos();
    limpiarFormularioPedido();
    }
private void cargarProductosDisponibles() {
    cbxProducto.removeAllItems();
    List<Producto> productos = controladorProducto.listarDisponibles();
    for (Producto p : productos) {
        cbxProducto.addItem(p);
    }
}

private void cargarHorarios() {
    cbxHoraRecoleccion.removeAllItems();
    for (String horario : HORARIOS_DISPONIBLES) {
        cbxHoraRecoleccion.addItem(horario);
    }
}

private void cargarEstados() {
    cbxEstadoPedido.removeAllItems();
    for (String estado : ESTADOS_PEDIDO) {
        cbxEstadoPedido.addItem(estado);
    }
}

private void listarPedidos() {
    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("Folio");
    modelo.addColumn("Matricula");
    modelo.addColumn("Hora Recolección");
    modelo.addColumn("Estado");
    modelo.addColumn("Total");
    tblPedidos.setModel(modelo);

    List<Pedido> lista = controladorPedido.listar();
    for (Pedido p : lista) {
        Object[] fila = {
            "PED-" + String.format("%06d", p.getIdPedido()),
            p.getUsuario() != null ? p.getUsuario().getMatricula() : "",
            p.getHoraRecoleccionEstimada() != null ? p.getHoraRecoleccionEstimada().toString() : "",
            p.getEstado(),
            p.getTotal()
        };
        modelo.addRow(fila);
    }
}

private void actualizarTablaCarrito() {
    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("Producto");
    modelo.addColumn("Precio Unitario");
    modelo.addColumn("Cantidad");
    modelo.addColumn("Subtotal");
    modelo.addColumn("Indicaciones");
    tblCarrito.setModel(modelo);

    int totalArticulos = 0;
    double totalPagar = 0;

    for (DetallePedido d : carritoActual.getDetalles()) {
        d.calcularSubtotal();
        Object[] fila = {
            d.getProducto().getNombre(),
            d.getPrecioUnitario(),
            d.getCantidad(),
            d.getSubtotal(),
            d.getIndicacionesEspeciales() != null ? d.getIndicacionesEspeciales() : ""
        };
        modelo.addRow(fila);
        totalArticulos += d.getCantidad();
        totalPagar += d.getSubtotal();
    }

    lblTotalArticulosValor.setText(String.valueOf(totalArticulos));
    lblTotalPedidoValor.setText(String.format("$%.2f", totalPagar));
}

private void limpiarFormularioPedido() {
  carritoActual = new Pedido();
    usuarioEncontrado = null;
    txtBuscarMatricula.setText("");
    lblUsuarioEncontrado.setText(" (sin buscar)");
    txtNumeroPedido.setText("Nuevo pedido");
    txtFechaPedido.setText(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    
    // Validaciones para evitar que lance error si los combos están vacíos
    if (cbxEstadoPedido.getItemCount() > 0) {
        cbxEstadoPedido.setSelectedIndex(0);
    }
    if (cbxHoraRecoleccion.getItemCount() > 0) {
        cbxHoraRecoleccion.setSelectedIndex(0);
    }
    
    txtCantidad.setText("1");
    txtIndicacionesEspeciales.setText("");
    actualizarTablaCarrito();
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGestionPedidos = new javax.swing.JPanel();
        pnlEncabezado = new javax.swing.JPanel();
        lblTituloEncabezado = new javax.swing.JLabel();
        lblBuscarUsuario = new javax.swing.JLabel();
        txtBuscarMatricula = new javax.swing.JTextField();
        btnBuscarUsuario = new javax.swing.JButton();
        lblUsuarioEncontrado = new javax.swing.JLabel();
        lblHoraRecoleccion = new javax.swing.JLabel();
        cbxHoraRecoleccion = new javax.swing.JComboBox<>();
        lblEstadoPedido = new javax.swing.JLabel();
        cbxEstadoPedido = new javax.swing.JComboBox<>();
        lblNumeroPedido = new javax.swing.JLabel();
        txtNumeroPedido = new javax.swing.JTextField();
        lblFechaPedido = new javax.swing.JLabel();
        txtFechaPedido = new javax.swing.JTextField();
        lblTotalArticulos = new javax.swing.JLabel();
        lblTotalArticulosValor = new javax.swing.JLabel();
        lblTotalPedido = new javax.swing.JLabel();
        lblTotalPedidoValor = new javax.swing.JLabel();
        pnlDetalle = new javax.swing.JPanel();
        lblTituloDetalle = new javax.swing.JLabel();
        lblProducto = new javax.swing.JLabel();
        cbxProducto = new javax.swing.JComboBox<>();
        lblCantidad = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        lblIndicaciones = new javax.swing.JLabel();
        txtIndicacionesEspeciales = new javax.swing.JTextField();
        btnAgregarCarrito = new javax.swing.JButton();
        btnQuitarCarrito = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCarrito = new javax.swing.JTable();
        pnlOperaciones = new javax.swing.JPanel();
        btnProcesarPedido = new javax.swing.JButton();
        btnCambiarEstado = new javax.swing.JButton();
        btnCancelarPedido = new javax.swing.JButton();
        btnBuscarPedido = new javax.swing.JButton();
        btnNuevoPedido = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPedidos = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestión Combinada de Pedidos y Comandas");

        pnlEncabezado.setBorder(javax.swing.BorderFactory.createTitledBorder("Encabezado del Pedido (Maestro)"));
        pnlEncabezado.setToolTipText("");

        lblTituloEncabezado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloEncabezado.setText("Datos del usuario y del folio");

        lblBuscarUsuario.setText("Buscar Usuario (Matricula):");

        btnBuscarUsuario.setText("Buscar");
        btnBuscarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarUsuarioActionPerformed(evt);
            }
        });

        lblUsuarioEncontrado.setText("(sin buscar)");

        lblHoraRecoleccion.setText("Hora de Recolección:");

        lblEstadoPedido.setText("Estado del Pedido:");

        lblNumeroPedido.setText("Número de Pedido:");

        txtNumeroPedido.setEditable(false);
        txtNumeroPedido.setText("Nuevo pedido");

        lblFechaPedido.setText("Fecha del Pedido:");

        lblTotalArticulos.setText("Total de Articulos:");

        lblTotalArticulosValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotalArticulosValor.setText("0");

        lblTotalPedido.setText("Total a Pagar:");

        lblTotalPedidoValor.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalPedidoValor.setForeground(new java.awt.Color(122, 15, 42));
        lblTotalPedidoValor.setText("$0.00");

        javax.swing.GroupLayout pnlEncabezadoLayout = new javax.swing.GroupLayout(pnlEncabezado);
        pnlEncabezado.setLayout(pnlEncabezadoLayout);
        pnlEncabezadoLayout.setHorizontalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloEncabezado)
                    .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                        .addComponent(lblBuscarUsuario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(btnBuscarUsuario))
                    .addComponent(lblUsuarioEncontrado)
                    .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                        .addComponent(lblHoraRecoleccion)
                        .addGap(18, 18, 18)
                        .addComponent(cbxHoraRecoleccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                        .addComponent(lblEstadoPedido)
                        .addGap(18, 18, 18)
                        .addComponent(cbxEstadoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                        .addComponent(lblNumeroPedido)
                        .addGap(18, 18, 18)
                        .addComponent(txtNumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                        .addComponent(lblFechaPedido)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFechaPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTotalArticulos)
                    .addComponent(lblTotalArticulosValor)
                    .addComponent(lblTotalPedido)
                    .addComponent(lblTotalPedidoValor))
                .addContainerGap(136, Short.MAX_VALUE))
        );
        pnlEncabezadoLayout.setVerticalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTituloEncabezado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBuscarUsuario)
                    .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscarMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBuscarUsuario)))
                .addGap(18, 18, 18)
                .addComponent(lblUsuarioEncontrado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHoraRecoleccion)
                    .addComponent(cbxHoraRecoleccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEstadoPedido)
                    .addComponent(cbxEstadoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNumeroPedido)
                    .addComponent(txtNumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaPedido)
                    .addComponent(txtFechaPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addComponent(lblTotalArticulos)
                .addGap(18, 18, 18)
                .addComponent(lblTotalArticulosValor)
                .addGap(18, 18, 18)
                .addComponent(lblTotalPedido)
                .addGap(18, 18, 18)
                .addComponent(lblTotalPedidoValor)
                .addContainerGap(154, Short.MAX_VALUE))
        );

        pnlDetalle.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalle del Carrito / Comanda (Detalle)"));

        lblTituloDetalle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloDetalle.setText("Productos del pedido");

        lblProducto.setText("Producto:");

        lblCantidad.setText("Cantidad:");

        txtCantidad.setText("1");

        lblIndicaciones.setText("Indicaciones Especiales (ej. Sin cebolla, alérgico):");

        btnAgregarCarrito.setText("Agregar al Carrito");
        btnAgregarCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCarritoActionPerformed(evt);
            }
        });

        btnQuitarCarrito.setText("Quitar del Carrito");
        btnQuitarCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarCarritoActionPerformed(evt);
            }
        });

        tblCarrito.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Precio Unitario", "Cantidad", "Subtotal", "Indicaciones"
            }
        ));
        jScrollPane1.setViewportView(tblCarrito);

        javax.swing.GroupLayout pnlDetalleLayout = new javax.swing.GroupLayout(pnlDetalle);
        pnlDetalle.setLayout(pnlDetalleLayout);
        pnlDetalleLayout.setHorizontalGroup(
            pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDetalleLayout.createSequentialGroup()
                        .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProducto)
                            .addComponent(lblTituloDetalle)
                            .addComponent(cbxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCantidad)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(55, 55, 55))
                    .addGroup(pnlDetalleLayout.createSequentialGroup()
                        .addComponent(lblIndicaciones)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetalleLayout.createSequentialGroup()
                        .addComponent(txtIndicacionesEspeciales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnQuitarCarrito)
                            .addComponent(btnAgregarCarrito))
                        .addGap(53, 53, 53))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetalleLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        pnlDetalleLayout.setVerticalGroup(
            pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTituloDetalle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProducto)
                    .addComponent(lblCantidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblIndicaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIndicacionesEspeciales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarCarrito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnQuitarCarrito)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlOperaciones.setBorder(javax.swing.BorderFactory.createTitledBorder("Operaciones sobre el pedido"));

        btnProcesarPedido.setForeground(new java.awt.Color(122, 15, 42));
        btnProcesarPedido.setText("Procesar Pedido");
        btnProcesarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesarPedidoActionPerformed(evt);
            }
        });

        btnCambiarEstado.setForeground(new java.awt.Color(122, 15, 42));
        btnCambiarEstado.setText("Cambiar Estado");
        btnCambiarEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarEstadoActionPerformed(evt);
            }
        });

        btnCancelarPedido.setText("Cancelar Pedido");
        btnCancelarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarPedidoActionPerformed(evt);
            }
        });

        btnBuscarPedido.setText("Buscar Pedido");
        btnBuscarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPedidoActionPerformed(evt);
            }
        });

        btnNuevoPedido.setText("Nuevo Pedido");
        btnNuevoPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoPedidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOperacionesLayout = new javax.swing.GroupLayout(pnlOperaciones);
        pnlOperaciones.setLayout(pnlOperacionesLayout);
        pnlOperacionesLayout.setHorizontalGroup(
            pnlOperacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperacionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnProcesarPedido)
                .addGap(107, 107, 107)
                .addComponent(btnCambiarEstado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelarPedido)
                .addGap(118, 118, 118)
                .addComponent(btnBuscarPedido)
                .addGap(103, 103, 103)
                .addComponent(btnNuevoPedido)
                .addGap(56, 56, 56))
        );
        pnlOperacionesLayout.setVerticalGroup(
            pnlOperacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperacionesLayout.createSequentialGroup()
                .addContainerGap(41, Short.MAX_VALUE)
                .addGroup(pnlOperacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProcesarPedido)
                    .addComponent(btnCambiarEstado)
                    .addComponent(btnCancelarPedido)
                    .addComponent(btnBuscarPedido)
                    .addComponent(btnNuevoPedido))
                .addGap(36, 36, 36))
        );

        tblPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Folio", "Matricula", "Hora Recolección", "Estado", "Total"
            }
        ));
        tblPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPedidosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPedidos);

        javax.swing.GroupLayout pnlGestionPedidosLayout = new javax.swing.GroupLayout(pnlGestionPedidos);
        pnlGestionPedidos.setLayout(pnlGestionPedidosLayout);
        pnlGestionPedidosLayout.setHorizontalGroup(
            pnlGestionPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGestionPedidosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGestionPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlOperaciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlGestionPedidosLayout.createSequentialGroup()
                        .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        pnlGestionPedidosLayout.setVerticalGroup(
            pnlGestionPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGestionPedidosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGestionPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOperaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGestionPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlGestionPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarUsuarioActionPerformed
        // TODO add your handling code here:
        String matricula = txtBuscarMatricula.getText().trim();
if (matricula.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Escriba una matrícula para buscar.");
    return;
}

Usuario usuario = controladorPedido.buscarUsuarioPorMatricula(matricula);
if (usuario == null) {
    usuarioEncontrado = null;
    lblUsuarioEncontrado.setText("No se encontró ningún usuario con esa matrícula.");
    JOptionPane.showMessageDialog(this, "No existe un usuario registrado con esa matrícula.");
} else {
    usuarioEncontrado = usuario;
    lblUsuarioEncontrado.setText(usuario.getNombre() + " | Matrícula: "
            + usuario.getMatricula() + " | Rol: " + usuario.getRol().getNombreRol());
}
    }//GEN-LAST:event_btnBuscarUsuarioActionPerformed

    private void btnAgregarCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCarritoActionPerformed
        // TODO add your handling code here:
        if (cbxProducto.getSelectedItem() == null) {
    JOptionPane.showMessageDialog(this, "Debe seleccionar un producto.");
    return;
}

int cantidad;
try {
    cantidad = Integer.parseInt(txtCantidad.getText().trim());
    if (cantidad <= 0) {
        JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
        return;
    }
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero válido.");
    return;
}

Producto producto = (Producto) cbxProducto.getSelectedItem();
DetallePedido detalle = new DetallePedido();
detalle.setProducto(producto);
detalle.setCantidad(cantidad);
detalle.setPrecioUnitario(producto.getPrecio());
detalle.setIndicacionesEspeciales(txtIndicacionesEspeciales.getText().trim());

carritoActual.agregarDetalle(detalle);
actualizarTablaCarrito();
txtCantidad.setText("1");
txtIndicacionesEspeciales.setText("");
    }//GEN-LAST:event_btnAgregarCarritoActionPerformed

    private void btnQuitarCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarCarritoActionPerformed
        // TODO add your handling code here:
        int fila = tblCarrito.getSelectedRow();
if (fila < 0) {
    JOptionPane.showMessageDialog(this, "Seleccione primero un producto del carrito.");
    return;
}

carritoActual.getDetalles().remove(fila);
actualizarTablaCarrito();
    }//GEN-LAST:event_btnQuitarCarritoActionPerformed

    private void btnProcesarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesarPedidoActionPerformed
        // TODO add your handling code here:
        if (usuarioEncontrado == null) {
    JOptionPane.showMessageDialog(this, "Debe buscar y encontrar un usuario válido antes de procesar el pedido.");
    return;
}

if (carritoActual.getDetalles().isEmpty()) {
    JOptionPane.showMessageDialog(this, "Debe agregar al menos un producto al carrito.");
    return;
}

carritoActual.setUsuario(usuarioEncontrado);
carritoActual.setEstado((String) cbxEstadoPedido.getSelectedItem());
String horario = (String) cbxHoraRecoleccion.getSelectedItem();
carritoActual.setHoraRecoleccionEstimada(LocalTime.parse(horario));

if (Sesion.esAdministrador()) {
    carritoActual.setAdministrador(Sesion.getUsuario());
}

int idGenerado = controladorPedido.registrarPedido(carritoActual);
if (idGenerado != -1) {
    JOptionPane.showMessageDialog(this,
            "Pedido procesado correctamente.\nFolio: PED-" + String.format("%06d", idGenerado)
            + "\nTotal: " + lblTotalPedidoValor.getText());
    listarPedidos();
    limpiarFormularioPedido();
} else {
    JOptionPane.showMessageDialog(this, "No se pudo procesar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_btnProcesarPedidoActionPerformed

    private void btnCambiarEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarEstadoActionPerformed
        // TODO add your handling code here:
        if (idPedidoSeleccionadoEnTabla == 0) {
    JOptionPane.showMessageDialog(this, "Seleccione primero un pedido de la tabla inferior.");
    return;
}

String nuevoEstado = (String) JOptionPane.showInputDialog(this,
        "Seleccione el nuevo estado del pedido:", "Cambiar Estado",
        JOptionPane.QUESTION_MESSAGE, null, ESTADOS_PEDIDO, ESTADOS_PEDIDO[0]);

if (nuevoEstado != null) {
    boolean actualizado = controladorPedido.actualizarEstado(idPedidoSeleccionadoEnTabla, nuevoEstado);
    if (actualizado) {
        JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.");
        listarPedidos();
    } else {
        JOptionPane.showMessageDialog(this, "No se pudo actualizar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    }//GEN-LAST:event_btnCambiarEstadoActionPerformed

    private void btnCancelarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPedidoActionPerformed
        // TODO add your handling code here:
        if (idPedidoSeleccionadoEnTabla == 0) {
    JOptionPane.showMessageDialog(this, "Seleccione primero un pedido de la tabla inferior.");
    return;
}

int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Está seguro de cancelar este pedido?",
        "Confirmar cancelación", JOptionPane.YES_NO_OPTION);

if (confirmacion == JOptionPane.YES_OPTION) {
    boolean cancelado = controladorPedido.cancelar(idPedidoSeleccionadoEnTabla);
    if (cancelado) {
        JOptionPane.showMessageDialog(this, "Pedido cancelado correctamente.");
        listarPedidos();
    } else {
        JOptionPane.showMessageDialog(this, "No se pudo cancelar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    }//GEN-LAST:event_btnCancelarPedidoActionPerformed

    private void btnBuscarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarPedidoActionPerformed
        // TODO add your handling code here:
        String texto = JOptionPane.showInputDialog(this, "Escriba la matrícula del usuario a buscar:");
if (texto == null || texto.trim().isEmpty()) {
    listarPedidos();
    return;
}

Usuario usuario = controladorPedido.buscarUsuarioPorMatricula(texto.trim());
if (usuario == null) {
    JOptionPane.showMessageDialog(this, "No existe un usuario con esa matrícula.");
    return;
}

DefaultTableModel modelo = new DefaultTableModel();
modelo.addColumn("Folio");
modelo.addColumn("Matricula");
modelo.addColumn("Hora Recolección");
modelo.addColumn("Estado");
modelo.addColumn("Total");
tblPedidos.setModel(modelo);

List<Pedido> lista = controladorPedido.listarPorUsuario(usuario.getIdUsuario());
for (Pedido p : lista) {
    Object[] fila = {
        "PED-" + String.format("%06d", p.getIdPedido()),
        usuario.getMatricula(),
        p.getHoraRecoleccionEstimada() != null ? p.getHoraRecoleccionEstimada().toString() : "",
        p.getEstado(),
        p.getTotal()
    };
    modelo.addRow(fila);
}
    }//GEN-LAST:event_btnBuscarPedidoActionPerformed

    private void btnNuevoPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoPedidoActionPerformed
        // TODO add your handling code here:
        limpiarFormularioPedido();
    }//GEN-LAST:event_btnNuevoPedidoActionPerformed

    private void tblPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPedidosMouseClicked
        // TODO add your handling code here:
        int fila = tblPedidos.getSelectedRow();
if (fila >= 0) {
    String folio = tblPedidos.getValueAt(fila, 0).toString(); // Ejemplo: "PED-000123"
    idPedidoSeleccionadoEnTabla = Integer.parseInt(folio.replace("PED-", ""));
}
    }//GEN-LAST:event_tblPedidosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarCarrito;
    private javax.swing.JButton btnBuscarPedido;
    private javax.swing.JButton btnBuscarUsuario;
    private javax.swing.JButton btnCambiarEstado;
    private javax.swing.JButton btnCancelarPedido;
    private javax.swing.JButton btnNuevoPedido;
    private javax.swing.JButton btnProcesarPedido;
    private javax.swing.JButton btnQuitarCarrito;
    private javax.swing.JComboBox<String> cbxEstadoPedido;
    private javax.swing.JComboBox<String> cbxHoraRecoleccion;
    private javax.swing.JComboBox<Producto> cbxProducto;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBuscarUsuario;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblEstadoPedido;
    private javax.swing.JLabel lblFechaPedido;
    private javax.swing.JLabel lblHoraRecoleccion;
    private javax.swing.JLabel lblIndicaciones;
    private javax.swing.JLabel lblNumeroPedido;
    private javax.swing.JLabel lblProducto;
    private javax.swing.JLabel lblTituloDetalle;
    private javax.swing.JLabel lblTituloEncabezado;
    private javax.swing.JLabel lblTotalArticulos;
    private javax.swing.JLabel lblTotalArticulosValor;
    private javax.swing.JLabel lblTotalPedido;
    private javax.swing.JLabel lblTotalPedidoValor;
    private javax.swing.JLabel lblUsuarioEncontrado;
    private javax.swing.JPanel pnlDetalle;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlGestionPedidos;
    private javax.swing.JPanel pnlOperaciones;
    private javax.swing.JTable tblCarrito;
    private javax.swing.JTable tblPedidos;
    private javax.swing.JTextField txtBuscarMatricula;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtFechaPedido;
    private javax.swing.JTextField txtIndicacionesEspeciales;
    private javax.swing.JTextField txtNumeroPedido;
    // End of variables declaration//GEN-END:variables
}
