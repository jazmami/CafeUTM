/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vista;
import controlador.ControladorPedido;
import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Usuario;
import utilidades.Sesion;
/**
 * Formulario: FrmMisPedidos
 * Permite al alumno/docente consultar sus pedidos activos, el historial anterior,
 * y ver el estado en tiempo real.
 * @author Dante Gonzalez, Eder Vyera, Emiliano Asdrubal, Sergio Ortiz
 */
public class FrmMisPedidos extends javax.swing.JInternalFrame {

    /**
     * Creates new form FrmMisPedidos
     */
    private ControladorPedido controladorPedido = new ControladorPedido();
private int idPedidoSeleccionado = 0;
    public FrmMisPedidos() {
        initComponents();
        mostrarDatosUsuario();
        listarMisPedidos();
    }
    
    /**
     * Muestra el nombre y la matrícula del usuario con sesión activa en el banner.
     */
    private void mostrarDatosUsuario() {
        Usuario user = Sesion.getUsuario();
        if (user != null) {
            lblBienvenida.setText("Bienvenido, " + user.getNombre());
            lblMatriculaUsuario.setText("Matrícula: " + (user.getMatricula() != null ? user.getMatricula() : "N/A"));
        } else {
            lblBienvenida.setText("Bienvenido, Usuario");
            lblMatriculaUsuario.setText("Matrícula: N/A");
        }
    }
    
    /**
     * Carga y llena la tabla del historial de pedidos anteriores.
     */
private void listarMisPedidos() {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        modelo.addColumn("Folio");
        modelo.addColumn("Fecha / Hora");
        modelo.addColumn("Hora Recolección");
        modelo.addColumn("Estado");
        modelo.addColumn("Total ($)");
        tblMisPedidos.setModel(modelo);

        if (Sesion.getUsuario() == null) {
            return;
        }

        List<Pedido> lista = controladorPedido.listarPorUsuario(Sesion.getUsuario().getIdUsuario());
        for (Pedido p : lista) {
            String horaEncargo = (p.getHoraPedido() != null)
                    ? p.getHoraPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))
                    : "";
            String horaRecoleccion = (p.getHoraRecoleccionEstimada() != null)
                    ? p.getHoraRecoleccionEstimada().toString()
                    : "";

            Object[] fila = {
                "PED-" + String.format("%06d", p.getIdPedido()),
                horaEncargo,
                horaRecoleccion,
                p.getEstado(),
                String.format("%.2f", p.getTotal())
            };
            modelo.addRow(fila);
        }

        // Estilos visuales de la cabecera
        tblMisPedidos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblMisPedidos.getTableHeader().setForeground(new Color(122, 15, 42));
        tblMisPedidos.getTableHeader().setBackground(Color.WHITE);

        // Formato y color según el Estado del pedido en la tabla
        tblMisPedidos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 0 || column == 2 || column == 3 || column == 4) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (column == 3 && value != null) {
                    String estado = value.toString();
                    if ("Pendiente".equals(estado)) {
                        setForeground(new Color(204, 102, 0)); // Naranja
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("En preparación".equals(estado)) {
                        setForeground(new Color(0, 102, 204)); // Azul
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Listo".equals(estado) || "Entregado".equals(estado)) {
                        setForeground(new Color(0, 128, 0)); // Verde
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Cancelado".equals(estado) || "No reclamado".equals(estado)) {
                        setForeground(new Color(192, 0, 0)); // Rojo
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                } else if (!isSelected) {
                    setForeground(Color.BLACK);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                return c;
            }
        });

        // Carga automáticamente el pedido activo más reciente en la tarjeta superior
        cargarPedidoActivo(lista);
    }

/**
     * Busca en la lista de pedidos del usuario aquel que esté en curso ("Pendiente", "En preparación", "Listo").
     * Si lo encuentra, despliega su estado y detalles en tiempo real en la tarjeta pnlPedidoActivoCard.
     */
    private void cargarPedidoActivo(List<Pedido> lista) {
        Pedido activo = null;

        // Buscar el primer pedido activo
        for (Pedido p : lista) {
            String e = p.getEstado();
            if ("Pendiente".equals(e) || "En preparación".equals(e) || "Listo".equals(e)) {
                activo = p;
                break;
            }
        }

        // Si no hay pedidos en estado activo, muestra el último registrado
        if (activo == null && !lista.isEmpty()) {
            activo = lista.get(0);
        }

        if (activo != null) {
            mostrarDetallePedidoActivo(activo);
        } else {
            limpiarPedidoActivo();
        }
    }
    
  /**
 * Despliega la información detallada del pedido activo y actualiza la barra de estado e indicaciones.
 */
