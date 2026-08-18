package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;
import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Producto;
import modelo.Usuario;

/**
 * Clase DAO encargada de realizar las operaciones CRUD sobre las tablas "pedidos"
 * y "detalle_pedidos" de la base de datos de CaféUTM.
 * @author Equipo CaféUTM
 */
public class PedidoDAO {

    /**
     * Registra un pedido completo en la base de datos con su lista de detalles (carrito)
     * dentro de una transacción SQL (commit/rollback).
     */
    public int registrarPedidoCompleto(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedidos (id_usuario, hora_pedido, hora_recoleccion_estimada, estado, total) VALUES (?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio_unitario, subtotal, indicaciones_especiales) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;
        ResultSet rsKeys = null;
        int idGenerado = -1;

        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false); // Iniciar transacción SQL

            // 1. Insertar el encabezado del pedido
            psPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setInt(1, pedido.getUsuario().getIdUsuario());

            if (pedido.getHoraPedido() != null) {
                psPedido.setTimestamp(2, Timestamp.valueOf(pedido.getHoraPedido()));
            } else {
                psPedido.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            }

            if (pedido.getHoraRecoleccionEstimada() != null) {
                psPedido.setString(3, pedido.getHoraRecoleccionEstimada().toString());
            } else {
                psPedido.setNull(3, java.sql.Types.VARCHAR);
            }

            psPedido.setString(4, pedido.getEstado());

            // Solo calcula el total si viene en 0; si ya trae el descuento calculado, respeta ese valor
            if (pedido.getTotal() <= 0) {
            pedido.calcularTotal();
            }

psPedido.setDouble(5, pedido.getTotal());

            int filasAfectadas = psPedido.executeUpdate();
            if (filasAfectadas > 0) {
                rsKeys = psPedido.getGeneratedKeys();
                if (rsKeys.next()) {
                    idGenerado = rsKeys.getInt(1);
                    pedido.setIdPedido(idGenerado);
                }
            }

            if (idGenerado == -1) {
                conn.rollback();
                return -1;
            }

            // 2. Insertar las líneas de detalle del carrito
            psDetalle = conn.prepareStatement(sqlDetalle);
            for (DetallePedido d : pedido.getDetalles()) {
                psDetalle.setInt(1, idGenerado);
                psDetalle.setInt(2, d.getProducto().getIdProducto());
                psDetalle.setInt(3, d.getCantidad());
                psDetalle.setDouble(4, d.getPrecioUnitario());
                psDetalle.setDouble(5, d.getSubtotal());
                psDetalle.setString(6, d.getIndicacionesEspeciales());
                psDetalle.addBatch();
            }

