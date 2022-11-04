package com.github.rccookie.math.interpreter;

import java.util.Stack;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

sealed interface Token {

    Operator COMMA             = new Operator(",", 2, -50,  Expressions::append);
    Operator LEFT_PARENTHESIS  = new Operator("(", 0, -99);
    Operator RIGHT_PARENTHESIS = new Operator(")", 1, -100, Expressions::buildList);
    Operator LEFT_BRACKET      = new Operator("[", 0, -99);
    Operator RIGHT_BRACKET     = new Operator("]", 0, -100, Expressions::buildVector);

    Operator PLUS      = new Operator("+", 2, 10, Number::add);
    Operator MINUS     = new Operator("-", 2, 10, Number::subtract);
    Operator MULTIPLY  = new Operator("*", 2, 20, Number::multiply);
    Operator DIVIDE    = new Operator("/", 2, 20, Number::divide);
    Operator NEGATE    = new Operator("~", 1, 11, Number::negate);
    Operator POWER     = new Operator("^", 2, 30, Number::raise);
    Operator FACTORIAL = new Operator("!", 1, 40, Functions::factorial);
    Operator ABS       = new Operator("abs");

    Operator DEGREE  = new Operator("\u00B0", 1, 120, Functions::degToRad);
    Operator PERCENT = new Operator("%",      1, 120, Functions::fromPercent);
    Operator SQUARE  = new Operator("\u00B2", 1, POWER.precedence, Functions::square);
    Operator CUBE    = new Operator("\u00B3", 1, POWER.precedence, Functions::cube);

    Operator DEFINE           = new Operator(":=", 2, 0, Expressions::define);
    Operator DEFINE_REVERSE   = new Operator("=:", 2, 0, Expressions::defineReverse);
    Operator LAMBDA_DEFINE    = new Operator("->", 2, 1, Expressions::lambda);
    Operator EQUALS           = new Operator("=",  2, 5, Number::equalTo);
    Operator LESS             = new Operator("<",  2, 5, Number::lessThan);
    Operator LESS_OR_EQUAL    = new Operator("<=", 2, 5, Number::lessThanOrEqual);
    Operator GREATER          = new Operator(">",  2, 5, Number::greaterThan);
    Operator GREATER_OR_EQUAL = new Operator(">=", 2, 5, Number::greaterThanOrEqual);

    Operator FUNCTION_CALL = new Operator("<invoke>", 2, MULTIPLY.precedence, Expressions::functionCallOrMultiply);



    record Operator(String literal, int paramCount, int precedence, boolean isFunction,
                    Function<Stack<? extends Number>, Number> function) implements Token {
        public Operator(String literal, int paramCount, int precedence, Function<Stack<? extends Number>, Number> function) {
            this(literal, paramCount, precedence, false, function);
        }
        public Operator(String literal, int paramCount, int precedence, BinaryOperator<Number> function) {
            this(literal, paramCount, precedence, false, stack -> {
                Number b = stack.pop(), a = stack.pop();
                return function.apply(a,b);
            });
        }
        public Operator(String literal, int paramCount, int precedence, UnaryOperator<Number> function) {
            this(literal, paramCount, precedence, false, stack -> function.apply(stack.pop()));
        }
        public Operator(String literal, int paramCount, int precedence) {
            this(literal, paramCount, precedence, false, null);
        }
        public Operator(String functionName) {
            this(functionName, 1, -10, true, s -> Expressions.callFunctionOrMultiply(functionName, s.pop()));
        }

        @Override
        public String toString() {
            return (isFunction ? "~" : "") + literal;
        }

        public Number apply(Stack<? extends Number> stack) {
            return function.apply(stack);
        }
    }

    sealed interface Value extends Token { }

    record Variable(String name) implements Value, Expression {
        @Override
        public String toString() {
            return name;
        }

        @Override
        public Number evaluate(Calculator c) {
            return c.getVar(name);
        }
    }

    record NumberToken(Number value) implements Value {
        @Override
        public String toString() {
            return value.toString();
        }
    }
}
