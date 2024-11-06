import expression_parser.ast.Visitor;
import expression_parser.ast.nodes.BinaryOpNode;
import expression_parser.ast.nodes.IdentifierNode;
import expression_parser.ast.nodes.UnaryOpNode;
import java.awt.Graphics;

class DrawingVisitor implements Visitor {


    @Override
    public void visit(BinaryOpNode node) {

        node.getLeft().accept(this);
        
        node.getRight().accept(this);
    }

    @Override
    public void visit(UnaryOpNode node) {

        node.getOperand().accept(this);
    }

    @Override
    public void visit(IdentifierNode node) {

    }
}
