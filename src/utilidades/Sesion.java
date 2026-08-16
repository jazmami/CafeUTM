package utilidades;

import modelo.Usuario;

/**
 * Clase que administra la sesión del usuario autenticado en el sistema.
 * Permite acceder desde cualquier ventana al Usuario activo (que ya
 * incluye tanto sus datos de acceso como su matrícula, nombre, rol,
 * etc., dado que la base de datos de 6 tablas fusionó "usuarios" y
 * "clientes" en una sola tabla).
 * @author Equipo CaféUTM
 */
public class Sesion {

    // Usuario que tiene la sesión iniciada
    private static Usuario usuario;

    /**
     * @return Usuario autenticado actualmente, o null si no hay sesión activa.
     */
    public static Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario autenticado al iniciar sesión.
     * @param usuario Usuario que inició sesión.
     */
    public static void setUsuario(Usuario usuario) {
        Sesion.usuario = usuario;
    }

    /**
     * Indica si el usuario autenticado tiene rol Administrador.
     * @return true si el rol del usuario en sesión es "Administrador".
     */
    public static boolean esAdministrador() {
        return usuario != null && usuario.getRol() != null
                && "Administrador".equals(usuario.getRol().getNombreRol());
    }

    /**
     * Cierra la sesión activa, limpiando los datos almacenados.
     */
    public static void cerrarSesion() {
        usuario = null;
    }
}
