package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import utilidades.Sesion;

/**
 * Clase controlador encargada de gestionar el proceso de autenticación
 * del sistema. Actúa como intermediario entre FrmLogin y UsuarioDAO.
 *
 * Se simplificó respecto a la versión anterior: como la base de datos
 * de 6 tablas fusionó "usuarios" y "clientes" en una sola tabla, ya no
 * es necesario consultar un EmpleadoDAO o ClienteDAO por separado
 * después del login — el objeto Usuario que regresa UsuarioDAO ya trae
 * toda la información de la persona.
 * @author Equipo CaféUTM
 */
public class ControladorLogin {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Valida las credenciales capturadas en el login. Si son
     * correctas, guarda al usuario en la clase Sesion para que esté
     * disponible en el resto de la aplicación.
     * @param nombreUsuario Usuario o matrícula capturados.
     * @param contrasena Contraseña capturada.
     * @return true si el acceso fue concedido.
     */
    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        Usuario usuario = usuarioDAO.iniciarSesion(nombreUsuario, contrasena);

        if (usuario == null) {
            return false;
        }

        Sesion.setUsuario(usuario);
        usuarioDAO.actualizarUltimoAcceso(usuario.getIdUsuario());

        return true;
    }
}

