/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expression_parser.ast.nodes;

import expression_parser.ast.Visitor;

/**
 *
 * @author Mord Mora
 */
public abstract class ASTNode {
    public abstract void accept(Visitor v); 
}
