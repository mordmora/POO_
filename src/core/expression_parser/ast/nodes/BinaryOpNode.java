package expression_parser.ast.nodes;

import core.gates.LogicGate;
import expression_parser.ast.Printer;
import expression_parser.ast.Visitor;

public class BinaryOpNode extends ASTNode {
    private String operator;
    private LogicGate gate;
    private ASTNode left;
    private ASTNode right;

    public BinaryOpNode(String operator, ASTNode left, ASTNode right, LogicGate gate) {
        this.operator = operator;
        this.left = left;
        this.right = right;
        this.gate = gate;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }
    
    public LogicGate getGate(){
        return gate;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}
