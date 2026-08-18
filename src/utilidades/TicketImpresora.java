package utilidades;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.DetallePedido;
import modelo.Pedido;

/**
 * Generador e impresor de tickets/comandas nativo usando la API estándar de Java.
 * Permite imprimir físicamente o exportar a PDF mediante 'Microsoft Print to PDF'.
 */
public class TicketImpresora implements Printable {

    private Pedido pedido;
    private List<DetallePedido> detalles;

    public TicketImpresora(Pedido pedido, List<DetallePedido> detalles) {
        this.pedido = pedido;
        this.detalles = detalles;
    }

    /**
     * Dibuja el formato del ticket sobre el lienzo de impresión.
     */
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // Fuentes
        Font fontTitulo = new Font("Monospaced", Font.BOLD, 12);
        Font fontSub = new Font("Monospaced", Font.BOLD, 9);
        Font fontNormal = new Font("Monospaced", Font.PLAIN, 8);
        Font fontPequena = new Font("Monospaced", Font.PLAIN, 7);

        int y = 20;
        int margenIzq = 10;

        // 1. Encabezado
        g2d.setFont(fontTitulo);
        g2d.setColor(new Color(122, 15, 42)); // Color institucional
        g2d.drawString("CAFETERÍA UNIVERSITARIA", margenIzq + 10, y);
        y += 12;
        g2d.drawString("        CaféUTM", margenIzq + 10, y);
        y += 14;

        g2d.setColor(Color.BLACK);
        g2d.setFont(fontNormal);
        g2d.drawString("==========================================", margenIzq, y);
        y += 12;

        // 2. Datos Generales del Pedido
        g2d.setFont(fontSub);
        g2d.drawString("FOLIO: #PED-" + String.format("%06d", pedido.getIdPedido()), margenIzq, y);
        y += 12;

        g2d.setFont(fontNormal);
        String fecha = (pedido.getHoraPedido() != null)
                ? pedido.getHoraPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))
                : "N/A";
        g2d.drawString("Fecha: " + fecha, margenIzq, y);
        y += 11;

        String cliente = (pedido.getUsuario() != null) ? pedido.getUsuario().getNombre() : "Cliente";
        String mat = (pedido.getUsuario() != null && pedido.getUsuario().getMatricula() != null)
                ? pedido.getUsuario().getMatricula() : "N/A";
        g2d.drawString("Cliente: " + cliente + " (" + mat + ")", margenIzq, y);
        y += 11;

        String recoleccion = (pedido.getHoraRecoleccionEstimada() != null)
                ? pedido.getHoraRecoleccionEstimada().toString() : "Inmediata";
        g2d.drawString("Hora Recolección: " + recoleccion, margenIzq, y);
        y += 12;

        g2d.drawString("------------------------------------------", margenIzq, y);
        y += 12;

        // 3. Encabezados de Partidas
        g2d.setFont(fontSub);
        g2d.drawString("Cant.  Descripción                Subtotal", margenIzq, y);
        y += 10;
        g2d.setFont(fontNormal);
        g2d.drawString("------------------------------------------", margenIzq, y);
        y += 12;

        // 4. Detalle de Productos
        for (DetallePedido d : detalles) {
            String nombreProd = d.getProducto().getNombre();
            if (nombreProd.contains("(") && nombreProd.contains(")")) {
                nombreProd = nombreProd.substring(0, nombreProd.indexOf("(")).trim();
            }
            if (nombreProd.length() > 22) {
                nombreProd = nombreProd.substring(0, 20) + "..";
            }

            // Calcular el subtotal si viniera en cero
            double subtotalItem = d.getSubtotal();
            if (subtotalItem <= 0) {
                double precioU = (d.getPrecioUnitario() > 0) ? d.getPrecioUnitario() 
                                : (d.getProducto() != null ? d.getProducto().getPrecio() : 0.0);
                subtotalItem = d.getCantidad() * precioU;
            }

            // Fila principal del producto
            String lineaProd = String.format("%-6s %-24s $%6.2f",
                    d.getCantidad() + "x",
                    nombreProd,
                    subtotalItem);
            g2d.drawString(lineaProd, margenIzq, y);
            y += 10;

            // Indicaciones / Guisados / Bebida si existen
            if (d.getIndicacionesEspeciales() != null
                    && !d.getIndicacionesEspeciales().trim().isEmpty()
                    && !"null".equalsIgnoreCase(d.getIndicacionesEspeciales().trim())) {
                g2d.setFont(fontPequena);
                g2d.drawString("   * " + d.getIndicacionesEspeciales(), margenIzq, y);
                g2d.setFont(fontNormal);
                y += 10;
            }
        }

        g2d.drawString("==========================================", margenIzq, y);
        y += 14;

        // 5. Total
        g2d.setFont(fontTitulo);
        g2d.drawString(String.format("TOTAL: $%.2f MXN", pedido.getTotal()), margenIzq + 25, y);
        y += 16;

        // 6. Pie de Ticket
        g2d.setFont(fontPequena);
        g2d.drawString("¡Gracias por tu compra!", margenIzq + 40, y);
        y += 9;
        g2d.drawString("Presenta este ticket al recoger.", margenIzq + 22, y);

        return PAGE_EXISTS;
    }

    /**
     * Abre el cuadro de diálogo del sistema para imprimir o guardar como PDF.
     */
    public static void imprimir(Pedido p, List<DetallePedido> d) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new TicketImpresora(p, d));

        // Muestra la ventana de selección de impresora nativa de Windows
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(null, "Ticket enviado a impresión / PDF correctamente.");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(null, "Error al imprimir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}