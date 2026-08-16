package controlador;

import dao.PedidoDAO;
import dao.UsuarioDAO;
import java.util.List;
import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Usuario;

/**
 * Clase controlador que gestiona el módulo combinado maestro-detalle
 * de Pedidos y Comandas, sirviendo de intermediario entre
 * FrmGestionPedidos y las clases PedidoDAO / UsuarioDAO — de forma
 * análoga a como ControladorRolPermiso combina RolDAO, PermisoDAO y
 * RolPermisoDAO en el proyecto VeterinariaPets.
 * @author Equipo CaféUTM
 */
public class ControladorPedido {

    private PedidoDAO pedidoDAO = new PedidoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Registra un pedido completo (encabezado + carrito de productos)
     * en una sola transacción.
     * @param pedido Pedido con su Usuario y su lista de DetallePedido cargada.
     * @return El id_pedido generado si el registro fue exitoso; -1 si falló.
     */
    public int registrarPedido(Pedido pedido) {
        return pedidoDAO.registrarPedidoCompleto(pedido);
    }

    public boolean actualizarEstado(int idPedido, String nuevoEstado) {
        return pedidoDAO.actualizarEstado(idPedido, nuevoEstado);
    }

    public boolean asignarAdministrador(int idPedido, int idAdministrador) {
        return pedidoDAO.asignarAdministrador(idPedido, idAdministrador);
    }

    public boolean cancelar(int idPedido) {
        return pedidoDAO.cancelar(idPedido);
    }

    public Pedido buscarPorId(int idPedido) {
        return pedidoDAO.buscarPorId(idPedido);
    }

    public List<Pedido> listar() {
        return pedidoDAO.listar();
    }

    public List<Pedido> listarPorUsuario(int idUsuario) {
        return pedidoDAO.listarPorUsuario(idUsuario);
    }

    public List<Pedido> listarPorEstado(String estado) {
        return pedidoDAO.listarPorEstado(estado);
    }

    public List<DetallePedido> obtenerDetallePorPedido(int idPedido) {
        return pedidoDAO.obtenerDetallePorPedido(idPedido);
    }

    /**
     * Obtiene la demanda acumulada por producto, utilizada en el
     * reporte de demanda diaria.
     */
    public List<Object[]> obtenerDemandaAcumulada() {
        return pedidoDAO.obtenerDemandaAcumulada();
    }

    /**
     * Busca un usuario (Alumno, Docente o Administrador) por su
     * matrícula. Se utiliza en FrmGestionPedidos para localizar al
     * usuario que realiza el pedido antes de armar el carrito.
     * @param matricula Matrícula a buscar.
     * @return Objeto Usuario si existe; de lo contrario null.
     */
    public Usuario buscarUsuarioPorMatricula(String matricula) {
        return usuarioDAO.buscarPorMatricula(matricula);
    }
}
