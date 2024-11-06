/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package expression_parser.ast;

import expression_parser.ast.nodes.BinaryOpNode;
import expression_parser.ast.nodes.IdentifierNode;
import expression_parser.ast.nodes.UnaryOpNode;

/**
 *
 * @author Mord Mora
 */
public interface Visitor {
    void visit(BinaryOpNode node);
    void visit(UnaryOpNode node);
    void visit(IdentifierNode node);
}
