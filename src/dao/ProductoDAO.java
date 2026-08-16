package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;
import modelo.Producto;

/**
 * Clase DAO encargada de realizar las operaciones CRUD sobre la tabla
 * "productos" de la base de datos (catálogo/menú de la cafetería).
 * @author Equipo CaféUTM
 */
public class ProductoDAO {

    public Producto buscarPorId(int idProducto) {
        Producto producto = null;

        String sql = """
                SELECT p.*, c.nombre_categoria
                FROM productos p
                INNER JOIN categorias c ON p.id_categoria = c.id_categoria
                WHERE p.id_producto=?
                """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                producto = mapearProducto(rs);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return producto;
    }

    public boolean registrar(Producto producto) {
        String sql = """
                INSERT INTO productos (nombre, precio, id_categoria, disponible)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getCategoria().getIdCategoria());
            ps.setBoolean(4, producto.isDisponible());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public boolean actualizar(Producto producto) {
        String sql = """
                UPDATE productos
                SET nombre=?, precio=?, id_categoria=?, disponible=?
                WHERE id_producto=?
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getCategoria().getIdCategoria());
            ps.setBoolean(4, producto.isDisponible());
            ps.setInt(5, producto.getIdProducto());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * Elimina un producto por su identificador. Si el producto ya
     * tiene líneas registradas en "detalle_pedidos", la eliminación
     * fallará (ON DELETE RESTRICT); en ese caso conviene marcarlo
     * como no disponible en lugar de eliminarlo.
     * @param idProducto Identificador del producto.
     * @return true si el registro fue eliminado.
     */
    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM productos WHERE id_producto=?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
    

    /**
     * Obtiene todos los productos registrados (para el perfil administrador).
     * @return Lista de productos.
     */
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();

        String sql = """
                SELECT p.*, c.nombre_categoria
                FROM productos p
                INNER JOIN categorias c ON p.id_categoria = c.id_categoria
                ORDER BY p.id_producto
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene únicamente los productos marcados como disponibles.
     * Se utiliza en el módulo de Pedidos, donde solo debe ofrecerse al
     * carrito lo que está disponible el día de hoy.
     * @return Lista de productos disponibles.
     */
    public List<Producto> listarDisponibles() {
        List<Producto> lista = new ArrayList<>();

        String sql = """
                SELECT p.*, c.nombre_categoria
                FROM productos p
                INNER JOIN categorias c ON p.id_categoria = c.id_categoria
                WHERE p.disponible = 1
                ORDER BY c.nombre_categoria, p.nombre
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lista;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("id_producto"));
        producto.setNombre(rs.getString("nombre"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setDisponible(rs.getBoolean("disponible"));

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNombreCategoria(rs.getString("nombre_categoria"));
        producto.setCategoria(categoria);

        return producto;
    }
    
  /**
     * Verifica si ya existe un producto con el mismo nombre (insensible a mayúsculas/minúsculas).
     * Se utiliza antes de registrar un producto nuevo.
     */
    public boolean existePorNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM productos WHERE LOWER(TRIM(nombre)) = LOWER(TRIM(?))";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar existencia de nombre de producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica si ya existe otro producto con el mismo nombre, excluyendo el ID actual.
     * Se utiliza antes de actualizar un producto existente para permitir conservar su propio nombre.
     */
    public boolean existePorNombre(String nombre, int idProductoActual) {
        String sql = "SELECT COUNT(*) FROM productos WHERE LOWER(TRIM(nombre)) = LOWER(TRIM(?)) AND id_producto != ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ps.setInt(2, idProductoActual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar nombre de producto al actualizar: " + e.getMessage());
        }
        return false;
    }  
}

