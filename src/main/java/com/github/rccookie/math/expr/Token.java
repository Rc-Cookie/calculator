package com.github.rccookie.math.expr;

import java.util.Stack;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

sealed interface Token {

    Operator COMMA             = Operator.noFunction(",", Precedence.COMMA, Builder::append);
    Operator LEFT_PARENTHESIS  = Operator.tokenOnly("(", Precedence.LEFT_PARENTHESIS);
    Operator RIGHT_PARENTHESIS = Operator.noFunction(")", Precedence.RIGHT_PARENTHESIS, Builder::buildList);
    Operator LEFT_BRACKET      = Operator.tokenOnly("[", Precedence.LEFT_BRACKET);
    Operator RIGHT_BRACKET     = Operator.noFunction("]", Precedence.RIGHT_BRACKET, Builder::buildVector);

    Operator PLUS      = new Operator("+", Precedence.PLUS, Expression::add);
    Operator MINUS     = new Operator("-", Precedence.MINUS, Expression::subtract);
    Operator MULTIPLY  = new Operator("*", Precedence.MULTIPLY, Expression::multiply);
    Operator DIVIDE    = new Operator("/", Precedence.DIVIDE, Expression::divide);
    Operator NEGATE    = new Operator("~", Precedence.NEGATE, Expression::negate);
    Operator POWER     = new Operator("^", Precedence.POWER, Expression::raise);
    Operator FACTORIAL = new Operator("!", Precedence.FACTORIAL, x -> new SimpleUnaryOperation("!", "$x!", x, 40, Functions::factorial));
    Operator ABS       = new Operator("abs", -10, true, s -> new Abs(s.pop()));

    Operator DEGREE  = new Operator("\u00B0", Precedence.DEGREE, x -> x.multiply(Number.DEG_TO_RAD()));
    Operator PERCENT = new Operator("%", Precedence.PERCENT, x -> (Expression) x.divide(100));
    Operator SQUARE  = new Operator("\u00B2", Precedence.POWER, x -> x.multiply(x));
    Operator CUBE    = new Operator("\u00B3", Precedence.POWER, x -> x.multiply(x).multiply(x));

    Operator DEFINE           = new Operator(":=", Precedence.DEFINE, FunctionDefinition::definition);
    Operator DEFINE_REVERSE   = new Operator("=:", Precedence.DEFINE, (b,a) -> FunctionDefinition.definition(a,b));
    Operator LAMBDA_DEFINE    = new Operator("->", Precedence.LAMBDA, RuntimeFunction::parseLambda);
    Operator EQUALS           = new Operator("=", Precedence.EQUALS, Expression::equalTo);
    Operator LESS             = new Operator("<", Precedence.LESS, Expression::lessThan);
    Operator LESS_OR_EQUAL    = new Operator("<=", Precedence.LESS_OR_EQUAL, Expression::lessThanOrEqual);
    Operator GREATER          = new Operator(">", Precedence.GREATER, Expression::greaterThan);
    Operator GREATER_OR_EQUAL = new Operator(">=", Precedence.GREATER_OR_EQUAL, Expression::greaterThanOrEqual);

    Operator IMPLICIT_OPERATION = new Operator("", Precedence.IMPLICIT, ImplicitOperationImpl::new);

    NumberToken I = new NumberToken(Number.I());


    record Operator(String literal, int precedence, boolean isFunction,
                    Function<Stack<? extends Expression>, Expression> function) implements Token {

        public Operator(String literal, int precedence, BinaryOperator<Expression> function) {
            this(literal, precedence, false, stack -> {
                Expression b = stack.pop(), a = stack.pop();
                return function.apply(a,b);
            });
        }
        public Operator(String literal, int precedence, UnaryOperator<Expression> function) {
            this(literal, precedence, false, stack -> function.apply(stack.pop()));
        }

        @Override
        public String toString() {
            return (isFunction ? "~" : "") + literal;
        }

        public Expression apply(Stack<? extends Expression> stack) {
            return function.apply(stack);
        }

        public static Operator noFunction(String literal, int precedence, Function<Stack<? extends Expression>, Expression> function) {
            return new Operator(literal, precedence, false, function);
        }

        public static Operator functionCall(String functionName) {
            return new Operator(functionName, -10, true,
                    s -> new FunctionCall(new Symbol(functionName), s.pop()));
        }

        public static Operator tokenOnly(String literal, int precedence) {
            return new Operator(literal, precedence, false, null);
        }
    }

    sealed interface Value extends Token { }

    record Symbol(String name) implements Value, Expression.Symbol {
        @Override
        public String toString() {
            return name;
        }

        @Override
        public Number evaluate(SymbolLookup c) {
            return c.get(name);
        }
    }

    record NumberToken(Number value) implements Value {
        @Override
        public String toString() {
            return value.toString();
        }
    }
}
