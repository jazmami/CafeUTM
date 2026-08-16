package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;

/**
 * Clase DAO encargada de realizar las operaciones CRUD sobre la tabla
 * "categorias" de la base de datos (Desayunos, Comidas, Bebidas, Antojitos).
 * @author Equipo CaféUTM
 */
public class CategoriaDAO {

    public Categoria buscarPorId(int idCategoria) {
        Categoria categoria = null;
        String sql = "SELECT * FROM categorias WHERE id_categoria=?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                categoria.setNombreCategoria(rs.getString("nombre_categoria"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categoria;
    }

    /**
     * Obtiene todas las categorías registradas. Se utiliza para llenar
     * el JComboBox del formulario de gestión de productos.
     * @return Lista de categorías.
     */
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nombre_categoria";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                categoria.setNombreCategoria(rs.getString("nombre_categoria"));
                lista.add(categoria);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lista;
    }
}
