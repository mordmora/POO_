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
public class  ORGate extends LogicGate {

    public ORGate(int x, int y, int numInputs) {
        super(x, y, numInputs);
    }

    @Override
    public void draw(Graphics2D g) {
        // Dibujar cuerpo de la compuerta OR
        g.setColor(Color.BLACK);
        g.drawArc(x - 10, y, 30, 50, -90, 180);
        g.drawArc(x - 30 , y, 70, 50, -90, 180); 
//        g.drawLine(x, y+10, x+15, y+10);
//        g.drawLine(x, y+20, x+20, y+20);
//        g.drawLine(x, y+30, x+20, y+30);
//        g.drawLine(x, y+40, x+15, y+40);
        g.drawLine(x+40, y+25, x+50, y+25);

        // Dibujar los puntos de entrada
        int index=0;
        for (Point input : inputPoints) {
            g.fillOval(input.x - 5, input.y - 5, 10, 10);  // Dibujar un pequeño círculo en cada entrada
            
            if(numInputs==4){
            if (index == 0 || index == 3) {
        g.drawLine(input.x - 5, input.y, input.x + 15, input.y);  
    } else if (index == 1 || index == 2) {
        g.drawLine(input.x - 5, input.y, input.x + 20, input.y);  
    }
    index++;  
            }
            else if(numInputs == 3){
                if (index == 0 || index == 2) {
        g.drawLine(input.x - 5, input.y, input.x + 18, input.y);  
    } else if (index == 1) {
        g.drawLine(input.x - 5, input.y, input.x+20, input.y); 
    }
    index++;
            }
            else if(numInputs == 2){
             g.drawLine(input.x - 5, input.y, input.x + 20, input.y);  
            }  
            
        }
        //Dibujar lineas de entrada
//        switch(numInputs){
//            case 1:
//               g.drawLine(x, y+10, x+15, y+10);
//            case 2:
//               g.drawLine(x, y+10, x+15, y+10);
//               g.drawLine(x, y+20, x+20, y+20);
//            case 3:
//               g.drawLine(x, y+10, x+15, y+10);
//               g.drawLine(x, y+20, x+20, y+20);
//               g.drawLine(x, y+30, x+20, y+30);
//            case 4:
//               g.drawLine(x, y+10, x+15, y+10);
//               g.drawLine(x, y+20, x+20, y+20);
//               g.drawLine(x, y+30, x+20, y+30);
//               g.drawLine(x, y+40, x+15, y+40); 
//               
//        }
        // Dibujar el punto de salida
        Point output = getOutputPoint();
        g.fillOval(output.x - 5, output.y - 5, 10, 10);  // Dibujar un pequeño círculo en la salida
    }
}