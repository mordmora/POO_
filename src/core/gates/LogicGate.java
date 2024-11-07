package core.gates;

import java.awt.Point;
import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class LogicGate {
    protected int x, y;
    protected int width = 50, height = 50;
    protected int numInputs; // Número de entradas
    protected ArrayList<Point> inputPoints;  // Lista de puntos de entrada
    public ArrayList<Cable> inputCables;  // Lista de cables de entrada
    public ArrayList<Cable> outputCables;  // Cable de salida

    public LogicGate(int x, int y, int numInputs) {
        this.x = x;
        this.y = y;
        this.numInputs = numInputs;
        this.inputPoints = new ArrayList<>();
        this.inputCables = new ArrayList<>(numInputs); // Inicializar lista de cables de entrada
        this.outputCables = new ArrayList<>();
        calculateInputPoints();  // Calcula la posición de las entradas
    }
    
    public int getNextAvailableInputIndex() {
    for (int i = 0; i < inputCables.size(); i++) {
        if (inputCables.get(i) == null) {  // Si la entrada está disponible
            return i;
        }
    }
    return -1;  // No hay entradas disponibles
}

    // Método abstracto que implementarán las compuertas
    public abstract void draw(Graphics2D g);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        calculateInputPoints();  // Recalcular puntos de entrada si la posición cambia
    }

    // Calcular los puntos de entrada en función del número de entradas
    private void calculateInputPoints() {
        inputPoints.clear();
        for (int i = 0; i < numInputs; i++) {
            // Distribuye los puntos de entrada de manera uniforme en el lado izquierdo de la compuerta
            int inputY = y + height * (i + 1) / (numInputs + 1);
            inputPoints.add(new Point(x, inputY));
        }
    }

    // Devuelve el punto de salida del cable (generalmente al lado derecho de la compuerta)
    public Point getOutputPoint() {
        return new Point(x + width, y + height / 2);
    }

    // Devuelve un punto de entrada específico (si la compuerta tiene varias entradas)
    public Point getInputPoint(int index) {
        if (index >= 0 && index < inputPoints.size()) {
            return inputPoints.get(index);
        }
        return null;  // Si el índice es inválido
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }

    public int getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
        updateInputPoints();
    }

    private void updateInputPoints() {
        inputPoints.clear();
        for(int i=0;i<numInputs; i++){
            int inputX = x;
            int inputY = y + (i + 1) * (height / (numInputs + 1));
            inputPoints.add(new Point(inputX, inputY));
        }
    }

   public void addInputCable(Cable cable, int index) {
    if (index >= 0 && index < numInputs) {
        inputCables.set(index, cable);
    }
}

    public ArrayList<Cable> getOutputCables() {
        return outputCables;
    }
}
