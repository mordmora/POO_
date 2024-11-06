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
public class NANDGate extends LogicGate {

    public NANDGate(int x, int y, int numInputs) {
        super(x, y, numInputs);
    }

    @Override
    public void draw(Graphics2D g) {
        // Dibujar cuerpo de la compuerta NAND
        g.setColor(Color.BLACK);
        //g.drawRect(x, y, width, height);
        g.drawLine(x, y, x, y);
        g.drawArc(x-15, y, width, height, -90, 180);
        g.drawLine(x+10, y, x+10, y+50);
        g.drawLine(x+42, y+25, x+50, y+25);
        g.drawArc(x + 35, y + 22, 7, 7, 0, 360);

        for (Point input : inputPoints) {
            g.fillOval(input.x - 5, input.y - 5, 10, 10);  // Dibujar un pequeño círculo en cada entrada
            g.drawLine(input.x - 5, input.y, input.x+10, input.y);
        }

        Point output = getOutputPoint();
        g.fillOval(output.x - 5, output.y - 5, 10, 10);  // Dibujar un pequeño círculo en la salida
    }
}