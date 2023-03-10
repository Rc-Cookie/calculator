package com.github.rccookie.math.expr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.github.rccookie.math.Rational;
import com.github.rccookie.util.IterableIterator;
import com.github.rccookie.util.StreamOperation;

class Lexer implements IterableIterator<Token> {

    private final Iterator<Token> src;

    Lexer(String src) {
        this.src = StreamOperation.on(src.trim().chars().mapToObj(c -> (char) c), new LiteralSplitter())
                .thenPerform(new NegateFilter())
                .thenPerform(new BracketCloser())
                .thenPerform(new AbsToFuncConverter()) // Execute before implicit operation insertion, otherwise |1| will become |1<impl>|
                .thenPerform(new ImplicitOperationInsertion())
                .thenPerform(new NegativeExponentEncloser());
    }

    @Override
    public boolean hasNext() {
        return src.hasNext();
    }

    @Override
    public Token next() {
        return src.next();
    }






    private static final class LiteralSplitter implements StreamOperation.Operator<Character, Token> {

        @Override
        public void operate(Character c, StreamOperation.Input<Character> in, StreamOperation.Output<Token> out) {
            if(Character.isWhitespace(c)) {
                in.skipWhile(Character::isWhitespace);
                if(!in.hasNext()) return;
                c = in.next();
            }

            out.push(switch(c) {
                case ',' -> Token.COMMA;
                case '(' -> Token.LEFT_PARENTHESIS;
                case '[' -> Token.LEFT_BRACKET;
                case ')' -> Token.RIGHT_PARENTHESIS;
                case ']' -> Token.RIGHT_BRACKET;

                case '+' -> Token.PLUS;
                case '-' -> in.skipIf(n -> n == '>') ? Token.LAMBDA_DEFINE : Token.MINUS;
                case '*', '·' -> Token.MULTIPLY;
                case '/' -> Token.DIVIDE;
                case '^' -> Token.POWER;
                case '!' -> Token.FACTORIAL;
                case '|' -> Token.ABS;

                case '°' -> Token.DEGREE;
                case '%' ->      Token.PERCENT;
                case '²' -> Token.SQUARE;
                case '³' -> Token.CUBE;

                case '=' -> in.skipIf(n -> n == ':') ? Token.DEFINE_REVERSE : Token.EQUALS;
                case ':' -> in.skipIf(n -> n == '=') ? Token.DEFINE : Token.DIVIDE;
                case '<' -> in.skipIf(n -> n == '=') ? Token.LESS_OR_EQUAL : Token.LESS;
                case '>' -> in.skipIf(n -> n == '=') ? Token.GREATER_OR_EQUAL : Token.GREATER;
                case '.' -> new Token.NumberToken(new Rational(new BigDecimal("0." + readInt(true, in))));
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    String num = c + readInt(false, in);
                    if(!in.skipIf(n -> n == '.'))
                        yield new Token.NumberToken(new Rational(new BigInteger(num)));
                    yield new Token.NumberToken(new Rational(new BigDecimal(num + '.' + readInt(false, in))));
                }
                default -> {
                    if(!isIdentifierChar(c))
                        throw new MathExpressionSyntaxException("Unexpected character '" + c + "'");
                    String name = c + readIdentifier(in);
                    in.skipWhile(Character::isWhitespace);
                    yield new Token.Symbol(name);
                }
            });
        }

        private static String readInt(boolean required, StreamOperation.Input<Character> in) {
            StringBuilder num = new StringBuilder();
            if(required) {
                char c = in.tryNext();
                if(c < '0' || c > '9')
                    throw new MathExpressionSyntaxException("Number expected");
                num.append(c);
            }
            char c;
            while(in.hasNext() && (c = in.peek(0)) >= '0' && c <= '9')
                num.append(in.next());
            return num.toString();
        }

        private boolean isIdentifierChar(char c) {
            return (c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') ||
                    (c >= '0' && c <= '9') ||
                    c == '$' || c == '#' || c == '\'' || c == '_' ||
                    c == 'ä' || c == 'ö' || c == 'ü' || c == 'ß' || //ae, oe, ue, ss
                    c == 'Ä' || c == 'Ö' || c == 'Ü' || // AE, OE, UE
                    c == 'Σ' || c == 'Π' || c == 'µ'; // ^2, ^3, my
        }

        private String readIdentifier(StreamOperation.Input<Character> in) {
            StringBuilder id = new StringBuilder();
            Character c;
            while((c = in.nextIf(this::isIdentifierChar)) != null)
                id.append(c);
            return id.toString();
        }
    }

    private static final class NegateFilter implements StreamOperation.Operator<Token, Token> {
        private static final Set<Token> BEFORE = Set.of(
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
                Token.GREATER_OR_EQUAL,
                Token.POWER
        );

