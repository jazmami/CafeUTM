package modelo;

/**
 * Clase modelo que representa un platillo o bebida del menú de la
 * cafetería. Corresponde a la tabla "productos" de la base de datos.
 * @author Equipo CaféUTM
 */
public class Producto {

    private int idProducto;
    private String nombre;
    private double precio;
    private boolean disponible;

    // Relación con Categoria
    private Categoria categoria;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, double precio,
            boolean disponible, Categoria categoria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
        this.categoria = categoria;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
