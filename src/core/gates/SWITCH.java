package core.gates;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class SWITCH extends LogicGate {
    private boolean isOn; // Estado del interruptor
    private BufferedImage imageA; // Imagen para el estado apagado
    private BufferedImage imageB; // Imagen para el estado encendido
    private int switchSize = 30;
    private String label; // Para almacenar la letra encima del SWITCH

    // Constructor modificado para incluir la etiqueta
    public SWITCH(String label, int x, int y) {
        super(x, y, 1); // Llamar al constructor de LogicGate
        this.isOn = false; // Inicialmente apagado
        this.label = label; // Asigna la letra

        // Cargar las imágenes
        try {
            imageA = ImageIO.read(getClass().getResource("/core/Flujo electrico/RECURSOSSWOFF.png"));
            imageB = ImageIO.read(getClass().getResource("/core/Flujo electrico/RECURSOSSWON.png"));
        } catch (IOException e) {
            e.printStackTrace(); // Manejo de errores
        }
    }

    public void toggle() {
        isOn = !isOn; // Cambia el estado del interruptor
        for(Cable c : inputCables){
            c.updateState();
        }
    }

    public boolean isOn() {
        return isOn; // Devuelve el estado del interruptor
    }

    public String getLabel() {
        return label; // Devuelve la etiqueta del SWITCH
    }

    public boolean contains(int x, int y) {
        // Sobrescribir para limitar el área de clic a 30x30 píxeles
        return x >= this.x && x <= this.x + switchSize && y >= this.y && y <= this.y + switchSize;
    }

    public Point getOutputPoint() {
        // El punto de salida será el centro del rectángulo de 10x10 dibujado en (x+30, y+10)
        return new Point(x + 35, y + 15);
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Determinar qué imagen dibujar dependiendo del estado
        BufferedImage currentImage = isOn ? imageB : imageA; // Cambiar aquí

        // Dibujar la imagen en la posición de la compuerta
        g2d.drawImage(currentImage, getX(), getY(), null);

        // Dibujar el rectángulo de salida
        g2d.fillRect(x + 30, y + 10, 10, 10);

        // Dibujar la letra encima del SWITCH
        g2d.drawString(label, x + 10, y - 5); // Ajusta las coordenadas según sea necesario
    }

 
}
