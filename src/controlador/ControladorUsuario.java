package controlador;

import dao.RolDAO;
import dao.UsuarioDAO;
import java.util.List;
import modelo.Rol;
import modelo.Usuario;

/**
 * Clase controlador que gestiona las operaciones CRUD de usuarios,
 * sirviendo de intermediario entre FrmGestionUsuarios y las clases DAO.
 * @author Equipo CaféUTM
 */
public class ControladorUsuario {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private RolDAO rolDAO = new RolDAO();

    public boolean registrar(Usuario usuario) {
        return usuarioDAO.registrar(usuario) != -1;
    }

    public boolean actualizar(Usuario usuario) {
        return usuarioDAO.actualizar(usuario);
    }

    public boolean eliminar(int idUsuario) {
        return usuarioDAO.eliminar(idUsuario);
    }

    public Usuario buscarPorId(int idUsuario) {
        return usuarioDAO.buscarPorId(idUsuario);
    }

    public List<Usuario> listar() {
        return usuarioDAO.listar();
    }

    /**
     * Obtiene el catálogo de roles disponibles, utilizado para llenar
     * el JComboBox del formulario de gestión de usuarios.
     */
    public List<Rol> listarRoles() {
        return rolDAO.listar();
    }
}
