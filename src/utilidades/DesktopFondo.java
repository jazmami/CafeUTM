package utilidades;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;

public class DesktopFondo extends JDesktopPane {

    private Image imagen;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Pinta el color de fondo gris-azul pastel
        g.setColor(new Color(220, 226, 235));
        g.fillRect(0, 0, getWidth(), getHeight());

        // 2. Carga la imagen desde la carpeta "imagenes" de tu proyecto
        URL imgURL = getClass().getResource("/imagenes/CafeUTMFondo.png");

        if (imgURL != null) {
            imagen = new ImageIcon(imgURL).getImage();

            int anchoImagen = imagen.getWidth(this);
            int altoImagen = imagen.getHeight(this);

            // Si la imagen ya cargó sus dimensiones, la centra
            if (anchoImagen > 0 && altoImagen > 0) {
                int x = (getWidth() - anchoImagen) / 2;
                int y = (getHeight() - altoImagen) / 2;
                g.drawImage(imagen, x, y, this);
            } else {
                // Respaldo de dibujado en caso de carga asíncrona
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}