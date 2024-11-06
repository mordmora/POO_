package core.gates;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author glsx0
 */
//import java.awt.Graphics2D;
//import java.awt.BasicStroke;
//
//public class NOTGate extends LogicGate {
//
//    public NOTGate(int x, int y, int numInputs) {
//        super(x, y, numInputs);
//    }
//
//    @Override
//    public void draw(Graphics2D g2d) {
//        g2d.setStroke(new BasicStroke(2));
//        g2d.drawLine(x, y, x, y + 100);
//        g2d.drawLine(x, y, x + 50, y + 50);
//        g2d.drawLine(x, y + 100, x + 50, y + 50);
//        g2d.drawLine(x - 50, y + 50, x, y + 50);
//        g2d.drawArc(x + 50, y + 43, 14, 14, 0, 360);
//        g2d.drawLine(x + 64, y + 50, x + 100, y + 50);
//        g2d.drawArc(x + 100, y + 43, 12, 12, 0, 360);
//        g2d.drawArc(x - 62, y + 44, 12, 12, 0, 360);
//
//    }
//}

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
public class NOTGate extends LogicGate {

    public NOTGate(int x, int y, int numInputs) {
        super(x, y, numInputs);
    }

    @Override
    public void draw(Graphics2D g) {
        // Dibujar cuerpo de la compuerta NAND
        g.setColor(Color.BLACK);
        //g.drawRect(x, y, width, height);
        g.drawLine(x, y, x, y);
        g.drawLine(x, y, x, y);
        g.drawLine(x, y, x, y);
        g.drawLine(x+10, y, x + 35, y + 25);
        g.drawLine(x+10, y + 50, x + 35, y + 25);
        g.drawLine(x+10, y, x+10, y+50);
        g.drawLine(x+42, y+25, x+50, y+25);
        g.drawArc(x + 35, y + 22, 7, 7, 0, 360);
        g.drawLine(x, y+25, x+10, y+25);
        g.fillOval(x - 5, y + 20, 10, 10);  // Dibujar un pequeño círculo en cada entrada
        
        // Dibujar el punto de salida
        Point output = getOutputPoint();
        g.fillOval(output.x - 5, output.y - 5, 10, 10);  // Dibujar un pequeño círculo en la salida
    }
}