private void mostrarDetallePedidoActivo(Pedido p) {
    idPedidoSeleccionado = p.getIdPedido();
    lblFolioActivo.setText("#PED-" + String.format("%06d", p.getIdPedido()));

    String horaEncargoStr = (p.getHoraPedido() != null)
            ? p.getHoraPedido().format(DateTimeFormatter.ofPattern("hh:mm a"))
            : "N/A";
    lblHoraEncargo.setText("Hora encargo: " + horaEncargoStr);

    String horaRecoleccionStr = (p.getHoraRecoleccionEstimada() != null)
            ? p.getHoraRecoleccionEstimada().toString()
            : "N/A";
    lblHoraRecoleccion.setText("Recolección: " + horaRecoleccionStr + " [" + p.getEstado() + "]");

    // 1. Actualizar la línea de progreso visual
    actualizarLineaEstado(p.getEstado());

    // 2. Cargar los productos e indicaciones especiales del pedido con formato limpio
    List<DetallePedido> detalles = controladorPedido.obtenerDetallePorPedido(p.getIdPedido());
    StringBuilder sbProductos = new StringBuilder();
    StringBuilder sbIndicaciones = new StringBuilder();

    for (DetallePedido d : detalles) {
        String nombreOriginal = d.getProducto().getNombre();

        // 1. Quitar el paréntesis largo de opciones del menú (ej. "Desayuno del Día")
        String nombreLimpio = nombreOriginal;
        if (nombreOriginal.contains("(") && nombreOriginal.contains(")")) {
            nombreLimpio = nombreOriginal.substring(0, nombreOriginal.indexOf("(")).trim();
        }

        // 2. Línea principal del platillo
        sbProductos.append(" • ").append(d.getCantidad()).append("x ")
                   .append(nombreLimpio)
                   .append(" ($").append(String.format("%.2f", d.getSubtotal())).append(")\n");

        // 3. Procesar guisados y notas por separado
        if (d.getIndicacionesEspeciales() != null
                && !d.getIndicacionesEspeciales().trim().isEmpty()
                && !"null".equalsIgnoreCase(d.getIndicacionesEspeciales().trim())) {

            String indCompleta = d.getIndicacionesEspeciales().trim();

            // CUADRO IZQUIERDO: Muestra únicamente los guisados y la bebida
            String soloGuisados = indCompleta;
            if (indCompleta.contains("[Nota:")) {
                soloGuisados = indCompleta.substring(0, indCompleta.indexOf("[Nota:")).trim();
            }
            if (soloGuisados.startsWith("Lleva:")) {
                sbProductos.append("    ↳ ").append(soloGuisados).append("\n");
            }

            // CUADRO DERECHO: Extrae exclusivamente la nota del usuario
            String soloNota = "";
            if (indCompleta.contains("[Nota:") && indCompleta.contains("]")) {
                int inicio = indCompleta.indexOf("[Nota:") + 6;
                int fin = indCompleta.lastIndexOf("]");
                soloNota = indCompleta.substring(inicio, fin).trim();
            } else if (!indCompleta.startsWith("Lleva:")) {
                soloNota = indCompleta;
            }

            if (!soloNota.isEmpty() && !sbIndicaciones.toString().contains(soloNota)) {
                if (sbIndicaciones.length() > 0) {
                    sbIndicaciones.append("\n");
                }
                sbIndicaciones.append("• ").append(soloNota);
            }
        }
    }

    // 3. Asignar los textos limpios a las áreas de texto
    txtProductosArea.setText(sbProductos.length() > 0 ? sbProductos.toString() : "Sin productos registrados.");

    if (sbIndicaciones.length() > 0) {
        txtIndicacionesActivo.setText(sbIndicaciones.toString());
        txtIndicacionesActivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtIndicacionesActivo.setForeground(new Color(40, 40, 40));
    } else {
        txtIndicacionesActivo.setText("Sin indicaciones especiales.");
        txtIndicacionesActivo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        txtIndicacionesActivo.setForeground(new Color(120, 120, 120));
    }
}
    
    /**
     * Limpia los componentes en caso de que el usuario no tenga pedidos.
     */
    private void limpiarPedidoActivo() {
        idPedidoSeleccionado = 0;
        lblFolioActivo.setText("#PED-000000");
        lblHoraEncargo.setText("Hora encargo: -");
        lblHoraRecoleccion.setText("Hora recolección: -");
        txtProductosArea.setText("No tienes ningún pedido activo registrado.");
        txtIndicacionesActivo.setText("Sin indicaciones.");
    }
    
    /**
 * Actualiza dinámicamente los colores de la línea de tiempo según el estado del pedido activo.
 */
