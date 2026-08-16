package modelo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase modelo que representa el encabezado de un pedido (ticket/folio).
 * Corresponde a la tabla "pedidos" de la base de datos.
 *
 * Un pedido puede contener varios productos, representados por la lista
 * de objetos DetallePedido (carrito de compras). El total se calcula en
 * la aplicación (no hay trigger en la base de datos de 6 tablas) y se
 * guarda mediante PedidoDAO.
 *
 * "usuario" es la persona (Alumno o Docente) que realiza el pedido.
 * "administrador" es, opcionalmente, el usuario con rol Administrador
 * que atendió/despachó el pedido (puede ser null mientras está
 * pendiente de atención).
 * @author Equipo CaféUTM
 */
public class Pedido {

    private int idPedido;
    private LocalDateTime horaPedido;
    private LocalTime horaRecoleccionEstimada;
    private String estado; // Pendiente, En preparación, Listo, Entregado, No reclamado, Cancelado
    private double total;

    // Relaciones
    private Usuario usuario;         // Alumno/Docente que realiza el pedido
    private Usuario administrador;   // Administrador que atendió el pedido (puede ser null)
    private List<DetallePedido> detalles;

    public Pedido() {
        this.detalles = new ArrayList<>();
    }

    public Pedido(int idPedido, LocalDateTime horaPedido, LocalTime horaRecoleccionEstimada,
            String estado, double total, Usuario usuario, Usuario administrador) {
        this.idPedido = idPedido;
        this.horaPedido = horaPedido;
        this.horaRecoleccionEstimada = horaRecoleccionEstimada;
        this.estado = estado;
        this.total = total;
        this.usuario = usuario;
        this.administrador = administrador;
        this.detalles = new ArrayList<>();
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getHoraPedido() {
        return horaPedido;
    }

    public void setHoraPedido(LocalDateTime horaPedido) {
        this.horaPedido = horaPedido;
    }

    public LocalTime getHoraRecoleccionEstimada() {
        return horaRecoleccionEstimada;
    }

    public void setHoraRecoleccionEstimada(LocalTime horaRecoleccionEstimada) {
        this.horaRecoleccionEstimada = horaRecoleccionEstimada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Usuario administrador) {
        this.administrador = administrador;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    // Método de apoyo para ir agregando productos al carrito antes de guardar
    public void agregarDetalle(DetallePedido detalle) {
        this.detalles.add(detalle);
    }

    /**
     * Recalcula el total del pedido sumando el subtotal de cada línea
     * del carrito. Debe invocarse después de modificar la lista de
     * detalles y antes de guardar/actualizar el pedido.
     */
    public void calcularTotal() {
        double suma = 0;
        for (DetallePedido d : detalles) {
            d.calcularSubtotal();
            suma += d.getSubtotal();
        }
        this.total = suma;
    }

    @Override
    public String toString() {
        return "Pedido #" + idPedido + " - " + estado;
    }
}

