package expression_parser.ast.nodes;

import expression_parser.ast.Visitor;

public class UnaryOpNode extends ASTNode {
    private final ASTNode operand; // El operando de la operación unaria

    public UnaryOpNode(ASTNode operand) {
        this.operand = operand; // Almacena el operando
    }

    public ASTNode getOperand() {
        return operand; // Retorna el operando
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this); // Acepta el visitante
    }
}
