package com.github.rccookie.math.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

final class InfixConverter {

    private final Stack<Token.Operator> operators = new Stack<>();
    private final List<Token> output = new ArrayList<>();
    private final Iterable<Token> tokens;

    public InfixConverter(Iterable<Token> tokens) {
        this.tokens = tokens;
    }

    public Iterable<Token> toPostfix() {
        process(Token.LEFT_PARENTHESIS);
        for(Token t : tokens) process(t);
        process(Token.RIGHT_PARENTHESIS);
        while(!operators.isEmpty()) {
            Token t = operators.pop();
//            if(t == Token.LEFT_PARENTHESIS)
//                throw new IllegalArgumentException("Mismatched parenthesis (missing right parenthesis)");
//            if(t == Token.LEFT_BRACKET)
//                throw new IllegalArgumentException("Mismatched bracket (missing right bracket)");
//            if(t != Token.LEFT_PARENTHESIS && t != Token.LEFT_BRACKET)
            output.add(t);
        }
        return output;
    }

    private void process(Token t) {
        switch(t) {
            case Token.Value v -> output.add(v);
            case Token.Operator o -> {
                if(o == Token.COMMA) {
                    while(!operators.isEmpty() && operators.peek().precedence() > Precedence.COMMA)
                        output.add(operators.pop());
                    output.add(Token.COMMA);
                }
                else if(o == Token.LEFT_PARENTHESIS || o == Token.LEFT_BRACKET) {
                    operators.push(o);
                    output.add(o);
                }
                else if(o == Token.RIGHT_PARENTHESIS || o == Token.RIGHT_BRACKET) {
                    Token other = o == Token.RIGHT_BRACKET ? Token.LEFT_BRACKET : Token.LEFT_PARENTHESIS;
                    while(operators.isEmpty() || operators.peek() != other) {
                        if(operators.isEmpty())
                            throw new IllegalArgumentException("Mismatched " + (o==Token.RIGHT_BRACKET?"bracket":"parenthesis"));
                        output.add(operators.pop());
                    }
                    output.add(o);
                    operators.pop(); // left parenthesis / bracket
                    if(!operators.isEmpty() && operators.peek().isFunction())
                        output.add(operators.pop());
                }
                else {
                    while (!o.isFunction() &&
                            !operators.isEmpty() &&
                            (operators.peek().precedence() >= o.precedence()))
                        output.add(operators.pop());
                    operators.push(o);
                }
            }
            default -> throw new AssertionError(t);
        }
    }
}
