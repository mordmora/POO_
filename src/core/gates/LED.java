package core.gates;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author glsx0
 */
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class LED extends LogicGate {
    private boolean isOn; // Estado del LED
    private BufferedImage imageA; // Imagen para el estado apagado
    private BufferedImage imageB; // Imagen para el estado encendido
    private int switchSize = 30;

    public LED(int x, int y) {
        super(x, y, 1); // Llamar al constructor de LogicGate, suponiendo que tiene 1 entrada
        this.isOn = false; // Inicialmente apagado

        // Cargar las imágenes
        try {
            imageA = ImageIO.read(getClass().getResourceAsStream("/core/Flujo electrico/RECURSOSLEDOFF.png"));
            imageB = ImageIO.read(getClass().getResourceAsStream("/core/Flujo electrico/RECURSOSLEDON.png"));
        } catch (IOException e) {
            e.printStackTrace(); // Manejo de errores en caso de que no se pueda cargar la imagen
        }
    }

    public void updateState(boolean switchState) {
        // Actualiza el estado del LED basado en el estado del interruptor
        this.isOn = switchState; // El LED estará encendido si el interruptor está encendido
    }
    

    public boolean isOn() {
        return isOn; // Devuelve el estado del LED
    }

    public boolean contains(int x, int y) {
        // Limitar el área de clic a 30x30 píxeles
        return x >= this.x && x <= this.x + switchSize && y >= this.y && y <= this.y + switchSize;
    }


    public Point getInputPoint() {
        // El punto de entrada será el centro del rectángulo de 10x10 dibujado en (x, y + 30)
        return new Point(x, y + 30);
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Determinar qué imagen dibujar dependiendo del estado
        BufferedImage currentImage = isOn ? imageB : imageA;

        // Dibujar la imagen en la posición del LED
        g2d.drawImage(currentImage, getX(), getY(), null);
        // Dibujar el rectángulo de entrada
        g2d.fillRect(x-10, y + 20, 10, 10); // Punto de entrada
    }

    public void toggle() {
        this.isOn = !this.isOn; // Cambia el estado del LED al opuesto
    updateState(this.isOn);
    }
}