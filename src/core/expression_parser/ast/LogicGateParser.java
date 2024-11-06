package expression_parser.ast;

import expression_parser.ast.nodes.ASTNode;
import expression_parser.ast.nodes.BinaryOpNode;
import expression_parser.ast.nodes.IdentifierNode;
import expression_parser.ast.nodes.UnaryOpNode;
import expression_parser.lexer.LogicGateLexer;
import core.gates.NORGate;
import core.gates.NANDGate;
import core.gates.ANDGate;
import core.gates.ORGate;

import java.util.List;

public class LogicGateParser {

    private final LogicGateLexer lexer;
    private List<LogicGateLexer.Token> tokens;
    private int currentTokenIndex;

    public LogicGateParser(LogicGateLexer lexer) {
        this.lexer = lexer;
        this.tokens = lexer.tokenize();
        this.currentTokenIndex = 0;
    }

    private LogicGateLexer.Token currentToken() {
        return tokens.get(currentTokenIndex);
    }

    private LogicGateLexer.Token nextToken() {
        if (currentTokenIndex < tokens.size() - 1) {
            currentTokenIndex++;
        }
        return currentToken();
    }

    private boolean expect(LogicGateLexer.TokenType type) {
        if (currentToken().type == type) {
            nextToken();
            return true;
        }
        return false;
    }

    // Expr -> Term (OR | NOR Term)*
    private ASTNode Expr() {
        ASTNode left = Term();
        while (currentToken().type == LogicGateLexer.TokenType.OR || 
               currentToken().type == LogicGateLexer.TokenType.NOR) {
            LogicGateLexer.Token operator = currentToken();
            nextToken(); // Consume el operador OR o NOR
            ASTNode right = Term();
            if (operator.type == LogicGateLexer.TokenType.OR) {
                left = new BinaryOpNode("|", left, right, new ORGate(0, 0, 2));
            } else if (operator.type == LogicGateLexer.TokenType.NOR) {
                left = new BinaryOpNode("~|", left, right, new NORGate(0, 0, 0));
            }
        }
        return left;
    }

    // Term -> Factor (AND | NAND Factor)*
    private ASTNode Term() {
        ASTNode left = Factor();
        while (currentToken().type == LogicGateLexer.TokenType.AND ||
               currentToken().type == LogicGateLexer.TokenType.NAND) {
            LogicGateLexer.Token operator = currentToken();
            nextToken(); // Consumir el operador AND o NAND
            ASTNode right = Factor(); 
            if (operator.type == LogicGateLexer.TokenType.AND) {
                left = new BinaryOpNode("&", left, right, new ANDGate(0, 0, 2));
            } else if (operator.type == LogicGateLexer.TokenType.NAND) {
                left = new BinaryOpNode("~&", left, right, new NANDGate(0, 0, 2));
            }
        }
        return left;
    }

    // Factor -> NOT? Primary
    private ASTNode Factor() {
        if (currentToken().type == LogicGateLexer.TokenType.NOT) {
            nextToken(); // Consumir el NOT
            ASTNode operand = Primary();
            return new UnaryOpNode(operand); // Crear nodo NOT
        }
        return Primary();
    }

    // Primary -> IDENTIFIER | LPAREN Expr RPAREN
    private ASTNode Primary() {
        if (currentToken().type == LogicGateLexer.TokenType.IDENTIFIER) {
            String identifier = currentToken().value;
            nextToken(); // Consumir el identificador
            return new IdentifierNode(identifier); // Crear nodo identificador
        } else if (currentToken().type == LogicGateLexer.TokenType.LPAREN) {
            nextToken(); // Consumir '('
            ASTNode expr = Expr(); 
            if (!expect(LogicGateLexer.TokenType.RPAREN)) {
                throw new RuntimeException("Error de sintaxis: Se esperaba ')'.");
            }
            return expr;
        } else {
            throw new RuntimeException("Error de sintaxis: Se esperaba un identificador o '('.");
        }
    }

    public ASTNode parse() {
        ASTNode root = Expr();
        if (currentToken().type != LogicGateLexer.TokenType.EOF) {
            throw new RuntimeException("EOF Expected.");
        }
        return root;
    }
}

