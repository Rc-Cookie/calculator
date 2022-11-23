package com.github.rccookie.math.expr;

import java.util.Stack;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Real;

sealed interface Token {

    Operator COMMA             = Operator.noFunction(",", -50,  Builder::append);
    Operator LEFT_PARENTHESIS  = Operator.tokenOnly("(", -99);
    Operator RIGHT_PARENTHESIS = Operator.noFunction(")", -100, Builder::buildList);
    Operator LEFT_BRACKET      = Operator.tokenOnly("[", -99);
    Operator RIGHT_BRACKET     = Operator.noFunction("]", -100, Builder::buildVector);

    Operator PLUS      = new Operator("+", 10, Expression::add);
    Operator MINUS     = new Operator("-", 10, Expression::subtract);
    Operator MULTIPLY  = new Operator("*", 20, Expression::multiply);
    Operator DIVIDE    = new Operator("/", 20, Expression::divide);
    Operator NEGATE    = new Operator("~", 11, Expression::negate);
    Operator POWER     = new Operator("^", 30, Expression::raise);
    Operator FACTORIAL = new Operator("!", 40, x -> new SimpleUnaryOperation("!", "($x)!", x, Functions::factorial));
    Operator ABS       = Operator.functionCall("abs");

    Operator DEGREE  = new Operator("\u00B0", 120, x -> x.multiply(Number.DEG_TO_RAD()));
    Operator PERCENT = new Operator("%", 120, x -> x.divide(new Real(100)));
    Operator SQUARE  = new Operator("\u00B2", POWER.precedence, x -> x.multiply(x));
    Operator CUBE    = new Operator("\u00B3", POWER.precedence, x -> x.multiply(x).multiply(x));

    Operator DEFINE           = new Operator(":=", 0, FunctionDefinition::definition);
    Operator DEFINE_REVERSE   = new Operator("=:", 0, (b,a) -> FunctionDefinition.definition(a,b));
    Operator LAMBDA_DEFINE    = new Operator("->", 1, RuntimeFunction::parseLambda);
    Operator EQUALS           = new Operator("=", 5, Expression::equalTo);
    Operator LESS             = new Operator("<", 5, Expression::lessThan);
    Operator LESS_OR_EQUAL    = new Operator("<=", 5, Expression::lessThanOrEqual);
    Operator GREATER          = new Operator(">", 5, Expression::greaterThan);
    Operator GREATER_OR_EQUAL = new Operator(">=", 5, Expression::greaterThanOrEqual);

    Operator IMPLICIT_OPERATION = new Operator("", MULTIPLY.precedence, ImplicitOperationImpl::new);



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