        boolean first = true;

        @Override
        public void operate(Token t, StreamOperation.Input<Token> in, StreamOperation.Output<Token> out) {
            if(first != (first = false) && t == Token.MINUS) {
                out.push(Token.NEGATE);
                return;
            }
            out.push(t);
            if(BEFORE.contains(t) && in.skipIf(n -> n == Token.MINUS))
                out.push(Token.NEGATE);
        }
    }

    private static final class BracketCloser implements StreamOperation.Operator<Token, Token> {

        private final Stack<Token> expected = new Stack<>();

        @Override
        public void operate(Token x, StreamOperation.Input<Token> in, StreamOperation.Output<Token> out) {
            if(x == Token.LEFT_PARENTHESIS)
                expected.push(Token.RIGHT_PARENTHESIS);
            else if(x == Token.LEFT_BRACKET)
                expected.push(Token.RIGHT_BRACKET);
            else if(x == Token.RIGHT_PARENTHESIS || x == Token.RIGHT_BRACKET) {
                while(!expected.isEmpty() && expected.peek() != x)
                    out.push(expected.pop());

                if(expected.isEmpty()) {
                    if(x == Token.RIGHT_PARENTHESIS)
                        throw new MathExpressionSyntaxException("Mismatched parenthesis (too many closing or trying to close bracket with parenthesis)");
                    else throw new MathExpressionSyntaxException("Mismatched brackets (too many closing or trying to close parenthesis with bracket)");
                }
                expected.pop();
            }

            out.push(x);

            if(!in.hasNext()) while(!expected.isEmpty())
                out.push(expected.pop());
        }
    }

    private static final class AbsToFuncConverter implements StreamOperation.Operator<Token, Token> {
        private final Stack<Boolean> open = new Stack<>(); { open.push(false); }

        @Override
        public void operate(Token x, StreamOperation.Input<Token> in, StreamOperation.Output<Token> out) {
            if(x == Token.ABS) {
                if(open.push(!open.pop())) // Is now open
                    out.push(Token.ABS, Token.LEFT_PARENTHESIS);
                else out.push(Token.RIGHT_PARENTHESIS);
            }
            else {
                out.push(x);
                if(x == Token.LEFT_PARENTHESIS || x == Token.LEFT_BRACKET)
                    open.push(false);
                else if(x == Token.RIGHT_PARENTHESIS || x == Token.RIGHT_BRACKET) {
                    if(open.pop()) throw new MathExpressionSyntaxException("Mismatched abs");
                    if(open.isEmpty()) throw new MathExpressionSyntaxException("Mismatched parenthesis / brackets (too many closing)");
                }
            }

            if(!in.hasNext()) {
                if(open.size() != 1) throw new MathExpressionSyntaxException("Unclosed parenthesis / brackets");
                if(open.peek()) throw new MathExpressionSyntaxException("Mismatched abs");
            }
        }
    }

    private static final class ImplicitOperationInsertion implements StreamOperation.Operator<Token, Token> {
        private static final Set<Token> BEFORE = Set.of(
                Token.RIGHT_PARENTHESIS,
                Token.RIGHT_BRACKET,
                Token.FACTORIAL,
                Token.DEGREE,
                Token.PERCENT,
                Token.SQUARE,
                Token.CUBE
        );
        private static final Set<Token> AFTER = Set.of(
                Token.LEFT_PARENTHESIS,
                Token.LEFT_BRACKET
        );

        @Override
        public void operate(Token t, StreamOperation.Input<Token> in, StreamOperation.Output<Token> out) {
            out.push(t);
            if(isBefore(t) && isAfter(in.peek()))
                out.push(Token.IMPLICIT_OPERATION);
        }

        private static boolean isBefore(Token t) {
            return t instanceof Token.NumberToken || t instanceof Token.Symbol || BEFORE.contains(t);
        }

        private static boolean isAfter(Token t) {
            return t != null && (t instanceof Token.NumberToken || t instanceof Token.Symbol || (t instanceof Token.Operator o && o.isFunction()) || AFTER.contains(t));
        }
    }

    private static final class NegativeExponentEncloser implements StreamOperation.Operator<Token, Token> {
        @Override
        public void operate(Token t, StreamOperation.Input<Token> in, StreamOperation.Output<Token> out) {
            out.push(t);
            if(t == Token.POWER && in.skipIf(n -> n == Token.NEGATE || n == Token.MINUS))
                out.push(Token.LEFT_PARENTHESIS, Token.NEGATE, in.next() /* Number or variable: 10^-2, 10^-x */, Token.RIGHT_PARENTHESIS);
        }
    }
}