private void actualizarLineaEstado(String estado) {
    Color colorInactivo = new Color(160, 160, 160);
    Color colorNaranja = new Color(217, 119, 6);   // Pendiente
    Color colorAzul = new Color(37, 99, 235);      // En preparación
    Color colorVerde = new Color(22, 163, 74);     // Listo / Entregado
    Color colorRojo = new Color(185, 28, 28);      // Cancelado

    if ("Pendiente".equals(estado)) {
        lblPaso1.setForeground(colorNaranja);
        lblPaso2.setForeground(colorInactivo);
        lblPaso3.setForeground(colorInactivo);
        lblLinea1.setForeground(colorInactivo);
        lblLinea2.setForeground(colorInactivo);
    } else if ("En preparación".equals(estado)) {
        lblPaso1.setForeground(colorNaranja);
        lblPaso2.setForeground(colorAzul);
        lblPaso3.setForeground(colorInactivo);
        lblLinea1.setForeground(colorAzul);
        lblLinea2.setForeground(colorInactivo);
    } else if ("Listo".equals(estado) || "Entregado".equals(estado)) {
        lblPaso1.setForeground(colorNaranja);
        lblPaso2.setForeground(colorAzul);
        lblPaso3.setForeground(colorVerde);
        lblLinea1.setForeground(colorAzul);
        lblLinea2.setForeground(colorVerde);
    } else if ("Cancelado".equals(estado)) {
        lblPaso1.setForeground(colorRojo);
        lblPaso1.setText("❶ Cancelado");
        lblPaso2.setForeground(colorInactivo);
        lblPaso3.setForeground(colorInactivo);
        lblLinea1.setForeground(colorInactivo);
        lblLinea2.setForeground(colorInactivo);
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMisPedidos = new javax.swing.JPanel();
        pnlBanner = new javax.swing.JPanel();
        lblLogoBanner = new javax.swing.JLabel();
        lblBienvenida = new javax.swing.JLabel();
        lblMatriculaUsuario = new javax.swing.JLabel();
        lblUsuarioIcono = new javax.swing.JLabel();
        pnlPedidoActivoCard = new javax.swing.JPanel();
        lblTituloPedidoActivo = new javax.swing.JLabel();
        pnlInfoActivo = new javax.swing.JPanel();
        lblFolioActivo = new javax.swing.JLabel();
        lblHoraEncargo = new javax.swing.JLabel();
        lblHoraRecoleccion = new javax.swing.JLabel();
        lblNota = new javax.swing.JLabel();
        pnlLineaEstado = new javax.swing.JPanel();
        lblPaso1 = new javax.swing.JLabel();
        lblLinea1 = new javax.swing.JLabel();
        lblPaso2 = new javax.swing.JLabel();
        lblLinea2 = new javax.swing.JLabel();
        lblPaso3 = new javax.swing.JLabel();
        pnlDetalleActivo = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtProductosArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtIndicacionesActivo = new javax.swing.JTextArea();
        pnlBotonesAccion = new javax.swing.JPanel();
        btnCancelarPedido = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        pnlHistorialCard = new javax.swing.JPanel();
        lblTituloHistorial = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMisPedidos = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Mis Pedidos");

        pnlMisPedidos.setBackground(new java.awt.Color(234, 239, 245));

        pnlBanner.setBackground(new java.awt.Color(122, 15, 42));
        pnlBanner.setPreferredSize(new java.awt.Dimension(900, 60));

        lblLogoBanner.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblLogoBanner.setForeground(new java.awt.Color(255, 255, 255));
        lblLogoBanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconopedidos.png"))); // NOI18N
        lblLogoBanner.setText("CAFETERÍA UNIVERSITARIA");
        lblLogoBanner.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblLogoBanner.setIconTextGap(12);

        lblBienvenida.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblBienvenida.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenida.setText("Bienvenido, [Nombre]");

        lblMatriculaUsuario.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblMatriculaUsuario.setForeground(new java.awt.Color(230, 210, 215));
        lblMatriculaUsuario.setText("Matrícula: [A00000]");

        lblUsuarioIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/usuario2.png"))); // NOI18N
        lblUsuarioIcono.setIconTextGap(8);

        javax.swing.GroupLayout pnlBannerLayout = new javax.swing.GroupLayout(pnlBanner);
        pnlBanner.setLayout(pnlBannerLayout);
        pnlBannerLayout.setHorizontalGroup(
            pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBannerLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(lblLogoBanner)
                .addGap(50, 50, 50)
                .addComponent(lblUsuarioIcono, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBienvenida)
                    .addComponent(lblMatriculaUsuario))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlBannerLayout.setVerticalGroup(
            pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblLogoBanner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlBannerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBienvenida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMatriculaUsuario)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlBannerLayout.createSequentialGroup()
                .addComponent(lblUsuarioIcono, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlPedidoActivoCard.setBackground(new java.awt.Color(255, 255, 255));
        pnlPedidoActivoCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 224, 233)));
        pnlPedidoActivoCard.setPreferredSize(new java.awt.Dimension(880, 240));

        lblTituloPedidoActivo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTituloPedidoActivo.setForeground(new java.awt.Color(88, 17, 36));
        lblTituloPedidoActivo.setText("Pedido Activo del Día");

        pnlInfoActivo.setBackground(new java.awt.Color(255, 255, 255));
        pnlInfoActivo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lblFolioActivo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblFolioActivo.setForeground(new java.awt.Color(88, 17, 36));
        lblFolioActivo.setText("#PED-1042");

        lblHoraEncargo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblHoraEncargo.setForeground(new java.awt.Color(80, 80, 80));
        lblHoraEncargo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/hora.png"))); // NOI18N
        lblHoraEncargo.setText("Hora encargo:");
        lblHoraEncargo.setIconTextGap(8);

        lblHoraRecoleccion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblHoraRecoleccion.setForeground(new java.awt.Color(80, 80, 80));
        lblHoraRecoleccion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/bolsa.png"))); // NOI18N
        lblHoraRecoleccion.setText("Hora recoleccion:");
        lblHoraRecoleccion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblHoraRecoleccion.setIconTextGap(8);

        javax.swing.GroupLayout pnlInfoActivoLayout = new javax.swing.GroupLayout(pnlInfoActivo);
        pnlInfoActivo.setLayout(pnlInfoActivoLayout);
        pnlInfoActivoLayout.setHorizontalGroup(
            pnlInfoActivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoActivoLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblFolioActivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHoraEncargo)
                .addGap(220, 220, 220)
                .addComponent(lblHoraRecoleccion)
                .addGap(30, 30, 30))
        );
        pnlInfoActivoLayout.setVerticalGroup(
            pnlInfoActivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoActivoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlInfoActivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFolioActivo)
                    .addComponent(lblHoraEncargo)
                    .addComponent(lblHoraRecoleccion))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblNota.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblNota.setForeground(new java.awt.Color(100, 100, 100));
        lblNota.setText("* Nota: Solo se pueden cancelar pedidos que se encuentren en estado \"Pendiente\".");

        pnlLineaEstado.setBackground(new java.awt.Color(255, 255, 255));
        pnlLineaEstado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lblPaso1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPaso1.setText("Pendiente");

        lblLinea1.setText("─────────");

        lblPaso2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPaso2.setText("En Preparacion");

        lblLinea2.setText("─────────");

        lblPaso3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPaso3.setText("Listo para Recoger");

        javax.swing.GroupLayout pnlLineaEstadoLayout = new javax.swing.GroupLayout(pnlLineaEstado);
        pnlLineaEstado.setLayout(pnlLineaEstadoLayout);
        pnlLineaEstadoLayout.setHorizontalGroup(
            pnlLineaEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLineaEstadoLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(lblPaso1)
                .addGap(92, 92, 92)
                .addComponent(lblLinea1)
                .addGap(92, 92, 92)
                .addComponent(lblPaso2)
                .addGap(92, 92, 92)
                .addComponent(lblLinea2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPaso3)
                .addGap(60, 60, 60))
        );
        pnlLineaEstadoLayout.setVerticalGroup(
            pnlLineaEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLineaEstadoLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlLineaEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPaso1)
                    .addComponent(lblLinea1)
                    .addComponent(lblPaso2)
                    .addComponent(lblLinea2)
                    .addComponent(lblPaso3))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pnlDetalleActivo.setBackground(new java.awt.Color(255, 255, 255));
        pnlDetalleActivo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        txtProductosArea.setColumns(20);
        txtProductosArea.setRows(5);
        jScrollPane2.setViewportView(txtProductosArea);

        txtIndicacionesActivo.setColumns(20);
        txtIndicacionesActivo.setRows(5);
        jScrollPane3.setViewportView(txtIndicacionesActivo);

        javax.swing.GroupLayout pnlDetalleActivoLayout = new javax.swing.GroupLayout(pnlDetalleActivo);
        pnlDetalleActivo.setLayout(pnlDetalleActivoLayout);
        pnlDetalleActivoLayout.setHorizontalGroup(
            pnlDetalleActivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleActivoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlDetalleActivoLayout.setVerticalGroup(
            pnlDetalleActivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetalleActivoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDetalleActivoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout pnlPedidoActivoCardLayout = new javax.swing.GroupLayout(pnlPedidoActivoCard);
        pnlPedidoActivoCard.setLayout(pnlPedidoActivoCardLayout);
        pnlPedidoActivoCardLayout.setHorizontalGroup(
            pnlPedidoActivoCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPedidoActivoCardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPedidoActivoCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlInfoActivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlPedidoActivoCardLayout.createSequentialGroup()
                        .addGroup(pnlPedidoActivoCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNota)
                            .addGroup(pnlPedidoActivoCardLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(lblTituloPedidoActivo)))
                        .addGap(500, 500, 500))
                    .addComponent(pnlLineaEstado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDetalleActivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPedidoActivoCardLayout.setVerticalGroup(
            pnlPedidoActivoCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPedidoActivoCardLayout.createSequentialGroup()
                .addComponent(lblTituloPedidoActivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlInfoActivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLineaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDetalleActivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNota)
                .addContainerGap())
        );

        pnlBotonesAccion.setBackground(new java.awt.Color(255, 255, 255));

        btnCancelarPedido.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelarPedido.setForeground(new java.awt.Color(192, 0, 0));
        btnCancelarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancelar.png"))); // NOI18N
        btnCancelarPedido.setText("Cancelar Pedido Seleccionado");
        btnCancelarPedido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelarPedido.setIconTextGap(8);
        btnCancelarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarPedidoActionPerformed(evt);
            }
        });

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(122, 15, 42));
        btnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/actualizar.png"))); // NOI18N
        btnActualizar.setText("Actualizar Historial");
        btnActualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnActualizar.setIconTextGap(8);
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBotonesAccionLayout = new javax.swing.GroupLayout(pnlBotonesAccion);
        pnlBotonesAccion.setLayout(pnlBotonesAccionLayout);
        pnlBotonesAccionLayout.setHorizontalGroup(
            pnlBotonesAccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotonesAccionLayout.createSequentialGroup()
                .addGap(154, 154, 154)
                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelarPedido)
                .addGap(154, 154, 154))
        );
        pnlBotonesAccionLayout.setVerticalGroup(
            pnlBotonesAccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotonesAccionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBotonesAccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlHistorialCard.setBackground(new java.awt.Color(255, 255, 255));
        pnlHistorialCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 224, 233)));
        pnlHistorialCard.setPreferredSize(new java.awt.Dimension(880, 260));

        lblTituloHistorial.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTituloHistorial.setForeground(new java.awt.Color(88, 17, 36));
        lblTituloHistorial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/historial.png"))); // NOI18N
        lblTituloHistorial.setText("Historial de Pedidos Anteriores");
        lblTituloHistorial.setIconTextGap(8);

        tblMisPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Folio", "Fecha / Hora", "Total ($)", "Estado"
            }
        ));
        tblMisPedidos.setGridColor(new java.awt.Color(230, 230, 230));
        tblMisPedidos.setPreferredSize(new java.awt.Dimension(850, 190));
        tblMisPedidos.setRowHeight(30);
        tblMisPedidos.setShowHorizontalLines(true);
        tblMisPedidos.setShowVerticalLines(true);
        tblMisPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMisPedidosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMisPedidos);

        javax.swing.GroupLayout pnlHistorialCardLayout = new javax.swing.GroupLayout(pnlHistorialCard);
        pnlHistorialCard.setLayout(pnlHistorialCardLayout);
        pnlHistorialCardLayout.setHorizontalGroup(
            pnlHistorialCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHistorialCardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHistorialCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(pnlHistorialCardLayout.createSequentialGroup()
                        .addComponent(lblTituloHistorial)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlHistorialCardLayout.setVerticalGroup(
            pnlHistorialCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHistorialCardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTituloHistorial)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlMisPedidosLayout = new javax.swing.GroupLayout(pnlMisPedidos);
        pnlMisPedidos.setLayout(pnlMisPedidosLayout);
        pnlMisPedidosLayout.setHorizontalGroup(
            pnlMisPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBanner, javax.swing.GroupLayout.DEFAULT_SIZE, 989, Short.MAX_VALUE)
            .addGroup(pnlMisPedidosLayout.createSequentialGroup()
                .addGroup(pnlMisPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlPedidoActivoCard, javax.swing.GroupLayout.DEFAULT_SIZE, 983, Short.MAX_VALUE)
                    .addComponent(pnlHistorialCard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 983, Short.MAX_VALUE)
                    .addGroup(pnlMisPedidosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlBotonesAccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlMisPedidosLayout.setVerticalGroup(
            pnlMisPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMisPedidosLayout.createSequentialGroup()
                .addComponent(pnlBanner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(pnlPedidoActivoCard, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBotonesAccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlHistorialCard, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMisPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMisPedidos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblMisPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMisPedidosMouseClicked
        // TODO add your handling code here:
    int fila = tblMisPedidos.getSelectedRow();
        if (fila >= 0) {
            String folio = tblMisPedidos.getValueAt(fila, 0).toString(); // Ejemplo: "PED-000123"
            idPedidoSeleccionado = Integer.parseInt(folio.replace("PED-", ""));
            
            // Buscar el objeto Pedido completo para mostrarlo en la tarjeta activa/superior
            List<Pedido> lista = controladorPedido.listarPorUsuario(Sesion.getUsuario().getIdUsuario());
            for (Pedido p : lista) {
                if (p.getIdPedido() == idPedidoSeleccionado) {
                    mostrarDetallePedidoActivo(p);
                    break;
                }
            }
        }
    }//GEN-LAST:event_tblMisPedidosMouseClicked

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        // TODO add your handling code here:
    listarMisPedidos();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnCancelarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPedidoActionPerformed
        // TODO add your handling code here:
    if (idPedidoSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un pedido del historial o de la tarjeta superior.");
            return;
        }

        int fila = tblMisPedidos.getSelectedRow();
        if (fila >= 0) {
            String estado = tblMisPedidos.getValueAt(fila, 3).toString();
            if (!"Pendiente".equals(estado)) {
                JOptionPane.showMessageDialog(this,
                        "Solo se pueden cancelar pedidos que se encuentren en estado 'Pendiente'.",
                        "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cancelar este pedido?",
                "Confirmar cancelación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean cancelado = controladorPedido.cancelar(idPedidoSeleccionado);
            if (cancelado) {
                JOptionPane.showMessageDialog(this, "Pedido cancelado correctamente.");
                listarMisPedidos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cancelar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnCancelarPedidoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnCancelarPedido;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblFolioActivo;
    private javax.swing.JLabel lblHoraEncargo;
    private javax.swing.JLabel lblHoraRecoleccion;
    private javax.swing.JLabel lblLinea1;
    private javax.swing.JLabel lblLinea2;
    private javax.swing.JLabel lblLogoBanner;
    private javax.swing.JLabel lblMatriculaUsuario;
    private javax.swing.JLabel lblNota;
    private javax.swing.JLabel lblPaso1;
    private javax.swing.JLabel lblPaso2;
    private javax.swing.JLabel lblPaso3;
    private javax.swing.JLabel lblTituloHistorial;
    private javax.swing.JLabel lblTituloPedidoActivo;
    private javax.swing.JLabel lblUsuarioIcono;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JPanel pnlBotonesAccion;
    private javax.swing.JPanel pnlDetalleActivo;
    private javax.swing.JPanel pnlHistorialCard;
    private javax.swing.JPanel pnlInfoActivo;
    private javax.swing.JPanel pnlLineaEstado;
    private javax.swing.JPanel pnlMisPedidos;
    private javax.swing.JPanel pnlPedidoActivoCard;
    private javax.swing.JTable tblMisPedidos;
    private javax.swing.JTextArea txtIndicacionesActivo;
    private javax.swing.JTextArea txtProductosArea;
    // End of variables declaration//GEN-END:variables
}
