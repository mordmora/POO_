package expression_parser.lexer;

import core.gates.XORGate;
import core.gates.NOTGate;
import core.gates.ORGate;
import core.gates.ANDGate;
import core.gates.NANDGate;
import core.gates.XNORGate;
import core.gates.NORGate;
import core.gates.LogicGate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicGateLexer {

    public enum TokenType {
        AND, OR, NOT, NAND, NOR, XOR, XNOR, // Compuertas lógicas
        LPAREN, RPAREN, // Paréntesis
        COMMA, // Coma
        ASSIGN, // Operador de asignación '='
        IDENTIFIER, // Variables (identificadores)
        EOF, // Fin de archivo
        INVALID // Token inválido
    }

    public static class Token {
        public TokenType type;
        public String value;
        public LogicGate gate;

        public Token(TokenType type, String value, LogicGate gate) {
            this.type = type;
            this.value = value;
            this.gate = gate;
        }

        @Override
        public String toString() {
            return String.format("<%s, %s>", type.name(), value);
        }
    }

    private static final Pattern TOKEN_PATTERNS = Pattern.compile(
            "\\s*(?:" +
            "(&|\\|\\||!|~&|~\\||\\^|~\\^)" +
            "|(\\()" +
            "|(\\))" +
            "|(,)" +
            "|(=)" +
            "|([a-zA-Z_][a-zA-Z_0-9]*)" +
            "|(.))"
    );

    private final String input; 
    private final Matcher matcher;
    private List<Token> tokens;
    private int pos;

    public LogicGateLexer(String input) {
        this.input = input;
        this.matcher = TOKEN_PATTERNS.matcher(input);
        this.tokens = new ArrayList<>();
        this.pos = 0;
    }

    public List<Token> tokenize() {
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                String logicSymbol = matcher.group(1);
                switch (logicSymbol) {
                    case "&":
                        tokens.add(new Token(TokenType.AND, logicSymbol, new ANDGate(0, 0, 2)));
                        break;
                    case "||":
                        tokens.add(new Token(TokenType.OR, logicSymbol, new ORGate(0, 0, 2)));
                        break;
                    case "!":
                        tokens.add(new Token(TokenType.NOT, logicSymbol, new NOTGate(0, 0, 1))); // Asegúrate de agregar la NOT
                        break;
                    case "~&":
                        tokens.add(new Token(TokenType.NAND, logicSymbol, new NANDGate(0, 0, 2)));
                        break;
                    case "~|":
                        tokens.add(new Token(TokenType.NOR, logicSymbol, new NORGate(0, 0, 2)));
                        break;
                    case "^":
                        tokens.add(new Token(TokenType.XOR, logicSymbol, new XORGate(0, 0, 2)));
                        break;
                    case "~^":
                        tokens.add(new Token(TokenType.XNOR, logicSymbol, new XNORGate(0, 0, 2)));
                        break;
                }
            } else if (matcher.group(2) != null) { // Paréntesis izquierdo
                tokens.add(new Token(TokenType.LPAREN, matcher.group(2), null));
            } else if (matcher.group(3) != null) { // Paréntesis derecho
                tokens.add(new Token(TokenType.RPAREN, matcher.group(3), null));
            } else if (matcher.group(4) != null) { // Coma
                tokens.add(new Token(TokenType.COMMA, matcher.group(4), null));
            } else if (matcher.group(5) != null) { // Asignación
                tokens.add(new Token(TokenType.ASSIGN, matcher.group(5), null));
            } else if (matcher.group(6) != null) { // Identificador
                tokens.add(new Token(TokenType.IDENTIFIER, matcher.group(6), null));
            } else if (matcher.group(7) != null) { // Caracter inválido
                tokens.add(new Token(TokenType.INVALID, matcher.group(7), null));
            }
        }
        tokens.add(new Token(TokenType.EOF, "", null)); // Añadir el token de fin de archivo
        return tokens;
    }

    public Token nextToken() {
        if (pos >= tokens.size()) {
            return new Token(TokenType.EOF, "", null);
        }
        return tokens.get(pos++);
    }

    public void reset() {
        pos = 0;
    }
}
