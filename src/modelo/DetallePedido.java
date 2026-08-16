package modelo;

/**
 * Clase modelo que representa una línea de producto dentro de un pedido
 * (ej. "2 x Sándwich de Jamón y Queso, sin cebolla"). Corresponde a la
 * tabla "detalle_pedidos" de la base de datos.
 *
 * IMPORTANTE: en la base de datos de 6 tablas, "subtotal" YA NO es una
 * columna calculada automáticamente (no hay GENERATED ALWAYS AS ni
 * trigger). Es responsabilidad de la aplicación Java calcular
 * subtotal = cantidad * precioUnitario ANTES de guardar, tarea que
 * realiza PedidoDAO.registrarPedidoCompleto().
 *
 * El campo "indicacionesEspeciales" reemplaza a la antigua tabla
 * "sugerencias": en lugar de un buzón de comentarios generales, cada
 * línea del carrito puede llevar una nota específica para ese producto
 * (ej. "sin cebolla", "alérgico a la lactosa").
 * @author Equipo CaféUTM
 */
public class DetallePedido {

    private int idDetalle;
    private int idPedido;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private String indicacionesEspeciales;

    // Relación con Producto
    private Producto producto;

    public DetallePedido() {
    }

    public DetallePedido(int idDetalle, int idPedido, int cantidad,
            double precioUnitario, String indicacionesEspeciales, Producto producto) {
        this.idDetalle = idDetalle;
        this.idPedido = idPedido;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.indicacionesEspeciales = indicacionesEspeciales;
        this.producto = producto;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getIndicacionesEspeciales() {
        return indicacionesEspeciales;
    }

    public void setIndicacionesEspeciales(String indicacionesEspeciales) {
        this.indicacionesEspeciales = indicacionesEspeciales;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /**
     * Calcula el subtotal de esta línea (cantidad x precio unitario) y
     * lo guarda en el atributo "subtotal". Debe invocarse antes de
     * enviar el objeto al DAO, ya que la base de datos no lo calcula
     * automáticamente.
     */
    public void calcularSubtotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    @Override
    public String toString() {
        return cantidad + " x " + (producto != null ? producto.getNombre() : "");
    }
}
