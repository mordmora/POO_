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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
public class ANDGate extends LogicGate {

    public ANDGate(int x, int y, int numInputs) {
        super(x, y, numInputs);
    }
    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawLine(x, y, x, y);
        g.drawArc(x-10, y, width, height, -90, 180);
        g.drawLine(x+13, y, x+13, y+50);
        g.drawLine(x+40, y+25, x+50, y+25);
        for (Point input : inputPoints) {
            g.fillOval(input.x - 5, input.y - 5, 10, 10);  // Dibujar un pequeño círculo en cada entrada
            g.drawLine(input.x-5, input.y, x+12, input.y);
        }
        Point output = getOutputPoint();
        g.fillOval(output.x - 5, output.y - 5, 10, 10);  // Dibujar un pequeño círculo en la salida
    }
}
