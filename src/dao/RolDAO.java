package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Rol;

/**
 * Clase DAO encargada de realizar las operaciones CRUD sobre la tabla
 * "roles" de la base de datos.
 * @author Equipo CaféUTM
 */
public class RolDAO {

    public Rol buscarPorId(int idRol) {
        Rol rol = null;
        String sql = "SELECT * FROM roles WHERE id_rol=?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return rol;
    }

    /**
     * Obtiene todos los roles registrados en el sistema. Se utiliza
     * para llenar el JComboBox del formulario de gestión de usuarios.
     * @return Lista de roles.
     */
    public List<Rol> listar() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY id_rol";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                lista.add(rol);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lista;
    }
}
