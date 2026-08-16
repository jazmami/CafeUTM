/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vista;
import controlador.ControladorPedido;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.DetallePedido;
import modelo.Pedido;
/**
 *
 * @author Aide
 */
public class FrmComandasCocina extends javax.swing.JInternalFrame {

    /**
     * Creates new form FrmComandasCocina
     */
    private ControladorPedido controlador = new ControladorPedido();
private int idPedidoSeleccionado = 0;
private static final Color VINO = new Color(122, 15, 42);
    public FrmComandasCocina() {
        initComponents();
        cargarComandasDelDia();
    }

    /**
 * Carga la tabla superior con los pedidos registrados en el día de hoy.
 */
private void cargarComandasDelDia() {
    DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    modelo.addColumn("Folio");
    modelo.addColumn("Matrícula / Usuario");
    modelo.addColumn("Hora Recolección");
    modelo.addColumn("Estado");
    modelo.addColumn("Total");
    tblComandas.setModel(modelo);

    List<Pedido> todos = controlador.listar();
    List<Pedido> deHoy = todos.stream()
            .filter(p -> p.getHoraPedido() != null
                    && p.getHoraPedido().toLocalDate().equals(LocalDate.now()))
            .collect(Collectors.toList());

    for (Pedido p : deHoy) {
        String usuarioStr = "";
        if (p.getUsuario() != null) {
            usuarioStr = (p.getUsuario().getMatricula() != null && !p.getUsuario().getMatricula().isEmpty())
                    ? p.getUsuario().getMatricula()
                    : p.getUsuario().getNombre();
        }

        Object[] fila = {
            "PED-" + String.format("%06d", p.getIdPedido()),
            usuarioStr,
            p.getHoraRecoleccionEstimada() != null ? p.getHoraRecoleccionEstimada().toString() : "",
            p.getEstado(),
            String.format("$%.2f", p.getTotal())
        };
        modelo.addRow(fila);
    }

    lblContadorPedidos.setText(deHoy.size() + " pedido(s) registrado(s) hoy");

    // Estilo visual de los encabezados y centrado
    tblComandas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    tblComandas.getTableHeader().setForeground(Color.BLACK);
    tblComandas.getTableHeader().setBackground(VINO);

    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    for (int i = 0; i < tblComandas.getColumnCount(); i++) {
        tblComandas.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    limpiarDetalle();
}

private void limpiarDetalle() {
    idPedidoSeleccionado = 0;
    txtDetalleProductos.setText("Seleccione un pedido de la tabla para ver su detalle.");
}

private void mostrarDetalle(int idPedido) {
 idPedidoSeleccionado = idPedido;

    // 1. Obtener las líneas de detalle del pedido desde la BD
    List<DetallePedido> lineas = controlador.obtenerDetallePorPedido(idPedido);
    
    // 2. Obtener el encabezado del pedido para datos generales y el monto total
    List<Pedido> todos = controlador.listar();
    Pedido pSel = null;
    for (Pedido p : todos) {
        if (p.getIdPedido() == idPedido) {
            pSel = p;
            break;
        }
    }

    // 3. Actualizar etiquetas de la tarjeta
    if (pSel != null) {
        if (lblDetalleFolio != null) {
            lblDetalleFolio.setText("Folio: #PED-" + String.format("%06d", idPedido));
        }
        if (lblDetalleMatricula != null) {
            String mat = (pSel.getUsuario() != null && pSel.getUsuario().getMatricula() != null)
                    ? pSel.getUsuario().getMatricula()
                    : (pSel.getUsuario() != null ? pSel.getUsuario().getNombre() : "N/A");
            lblDetalleMatricula.setText("Matrícula: " + mat);
        }
        if (lblDetalleHora != null) {
            String horaRec = (pSel.getHoraRecoleccionEstimada() != null)
                    ? pSel.getHoraRecoleccionEstimada().toString()
                    : "N/A";
            lblDetalleHora.setText("Hora recolección: " + horaRec);
        }
        if (lblTotalPedidoValor != null) {
            lblTotalPedidoValor.setText(String.format("$%.2f", pSel.getTotal()));
        }
    }

    // 4. Separar productos y extraer únicamente la nota en Indicaciones Especiales
    StringBuilder prodTxt = new StringBuilder();
    StringBuilder indTxt = new StringBuilder();

    for (DetallePedido d : lineas) {
        String nombreOriginal = d.getProducto().getNombre();

        // 1. Nombre limpio sin paréntesis largos
        String nombreLimpio = nombreOriginal;
        if (nombreOriginal.contains("(") && nombreOriginal.contains(")")) {
            nombreLimpio = nombreOriginal.substring(0, nombreOriginal.indexOf("(")).trim();
        }

        // 2. Línea del platillo
        prodTxt.append(" • ").append(d.getCantidad()).append("x ")
               .append(nombreLimpio)
               .append(" ($").append(String.format("%.2f", d.getSubtotal())).append(")\n");

        // 3. Procesar guisados y notas
        if (d.getIndicacionesEspeciales() != null 
                && !d.getIndicacionesEspeciales().trim().isEmpty() 
                && !"null".equalsIgnoreCase(d.getIndicacionesEspeciales().trim())) {
            
            String indCompleta = d.getIndicacionesEspeciales().trim();

           // CUADRO IZQUIERDO: Muestra únicamente los guisados sin repetir la nota
        String soloGuisados = indCompleta;
        if (indCompleta.contains("[Nota:")) {
        soloGuisados = indCompleta.substring(0, indCompleta.indexOf("[Nota:")).trim();
        }

        if (soloGuisados.startsWith("Lleva:")) {
        prodTxt.append("    ↳ ").append(soloGuisados).append("\n");
        }

            // CUADRO DERECHO: Extrae SOLAMENTE la nota escrita por el usuario
            String soloNota = "";
            if (indCompleta.contains("[Nota:") && indCompleta.contains("]")) {
                int inicio = indCompleta.indexOf("[Nota:") + 6;
                int fin = indCompleta.lastIndexOf("]");
                soloNota = indCompleta.substring(inicio, fin).trim();
            } else if (!indCompleta.startsWith("Lleva:")) {
                // Para productos individuales que no son paquetes (ej. "sin hielo", "extra salsa")
                soloNota = indCompleta;
            }

            // Si hay una nota real, agregarla a Indicaciones Especiales
            if (!soloNota.isEmpty() && !indTxt.toString().contains(soloNota)) {
                if (indTxt.length() > 0) {
                    indTxt.append("\n");
                }
                indTxt.append("• ").append(soloNota);
            }
        }
    }

    // 5. Asignar texto al cuadro de productos (txtDetalleProductos o txtDetalle)
    if (txtDetalleProductos != null) {
        txtDetalleProductos.setText(prodTxt.length() > 0 ? prodTxt.toString() : "Sin productos.");
    } else if (txtDetalleProductos != null) {
        txtDetalleProductos.setText(prodTxt.length() > 0 ? prodTxt.toString() : "Sin productos.");
    }

    // 6. Asignar texto al cuadro de Indicaciones Especiales (txtIndicacionesEspeciales)
    if (txtIndicacionesEspeciales != null) {
        if (indTxt.length() > 0) {
            txtIndicacionesEspeciales.setText(indTxt.toString());
            txtIndicacionesEspeciales.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
            txtIndicacionesEspeciales.setForeground(new java.awt.Color(40, 40, 40));
        } else {
            txtIndicacionesEspeciales.setText("Sin indicaciones especiales.");
            txtIndicacionesEspeciales.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 12));
            txtIndicacionesEspeciales.setForeground(new java.awt.Color(120, 120, 120));
        }
    }
}

