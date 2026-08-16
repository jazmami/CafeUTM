package utilidades; 

import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PanelFondo extends JPanel {

    private Image imagen;

    public PanelFondo() {
        // Hace transparente el panel base para que se aprecie el dibujo del fondo
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. Siempre se llama a super.paintComponent primero para limpiar el área
        super.paintComponent(g);

        // 2. Cargamos la imagen desde la ruta de los recursos del proyecto
        URL imgURL = getClass().getResource("/utm/cafe/imagenes/CafeUTMFondo.png");

        if (imgURL != null) {
            imagen = new ImageIcon(imgURL).getImage();
            // 3. Dibujamos la imagen escalada a todo el ancho y alto del JPanel
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}