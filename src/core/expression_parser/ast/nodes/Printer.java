package expression_parser.ast;

import core.DrawnFrame;
import core.gates.SWITCH; // Asegúrate de importar la clase SWITCH
import expression_parser.ast.nodes.*;

import java.util.HashSet;
import java.util.Set;

public class Printer implements Visitor {

    private final DrawnFrame dp;
    private final Set<String> uniqueIdentifiers; // Almacena identificadores únicos

    public Printer(DrawnFrame dp) {
        this.dp = dp;
        this.uniqueIdentifiers = new HashSet<>();
    }

    @Override
    public void visit(BinaryOpNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        dp.addGate(node.getGate());
    }

    @Override
    public void visit(UnaryOpNode node) {
        node.getOperand().accept(this); // Visita el nodo hijo del operador unario
    }

    @Override
    public void visit(IdentifierNode node) {
        String identifier = node.getName();

        // Si el identificador es único, creamos un SWITCH y lo agregamos al DrawnFrame
        if (uniqueIdentifiers.add(identifier)) { // Añade solo si es único
            SWITCH newSwitch = new SWITCH(identifier, 0, 0 ); // Pasa el identificador como etiqueta
            dp.addGate(newSwitch);
            System.out.println("SWITCH creado para: " + identifier);
        }
    }
}