            psDetalle.executeBatch();
            conn.commit(); // Confirmar la transacción

        } catch (SQLException e) {
            System.out.println("Error al registrar pedido completo: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error en rollback: " + ex.getMessage());
                }
            }
            return -1;
        } finally {
            try {
                if (rsKeys != null) rsKeys.close();
                if (psPedido != null) psPedido.close();
                if (psDetalle != null) psDetalle.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error al cerrar conexiones: " + ex.getMessage());
            }
        }

        return idGenerado;
    }

    public int registrar(Pedido pedido) {
        return registrarPedidoCompleto(pedido);
    }

    /**
     * Busca un pedido específico por su ID.
     */
    public Pedido buscarPorId(int idPedido) {
        Pedido pedido = null;
        String sql = """
            SELECT p.*, u.nombre AS nombre_usuario, u.matricula
            FROM pedidos p
            INNER JOIN usuarios u ON p.id_usuario = u.id_usuario
            WHERE p.id_pedido = ?
        """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedido = new Pedido();
                    pedido.setIdPedido(rs.getInt("id_pedido"));

                    Timestamp ts = rs.getTimestamp("hora_pedido");
                    if (ts != null) {
                        pedido.setHoraPedido(ts.toLocalDateTime());
                    }

                    String horaRec = rs.getString("hora_recoleccion_estimada");
                    if (horaRec != null && !horaRec.isEmpty()) {
                        try {
                            pedido.setHoraRecoleccionEstimada(java.time.LocalTime.parse(horaRec));
                        } catch (Exception e) {}
                    }

                    pedido.setEstado(rs.getString("estado"));
                    pedido.setTotal(rs.getDouble("total"));

                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre_usuario"));
                    u.setMatricula(rs.getString("matricula"));
                    pedido.setUsuario(u);

                    pedido.setDetalles(obtenerDetallePorPedido(idPedido));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar pedido por ID: " + e.getMessage());
        }
        return pedido;
    }

    /**
     * Obtiene todos los pedidos registrados en el sistema.
     */
    public List<Pedido> listar() {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, u.nombre AS nombre_usuario, u.matricula
            FROM pedidos p
            INNER JOIN usuarios u ON p.id_usuario = u.id_usuario
            ORDER BY p.id_pedido DESC
        """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("id_pedido"));

                Timestamp ts = rs.getTimestamp("hora_pedido");
                if (ts != null) {
                    pedido.setHoraPedido(ts.toLocalDateTime());
                }

                String horaRec = rs.getString("hora_recoleccion_estimada");
                if (horaRec != null && !horaRec.isEmpty()) {
                    try {
                        pedido.setHoraRecoleccionEstimada(java.time.LocalTime.parse(horaRec));
                    } catch (Exception e) {}
                }

                pedido.setEstado(rs.getString("estado"));
                pedido.setTotal(rs.getDouble("total"));

                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre_usuario"));
                u.setMatricula(rs.getString("matricula"));
                pedido.setUsuario(u);

                lista.add(pedido);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene la lista de pedidos de un usuario específico.
     */
    public List<Pedido> listarPorUsuario(int idUsuario) {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, u.nombre AS nombre_usuario, u.matricula
            FROM pedidos p
            INNER JOIN usuarios u ON p.id_usuario = u.id_usuario
            WHERE p.id_usuario = ?
            ORDER BY p.id_pedido DESC
        """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setIdPedido(rs.getInt("id_pedido"));

                    Timestamp ts = rs.getTimestamp("hora_pedido");
                    if (ts != null) {
                        pedido.setHoraPedido(ts.toLocalDateTime());
                    }

                    String horaRec = rs.getString("hora_recoleccion_estimada");
                    if (horaRec != null && !horaRec.isEmpty()) {
                        try {
                            pedido.setHoraRecoleccionEstimada(java.time.LocalTime.parse(horaRec));
                        } catch (Exception e) {}
                    }

                    pedido.setEstado(rs.getString("estado"));
                    pedido.setTotal(rs.getDouble("total"));

                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre_usuario"));
                    u.setMatricula(rs.getString("matricula"));
                    pedido.setUsuario(u);

                    lista.add(pedido);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pedidos por usuario: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene pedidos filtrados por estado.
     */
    public List<Pedido> listarPorEstado(String estado) {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, u.nombre AS nombre_usuario, u.matricula
            FROM pedidos p
            INNER JOIN usuarios u ON p.id_usuario = u.id_usuario
            WHERE p.estado = ?
            ORDER BY p.id_pedido DESC
        """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setIdPedido(rs.getInt("id_pedido"));

                    Timestamp ts = rs.getTimestamp("hora_pedido");
                    if (ts != null) {
                        pedido.setHoraPedido(ts.toLocalDateTime());
                    }

                    String horaRec = rs.getString("hora_recoleccion_estimada");
                    if (horaRec != null && !horaRec.isEmpty()) {
                        try {
                            pedido.setHoraRecoleccionEstimada(java.time.LocalTime.parse(horaRec));
                        } catch (Exception e) {}
                    }

                    pedido.setEstado(rs.getString("estado"));
                    pedido.setTotal(rs.getDouble("total"));

                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre_usuario"));
                    u.setMatricula(rs.getString("matricula"));
                    pedido.setUsuario(u);

                    lista.add(pedido);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pedidos por estado: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene los productos correspondientes a un pedido.
     */
    public List<DetallePedido> obtenerDetallePorPedido(int idPedido) {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = """
            SELECT dp.*, p.nombre, p.precio, p.disponible, c.id_categoria, c.nombre_categoria
            FROM detalle_pedidos dp
            INNER JOIN productos p ON dp.id_producto = p.id_producto
            INNER JOIN categorias c ON p.id_categoria = c.id_categoria
            WHERE dp.id_pedido = ?
        """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setIdDetalle(rs.getInt("id_detalle"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    detalle.setIndicacionesEspeciales(rs.getString("indicaciones_especiales"));

                    detalle.setSubtotal(rs.getDouble("subtotal"));
        
                    // Si en la base de datos viniera en 0, calcularlo por respaldo:
                    if (detalle.getSubtotal() <= 0) {
                     detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
                    }

                    detalle.setIndicacionesEspeciales(rs.getString("indicaciones_especiales"));
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setDisponible(rs.getBoolean("disponible"));

                    Categoria cat = new Categoria();
                    cat.setIdCategoria(rs.getInt("id_categoria"));
                    cat.setNombreCategoria(rs.getString("nombre_categoria"));
                    p.setCategoria(cat);

                    detalle.setProducto(p);
                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener detalle de pedido: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Asigna un administrador responsable del pedido.
     */
    public boolean asignarAdministrador(int idPedido, int idAdministrador) {
        String sql = "UPDATE pedidos SET id_administrador = ? WHERE id_pedido = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAdministrador);
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al asignar administrador: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza el estado de un pedido.
     */
    public boolean actualizarEstado(int idPedido, String nuevoEstado) {
        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del pedido: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cancela un pedido.
     */
    public boolean cancelar(int idPedido) {
        return actualizarEstado(idPedido, "Cancelado");
    }

  /**
     * Obtiene la demanda acumulada por producto distinguiendo las ventas de hoy
     * y el total histórico, sincronizado con la fecha local de la aplicación.
     */
    public List<Object[]> obtenerDemandaAcumulada() {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT 
                pr.nombre,
                COALESCE(SUM(CASE WHEN DATE(p.hora_pedido) = ? OR DATE(p.hora_pedido) = CURDATE() THEN dp.cantidad ELSE 0 END), 0) AS vendidos_hoy,
                SUM(dp.cantidad) AS total_historico,
                SUM(dp.subtotal) AS total_recaudado
            FROM detalle_pedidos dp
            INNER JOIN productos pr ON dp.id_producto = pr.id_producto
            INNER JOIN pedidos p ON dp.id_pedido = p.id_pedido
            WHERE p.estado != 'Cancelado'
            GROUP BY pr.id_producto, pr.nombre
            ORDER BY total_historico DESC
        """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Pasa la fecha actual de la computadora (ej. "2026-08-18")
            ps.setString(1, java.time.LocalDate.now().toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                        rs.getString("nombre"),
                        rs.getInt("vendidos_hoy"),       // Columna 1: Ventas de Hoy
                        rs.getInt("total_historico"),    // Columna 2: Total Acumulado
                        rs.getDouble("total_recaudado")  // Columna 3: Monto Recaudado ($)
                    };
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener demanda acumulada: " + e.getMessage());
        }
        return lista;
    }
}