private int obtenerIdDesdeFolio(String folio) {
    return Integer.parseInt(folio.replace("PED-", ""));
}

/**
 * Valida las transiciones de estado del pedido seleccionado antes de actualizar la BD.
 * Evita cancelaciones dobles o modificar pedidos ya concluidos/cancelados por el usuario.
 */
private void cambiarEstado(String nuevoEstado) {
    if (idPedidoSeleccionado == 0) {
        JOptionPane.showMessageDialog(this, 
                "Seleccione primero un pedido de la tabla superior.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 1. Obtener el estado actual del pedido seleccionado directamente de la tabla
    int fila = tblComandas.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, 
                "Por favor, seleccione un pedido de la lista.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String estadoActual = tblComandas.getValueAt(fila, 3).toString().trim();

    // 2. VALIDACIÓN 1: El pedido ya fue cancelado previamente por el alumno o el admin
    if ("Cancelado".equalsIgnoreCase(estadoActual)) {
        JOptionPane.showMessageDialog(this,
                "⚠️ Este pedido ya se encuentra CANCELADO por el usuario/alumno.\nNo se pueden realizar más acciones ni volver a cancelarlo.",
                "Pedido ya Cancelado", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 3. VALIDACIÓN 2: El pedido ya fue entregado
    if ("Entregado".equalsIgnoreCase(estadoActual)) {
        JOptionPane.showMessageDialog(this,
                "Este pedido ya fue ENTREGADO al usuario. Su ciclo ha finalizado.",
                "Pedido Entregado", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // 4. VALIDACIÓN 3: El pedido ya está en el estado al que se intenta cambiar
    if (nuevoEstado.equalsIgnoreCase(estadoActual)) {
        JOptionPane.showMessageDialog(this,
                "El pedido ya se encuentra en estado: \"" + estadoActual + "\".",
                "Estado Actual", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // 5. Confirmación si el administrador decide cancelarlo
    if ("Cancelado".equalsIgnoreCase(nuevoEstado)) {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cancelar el pedido #PED-" + String.format("%06d", idPedidoSeleccionado) + "?",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
    }

    // 6. Actualización en la base de datos
    boolean actualizado = controlador.actualizarEstado(idPedidoSeleccionado, nuevoEstado);
    if (actualizado) {
        JOptionPane.showMessageDialog(this, 
                "El estado del pedido #PED-" + String.format("%06d", idPedidoSeleccionado) 
                + " cambió a: \"" + nuevoEstado + "\".", 
                "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
        cargarComandasDelDia();
    } else {
        JOptionPane.showMessageDialog(this, 
                "No se pudo actualizar el estado del pedido.",
                "Error", JOptionPane.ERROR_MESSAGE);
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

        pnlPrincipal = new javax.swing.JPanel();
        pnlBanner = new javax.swing.JPanel();
        lblLogoBanner = new javax.swing.JLabel();
        lblTituloBanner = new javax.swing.JLabel();
        lblSubtituloBanner = new javax.swing.JLabel();
        lblUsuarioBanner = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        pnlTablaCard = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblComandas = new javax.swing.JTable();
        btnActualizarTop = new javax.swing.JButton();
        lblTitulo = new javax.swing.JLabel();
        lblContadorPedidos = new javax.swing.JLabel();
        pnlDetalleContenedor = new javax.swing.JPanel();
        pnlMosaicoBotones = new javax.swing.JPanel();
        btnEntregado = new javax.swing.JButton();
        btnEnPreparacion = new javax.swing.JButton();
        btnListoRecoger = new javax.swing.JButton();
        btnCancelarPedido = new javax.swing.JButton();
        pnlDetalleProductosCard = new javax.swing.JPanel();
        lblDetalleFolio = new javax.swing.JLabel();
        lblDetalleMatricula = new javax.swing.JLabel();
        lblDetalleHora = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDetalleProductos = new javax.swing.JTextArea();
        lblTotalPedidoTitulo = new javax.swing.JLabel();
        lblTotalPedidoValor = new javax.swing.JLabel();
        pnlIndicacionesCard = new javax.swing.JPanel();
        lblIndicacionesTitulo = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtIndicacionesEspeciales = new javax.swing.JTextArea();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Comandas de Cocina");

        pnlBanner.setBackground(new java.awt.Color(122, 15, 42));
        pnlBanner.setForeground(new java.awt.Color(255, 255, 255));
        pnlBanner.setPreferredSize(new java.awt.Dimension(1000, 60));

        lblLogoBanner.setBackground(new java.awt.Color(204, 204, 204));
        lblLogoBanner.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblLogoBanner.setForeground(new java.awt.Color(255, 255, 255));
        lblLogoBanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconopedidos.png"))); // NOI18N
        lblLogoBanner.setText("CAFETERÍA UNIVERSITARIA");
        lblLogoBanner.setIconTextGap(12);

        lblTituloBanner.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTituloBanner.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloBanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/sombrerochef.png"))); // NOI18N
        lblTituloBanner.setText("Gestión de Comandas de Cocina - CaféUTM");
        lblTituloBanner.setIconTextGap(8);

        lblSubtituloBanner.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblSubtituloBanner.setForeground(new java.awt.Color(255, 255, 255));
        lblSubtituloBanner.setText("Cocina | Panel de Control");

        lblUsuarioBanner.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblUsuarioBanner.setForeground(new java.awt.Color(255, 255, 255));
        lblUsuarioBanner.setText("Administrador Cocina");

        javax.swing.GroupLayout pnlBannerLayout = new javax.swing.GroupLayout(pnlBanner);
        pnlBanner.setLayout(pnlBannerLayout);
        pnlBannerLayout.setHorizontalGroup(
            pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBannerLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblLogoBanner)
                .addGroup(pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBannerLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTituloBanner))
                    .addGroup(pnlBannerLayout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(lblSubtituloBanner)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblUsuarioBanner)
                .addContainerGap())
        );
        pnlBannerLayout.setVerticalGroup(
            pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBannerLayout.createSequentialGroup()
                .addGroup(pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlBannerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTituloBanner)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSubtituloBanner)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblLogoBanner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBannerLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblUsuarioBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        pnlTablaCard.setBackground(new java.awt.Color(255, 255, 255));
        pnlTablaCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 224, 233)));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(850, 220));

        tblComandas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Folio", "Matricula/Usuario", "Hora Recolección", "Estado", "Total"
            }
        ));
        tblComandas.setGridColor(new java.awt.Color(230, 230, 230));
        tblComandas.setRowHeight(30);
        tblComandas.setShowHorizontalLines(true);
        tblComandas.setShowVerticalLines(true);
        tblComandas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblComandasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblComandas);

        btnActualizarTop.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizarTop.setForeground(new java.awt.Color(122, 15, 42));
        btnActualizarTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/actualizar.png"))); // NOI18N
        btnActualizarTop.setText("Actualizar Lista");
        btnActualizarTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarTopActionPerformed(evt);
            }
        });

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(122, 15, 42));
        lblTitulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/listapedidos.png"))); // NOI18N
        lblTitulo.setText("Pedidos del Día");
        lblTitulo.setIconTextGap(8);

        lblContadorPedidos.setForeground(new java.awt.Color(120, 120, 120));
        lblContadorPedidos.setText("0 pedido(s) registrados hoy");

        javax.swing.GroupLayout pnlTablaCardLayout = new javax.swing.GroupLayout(pnlTablaCard);
        pnlTablaCard.setLayout(pnlTablaCardLayout);
        pnlTablaCardLayout.setHorizontalGroup(
            pnlTablaCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaCardLayout.createSequentialGroup()
                .addGroup(pnlTablaCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTablaCardLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTablaCardLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitulo)
                        .addGap(18, 18, 18)
                        .addComponent(lblContadorPedidos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnActualizarTop)))
                .addContainerGap())
        );
        pnlTablaCardLayout.setVerticalGroup(
            pnlTablaCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTablaCardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTablaCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActualizarTop)
                    .addComponent(lblTitulo)
                    .addComponent(lblContadorPedidos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        pnlDetalleContenedor.setLayout(new java.awt.GridLayout(1, 2));

        pnlMosaicoBotones.setLayout(new java.awt.GridLayout(1, 4));

        btnEntregado.setBackground(new java.awt.Color(102, 102, 102));
        btnEntregado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEntregado.setForeground(new java.awt.Color(255, 255, 255));
        btnEntregado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/entregado.png"))); // NOI18N
        btnEntregado.setText("Entregado");
        btnEntregado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEntregado.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnEntregado.setIconTextGap(8);
        btnEntregado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntregadoActionPerformed(evt);
            }
        });

        btnEnPreparacion.setBackground(new java.awt.Color(51, 102, 255));
        btnEnPreparacion.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnEnPreparacion.setForeground(new java.awt.Color(255, 255, 255));
        btnEnPreparacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/preparacion.png"))); // NOI18N
        btnEnPreparacion.setText("En Preparación");
        btnEnPreparacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEnPreparacion.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnEnPreparacion.setIconTextGap(8);
        btnEnPreparacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnPreparacionActionPerformed(evt);
            }
        });

        btnListoRecoger.setBackground(new java.awt.Color(0, 153, 0));
        btnListoRecoger.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnListoRecoger.setForeground(new java.awt.Color(255, 255, 255));
        btnListoRecoger.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/completado.png"))); // NOI18N
        btnListoRecoger.setText("Listo para Recoger");
        btnListoRecoger.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnListoRecoger.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnListoRecoger.setIconTextGap(8);
        btnListoRecoger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListoRecogerActionPerformed(evt);
            }
        });

        btnCancelarPedido.setBackground(new java.awt.Color(255, 153, 153));
        btnCancelarPedido.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelarPedido.setForeground(new java.awt.Color(192, 0, 0));
        btnCancelarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancelar.png"))); // NOI18N
        btnCancelarPedido.setText("Cancelar Pedido");
        btnCancelarPedido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelarPedido.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCancelarPedido.setIconTextGap(8);
        btnCancelarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarPedidoActionPerformed(evt);
            }
        });

        pnlDetalleProductosCard.setBackground(new java.awt.Color(255, 255, 255));
        pnlDetalleProductosCard.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalle del Pedido Seleccionado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(122, 15, 42))); // NOI18N
        pnlDetalleProductosCard.setPreferredSize(new java.awt.Dimension(850, 140));

        lblDetalleFolio.setText("Folio: ");

        lblDetalleMatricula.setText("Matricula: ");

        lblDetalleHora.setText("Hora de recoleccion: ");

        txtDetalleProductos.setEditable(false);
        txtDetalleProductos.setColumns(20);
        txtDetalleProductos.setLineWrap(true);
        txtDetalleProductos.setRows(5);
        txtDetalleProductos.setText("Seleccione un pedido de la tabla para ver su detalle.");
        txtDetalleProductos.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtDetalleProductos);

        lblTotalPedidoTitulo.setText("Total del Pedido:");

        lblTotalPedidoValor.setForeground(new java.awt.Color(88, 17, 36));
        lblTotalPedidoValor.setText("$0.00");

        pnlIndicacionesCard.setBackground(new java.awt.Color(250, 251, 240));
        pnlIndicacionesCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(245, 230, 200)));

        lblIndicacionesTitulo.setText("Indicaciones Especiales");

        txtIndicacionesEspeciales.setBackground(new java.awt.Color(255, 251, 240));
        txtIndicacionesEspeciales.setColumns(20);
        txtIndicacionesEspeciales.setRows(5);
        jScrollPane3.setViewportView(txtIndicacionesEspeciales);

        javax.swing.GroupLayout pnlIndicacionesCardLayout = new javax.swing.GroupLayout(pnlIndicacionesCard);
        pnlIndicacionesCard.setLayout(pnlIndicacionesCardLayout);
        pnlIndicacionesCardLayout.setHorizontalGroup(
            pnlIndicacionesCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIndicacionesCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblIndicacionesTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlIndicacionesCardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlIndicacionesCardLayout.setVerticalGroup(
            pnlIndicacionesCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIndicacionesCardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblIndicacionesTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlDetalleProductosCardLayout = new javax.swing.GroupLayout(pnlDetalleProductosCard);
        pnlDetalleProductosCard.setLayout(pnlDetalleProductosCardLayout);
        pnlDetalleProductosCardLayout.setHorizontalGroup(
            pnlDetalleProductosCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleProductosCardLayout.createSequentialGroup()
                .addGroup(pnlDetalleProductosCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDetalleProductosCardLayout.createSequentialGroup()
                        .addGap(243, 243, 243)
                        .addComponent(lblTotalPedidoTitulo)
                        .addGap(18, 18, 18)
                        .addComponent(lblTotalPedidoValor))
                    .addGroup(pnlDetalleProductosCardLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblDetalleFolio)
                        .addGap(102, 102, 102)
                        .addComponent(lblDetalleMatricula)
                        .addGap(96, 96, 96)
                        .addComponent(lblDetalleHora))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlIndicacionesCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlDetalleProductosCardLayout.setVerticalGroup(
            pnlDetalleProductosCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleProductosCardLayout.createSequentialGroup()
                .addGroup(pnlDetalleProductosCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDetalleProductosCardLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlDetalleProductosCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDetalleFolio)
                            .addComponent(lblDetalleMatricula)
                            .addComponent(lblDetalleHora))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetalleProductosCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTotalPedidoTitulo)
                            .addComponent(lblTotalPedidoValor)))
                    .addComponent(pnlIndicacionesCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlBanner, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPrincipalLayout.createSequentialGroup()
                                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pnlDetalleProductosCard, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
                                    .addComponent(pnlTablaCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlDetalleContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(pnlMosaicoBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(btnEnPreparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnListoRecoger, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnEntregado, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnCancelarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 29, Short.MAX_VALUE))
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addComponent(pnlBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addGap(238, 238, 238)
                        .addComponent(pnlDetalleContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlTablaCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlDetalleProductosCard, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEnPreparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListoRecoger, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEntregado, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlMosaicoBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnPreparacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnPreparacionActionPerformed
        // TODO add your handling code here:
        cambiarEstado("En preparación");
    }//GEN-LAST:event_btnEnPreparacionActionPerformed

    private void tblComandasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblComandasMouseClicked
        // TODO add your handling code here:
int fila = tblComandas.getSelectedRow();
    if (fila >= 0) {
        String folio = tblComandas.getValueAt(fila, 0).toString(); // Ejemplo: "PED-000001"
        int idPedido = Integer.parseInt(folio.replace("PED-", "").replace("#", ""));
        mostrarDetalle(idPedido);
    }
    }//GEN-LAST:event_tblComandasMouseClicked

    private void btnListoRecogerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListoRecogerActionPerformed
        // TODO add your handling code here:
        cambiarEstado("Listo");
    }//GEN-LAST:event_btnListoRecogerActionPerformed

    private void btnEntregadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntregadoActionPerformed
        // TODO add your handling code here:
        cambiarEstado("Entregado");
    }//GEN-LAST:event_btnEntregadoActionPerformed

    private void btnCancelarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPedidoActionPerformed
        // TODO add your handling code here:
        cambiarEstado("Cancelado");
    }//GEN-LAST:event_btnCancelarPedidoActionPerformed

    private void btnActualizarTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarTopActionPerformed
        // TODO add your handling code here:
        cargarComandasDelDia();
    }//GEN-LAST:event_btnActualizarTopActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarTop;
    private javax.swing.JButton btnCancelarPedido;
    private javax.swing.JButton btnEnPreparacion;
    private javax.swing.JButton btnEntregado;
    private javax.swing.JButton btnListoRecoger;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblContadorPedidos;
    private javax.swing.JLabel lblDetalleFolio;
    private javax.swing.JLabel lblDetalleHora;
    private javax.swing.JLabel lblDetalleMatricula;
    private javax.swing.JLabel lblIndicacionesTitulo;
    private javax.swing.JLabel lblLogoBanner;
    private javax.swing.JLabel lblSubtituloBanner;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTituloBanner;
    private javax.swing.JLabel lblTotalPedidoTitulo;
    private javax.swing.JLabel lblTotalPedidoValor;
    private javax.swing.JLabel lblUsuarioBanner;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JPanel pnlDetalleContenedor;
    private javax.swing.JPanel pnlDetalleProductosCard;
    private javax.swing.JPanel pnlIndicacionesCard;
    private javax.swing.JPanel pnlMosaicoBotones;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlTablaCard;
    private javax.swing.JTable tblComandas;
    private javax.swing.JTextArea txtDetalleProductos;
    private javax.swing.JTextArea txtIndicacionesEspeciales;
    // End of variables declaration//GEN-END:variables
}
