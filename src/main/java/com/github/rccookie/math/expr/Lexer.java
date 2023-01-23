package com.github.rccookie.math.expr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.Stack;

import com.github.rccookie.math.Rational;
import com.github.rccookie.util.StepIterator;

class Lexer extends StepIterator<Token> {

    private static final Set<Token> BEFORE_NEGATE = Set.of(
            Token.LEFT_PARENTHESIS,
            Token.LEFT_BRACKET,
            Token.COMMA,
            Token.PLUS,
            Token.MINUS,
            Token.NEGATE,
            Token.DIVIDE,
            Token.DEFINE,
            Token.LAMBDA_DEFINE,
            Token.EQUALS,
            Token.LESS,
            Token.LESS_OR_EQUAL,
            Token.GREATER,
            Token.GREATER_OR_EQUAL
    );
    private static final Set<Token> BEFORE_INSERT_MULTIPLY = Set.of(
            Token.RIGHT_PARENTHESIS,
            Token.RIGHT_BRACKET,
            Token.FACTORIAL,
            Token.DEGREE,
            Token.PERCENT,
            Token.SQUARE,
            Token.CUBE
    );
    private static final Set<Token> AFTER_INSERT_MULTIPLY = Set.of(
            Token.LEFT_PARENTHESIS,
            Token.LEFT_BRACKET
    );

    private final char[] src;
    private int p = 0;

    private Token last = null;
    private final Stack<Token> next = new Stack<>();
    private final Stack<Boolean> isAbs = new Stack<>();

    Lexer(String source) {
        if(source.contains("" + (char) 0))
            throw new MathExpressionSyntaxException("Null character in source string");
        this.src = (source + (char) 0).toCharArray();
        isAbs.push(false);
    }

    @Override
    protected Token getNext() {
        if(!next.isEmpty()) {
            Token t = next.pop();
            if(t == Token.LEFT_PARENTHESIS || t == Token.LEFT_BRACKET)
                isAbs.push(last == Token.ABS);
            else if((t == Token.RIGHT_PARENTHESIS || t == Token.RIGHT_BRACKET) && isAbs.pop())
                throw new MathExpressionSyntaxException("Mismatched abs");

            last = t;
            return last;
        }

        skipWhitespaces();
        if(p == src.length-1) return null;

        char c = src[p++];
        Token t = switch (c) {
            case ',' -> Token.COMMA;
            case '(' -> {
                isAbs.push(last == Token.ABS);
                yield Token.LEFT_PARENTHESIS;
            }
            case '[' -> {
                isAbs.push(last == Token.ABS);
                yield Token.LEFT_BRACKET;
            }
            case ')' -> {
                if(isAbs.pop())
                    throw new MathExpressionSyntaxException("Mismatched abs");
                yield Token.RIGHT_PARENTHESIS;
            }
            case ']' -> {
                if(isAbs.pop())
                    throw new MathExpressionSyntaxException("Mismatched abs");
                yield Token.RIGHT_BRACKET;
            }

            case '+' -> Token.PLUS;
            case '-' -> {
                if(src[p] != '>')
                    yield last == null || BEFORE_NEGATE.contains(last) ? Token.NEGATE : Token.MINUS;
                p++;
                yield Token.LAMBDA_DEFINE;
            }
            case '*', '\u00B7' -> Token.MULTIPLY;
            case '/' -> Token.DIVIDE;
            case '^' -> Token.POWER;
            case '!' -> Token.FACTORIAL;
            case '|' -> {
                if(isAbs.peek()) {
                    isAbs.pop();
                    yield Token.RIGHT_PARENTHESIS;
                }
                next.push(Token.LEFT_PARENTHESIS);
                yield Token.ABS;
            }

            case '\u00B0' -> Token.DEGREE;
            case '%' -> Token.PERCENT;
            case '\u00B2' -> Token.SQUARE;
            case '\u00B3' -> Token.CUBE;

            case '=' -> {
                if(src[p] != ':') yield Token.EQUALS;
                p++;
                yield Token.DEFINE_REVERSE;
            }
            case ':' -> {
                if(src[p] != '=') yield Token.DIVIDE;
                p++;
                yield Token.DEFINE;
            }
            case '<' -> {
                if(src[p] != '=') yield Token.LESS;
                p++;
                yield Token.LESS_OR_EQUAL;
            }
            case '>' -> {
                if(src[p] != '=') yield Token.GREATER;
                p++;
                yield Token.GREATER_OR_EQUAL;
            }
            case '.' -> new Token.NumberToken(new Rational(new BigDecimal("0." + readInt(true))));
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                String num = c + readInt(false);
                if(src[p] != '.')
                    yield new Token.NumberToken(new Rational(new BigInteger(num)));
                p++;
                yield new Token.NumberToken(new Rational(new BigDecimal(num + '.' + readInt(false))));
            }
            default -> {
                if(!isIdentifierChar(c))
                    throw new MathExpressionSyntaxException("Unexpected character '" + c + "'");
                String name = c + readIdentifier();
                skipWhitespaces();
                if(src[p] == '(') {
                    next.push(Token.IMPLICIT_OPERATION);
                    yield name.equals("i") ? Token.I : new Token.Symbol(name);
                }
                if(name.equals("i")) yield Token.I;
                yield src[p] == '(' ? Token.Operator.functionCall(name) : new Token.Symbol(name);
            }
        };
        if(isBeforeMultiply(last) && isAfterMultiply(t)) {
            next.push(t);
            t = Token.IMPLICIT_OPERATION;
        }
        return last = t;
    }

    private void skipWhitespaces() {
        while (Character.isWhitespace(src[p])) p++;
    }

    private String readInt(boolean required) {
        StringBuilder num = new StringBuilder();
        if(required) {
            char c = src[p++];
            if(c < '0' || c > '9')
                throw new MathExpressionSyntaxException("Number expected");
            num.append(c);
        }
        while (src[p] >= '0' && src[p] <= '9')
            num.append(src[p++]);
        return num.toString();
    }

    private String readIdentifier() {
        StringBuilder id = new StringBuilder();
        while (isIdentifierChar(src[p]))
            id.append(src[p++]);
        return id.toString();
    }

    private static boolean isIdentifierChar(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9') ||
                c == '$' || c == '#' || c == '\'' || c == '_' ||
                c == '\u00E4' || c == '\u00F6' || c == '\u00FC' || c == '\u00DF' || //ae, oe, ue, ss
                c == '\u00C4' || c == '\u00D6' || c == '\u00DC' || // AE, OE, UE
                c == '\u03A3' || c == '\u03A0' || c == '\u00B5'; // ^2, ^3, my
    }

    private static boolean isBeforeMultiply(Token t) {
        return t != null && (t instanceof Token.NumberToken || t instanceof Token.Symbol || BEFORE_INSERT_MULTIPLY.contains(t));
    }

    private static boolean isAfterMultiply(Token t) {
        return t instanceof Token.NumberToken || t instanceof Token.Symbol || (t instanceof Token.Operator o && o.isFunction()) || AFTER_INSERT_MULTIPLY.contains(t);
    }
}
