package controlador;

import dao.CategoriaDAO;
import dao.ProductoDAO;
import java.util.List;
import modelo.Categoria;
import modelo.Producto;

/**
 * Clase controlador que gestiona las operaciones CRUD de productos
 * (catálogo/menú), sirviendo de intermediario entre
 * FrmGestionProductos / FrmGestionPedidos y las clases DAO.
 * @author Equipo CaféUTM
 */
public class ControladorProducto {

    private ProductoDAO productoDAO = new ProductoDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();

    public boolean registrar(Producto producto) {
        return productoDAO.registrar(producto);
    }

    public boolean actualizar(Producto producto) {
        return productoDAO.actualizar(producto);
    }

    public boolean eliminar(int idProducto) {
        return productoDAO.eliminar(idProducto);
    }

    public List<Producto> listar() {
        return productoDAO.listar();
    }

    /**
     * Obtiene solo los productos disponibles, utilizado en el
     * formulario de registro de pedidos (carrito de compras).
     */
    public List<Producto> listarDisponibles() {
        return productoDAO.listarDisponibles();
    }

    /**
     * Obtiene el catálogo de categorías, utilizado para llenar el
     * JComboBox del formulario de gestión de productos.
     */
    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }
    
    /**
     * Busca y obtiene un producto por su ID en la lista de productos.
     */
    public Producto obtenerPorId(int idProducto) {
        for (Producto p : productoDAO.listar()) {
            if (p.getIdProducto() == idProducto) {
                return p;
            }
        }
        return null;
    }
    
    public boolean existePorNombre(String nombre) {
        return productoDAO.existePorNombre(nombre);
    }

    public boolean existePorNombre(String nombre, int idProductoActual) {
        return productoDAO.existePorNombre(nombre, idProductoActual);
    }
}
