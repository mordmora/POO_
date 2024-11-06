package core.gates;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Cable {
    private LogicGate source;  // Compuerta de origen
    private LogicGate destination;  // Compuerta de destino
    private int destinationInputIndex;  // Índice de la entrada de destino
    private boolean isPowered;
    private Graphics2D g2d = null;

    // Constructor de Cable
    public Cable(LogicGate source, LogicGate destination, int destinationInputIndex) {
        this.source = source;
        this.destination = destination;
        this.destinationInputIndex = destinationInputIndex;
        this.isPowered = false;
    }

    // Dibuja el cable entre la salida de la compuerta fuente y la entrada de la compuerta destino
    public void draw(Graphics2D g) {
        g2d = g;
        // Obtener el punto de salida de la compuerta fuente
        Point p1 = source.getOutputPoint();

        // Obtener el punto de entrada de la compuerta destino (según el índice de entrada)
        Point p2 = destination.getInputPoint(destinationInputIndex);

        // Verificar si ambos puntos son válidos
        if (p1 != null && p2 != null) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        if(isPowered)
            g.setColor(Color.green);
        else{
            g.setColor(Color.red);
        }
    }
    
    public Graphics2D getGraphics(){
        if(g2d != null){
            return g2d;
        }
        return null;
    }

    public void updateState() {
        System.out.println("instance of sw");
        if (source instanceof SWITCH switch1) {

            this.isPowered = switch1.isOn(); // El cable se enciende si el interruptor está encendido
        }
    }

    // Getters y setters (si es necesario)
    public LogicGate getSource() {
        return source;
    }

    public void setSource(LogicGate source) {
        this.source = source;
    }

    public LogicGate getDestination() {
        return destination;
    }

    public void setDestination(LogicGate destination) {
        this.destination = destination;
    }
    
    public void setPower(boolean power ){
        this.isPowered = power;
        System.out.println(isPowered);
    }

    public int getDestinationInputIndex() {
        return destinationInputIndex;
    }

    public void setDestinationInputIndex(int destinationInputIndex) {
        this.destinationInputIndex = destinationInputIndex;
    }
}
