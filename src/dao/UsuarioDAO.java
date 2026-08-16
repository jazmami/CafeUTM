package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import modelo.Rol;
import modelo.Usuario;

/**
 * Clase DAO encargada de realizar las operaciones CRUD y el proceso de
 * autenticación sobre la tabla "usuarios" de la base de datos.
 *
 * Esta tabla fusiona lo que en el diseño anterior eran dos tablas
 * separadas ("usuarios" para login y "clientes" para los datos del
 * alumno/docente). Por eso cada consulta trae en un solo SELECT tanto
 * los datos de acceso como los datos de la persona.
 * @author Equipo CaféUTM
 */
public class UsuarioDAO {

    /**
     * Valida las credenciales de acceso contra la base de datos. Solo
     * se permite el acceso si el usuario existe, la contraseña
     * coincide y la cuenta está activa.
     * @param nombreUsuario Nombre de usuario capturado en el login.
     * @param contrasena Contraseña capturada en el login.
     * @return Objeto Usuario con su Rol asociado si las credenciales
     * son correctas; null en caso contrario.
     */
    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {
        Usuario usuario = null;

        String sql = """
                SELECT u.*, r.nombre_rol, r.descripcion
                FROM usuarios u
                INNER JOIN roles r ON u.id_rol = r.id_rol
                WHERE u.nombre_usuario = ? AND u.contrasena = ? AND u.activo = 1
                """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearUsuario(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }

        return usuario;
    }

    /**
     * Registra un nuevo usuario (Administrador, Alumno o Docente) en
     * la base de datos.
     * @param usuario Objeto con la información completa del usuario.
     * @return El id_usuario generado si el registro fue exitoso; -1 si falló.
     */
    public int registrar(Usuario usuario) {
        String sql = """
                INSERT INTO usuarios
                    (matricula, nombre_usuario, contrasena, nombre, id_rol,
                     tipo_usuario, becado, porcentaje_beca, activo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getMatricula());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, usuario.getContrasena());
            ps.setString(4, usuario.getNombre());
            ps.setInt(5, usuario.getRol().getIdRol());
            ps.setString(6, usuario.getTipoUsuario());
            ps.setBoolean(7, usuario.isBecado());
            ps.setDouble(8, usuario.getPorcentajeBeca());
            ps.setBoolean(9, usuario.isActivo());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rsKeys = ps.getGeneratedKeys();
                if (rsKeys.next()) {
                    return rsKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1;
    }

    /**
     * Actualiza la información completa de un usuario, incluyendo su
     * contraseña. Para no sobrescribir la contraseña existente cuando
     * el formulario de edición se deja en blanco, la Vista es
     * responsable de recuperar la contraseña actual antes de llamar a
     * este método si el campo de captura viene vacío.
     * @param usuario Usuario con los datos actualizados.
     * @return true si la actualización fue exitosa.
     */
    public boolean actualizar(Usuario usuario) {
        String sql = """
                UPDATE usuarios
                SET matricula=?, nombre_usuario=?, contrasena=?, nombre=?,
                    id_rol=?, tipo_usuario=?, becado=?, porcentaje_beca=?, activo=?
                WHERE id_usuario=?
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getMatricula());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, usuario.getContrasena());
            ps.setString(4, usuario.getNombre());
            ps.setInt(5, usuario.getRol().getIdRol());
            ps.setString(6, usuario.getTipoUsuario());
            ps.setBoolean(7, usuario.isBecado());
            ps.setDouble(8, usuario.getPorcentajeBeca());
            ps.setBoolean(9, usuario.isActivo());
            ps.setInt(10, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * Elimina un usuario por su identificador. Al eliminarlo se
     * eliminan en cascada (ON DELETE CASCADE) todos sus pedidos.
     * @param idUsuario Identificador del usuario.
     * @return true si el registro fue eliminado.
     */
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario=?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * Actualiza la fecha y hora del último acceso de un usuario.
     * @param idUsuario Identificador del usuario.
     * @return true si la actualización fue exitosa.
     */
    public boolean actualizarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE usuarios SET ultimo_acceso = NOW() WHERE id_usuario = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * Busca un usuario por su identificador numérico.
     * @param idUsuario Identificador del usuario.
     * @return Objeto Usuario si existe; de lo contrario null.
     */
    public Usuario buscarPorId(int idUsuario) {
        Usuario usuario = null;
        String sql = """
                SELECT u.*, r.nombre_rol, r.descripcion
                FROM usuarios u
                INNER JOIN roles r ON u.id_rol = r.id_rol
                WHERE u.id_usuario = ?
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearUsuario(rs);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return usuario;
    }

    /**
     * Busca un usuario por su matrícula. Se utiliza en el módulo de
     * Pedidos, donde el operador escribe la matrícula del alumno o
     * docente para iniciar un nuevo pedido.
     * @param matricula Matrícula a buscar.
     * @return Objeto Usuario si existe; de lo contrario null.
     */
    public Usuario buscarPorMatricula(String matricula) {
        Usuario usuario = null;
        String sql = """
                SELECT u.*, r.nombre_rol, r.descripcion
                FROM usuarios u
                INNER JOIN roles r ON u.id_rol = r.id_rol
                WHERE u.matricula = ?
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, matricula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearUsuario(rs);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return usuario;
    }

    /**
     * Obtiene todos los usuarios registrados junto con su rol.
     * @return Lista de usuarios.
     */
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();

        String sql = """
                SELECT u.*, r.nombre_rol, r.descripcion
                FROM usuarios u
                INNER JOIN roles r ON u.id_rol = r.id_rol
                ORDER BY u.id_usuario
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lista;
    }

    /**
     * Método de apoyo que construye un objeto Usuario a partir de una
     * fila del ResultSet, evitando repetir el mismo mapeo en varios
     * métodos de la clase.
     * @param rs Fila actual del ResultSet posicionada con rs.next().
     * @return Objeto Usuario con la información de la fila.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setMatricula(rs.getString("matricula"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
        usuario.setBecado(rs.getBoolean("becado"));
        usuario.setPorcentajeBeca(rs.getDouble("porcentaje_beca"));
        usuario.setActivo(rs.getBoolean("activo"));

        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombreRol(rs.getString("nombre_rol"));
        rol.setDescripcion(rs.getString("descripcion"));
        usuario.setRol(rol);

        return usuario;
    }
}
