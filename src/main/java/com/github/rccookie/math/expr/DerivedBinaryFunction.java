package com.github.rccookie.math.expr;

import java.util.function.BinaryOperator;

import com.github.rccookie.math.Number;

record DerivedBinaryFunction(
        String name,
        String format,
        Expression.Function function,
        Expression b,
        BinaryOperator<Number> operator) implements AbstractFunction, Expression.BinaryOperation {

    DerivedBinaryFunction(String name, String format, Function function, Number b, BinaryOperator<Number> operator) {
        this(name, format, function, Expression.of(b), operator);
    }

    @Override
    public int operandCount() {
        return 2;
    }

    @Override
    public Expression a() {
        return function;
    }

    @Override
    public Expression[] operands() {
        return new Expression[] { this, b };
    }

    @Override
    public int paramCount() {
        return function.paramCount();
    }

    @Override
    public String[] paramNames() {
        return function.paramNames();
    }

    @Override
    public String toString() {
        return (paramCount() == 1 ? paramNames()[0] : '('+String.join(",", paramNames())+')')+" -> " +
                format.replace("$1", function.expr().toString()).replace("$2", b.toString());
    }

    @Override
    public Expression expr() {
        return new Body();
    }

    @Override
    public Number evaluate(SymbolLookup lookup, Number params) {
        return Expression.evaluate(operator.apply(function.evaluate(lookup, params), b), lookup);
    }



    private final class Body implements Expression {

        @Override
        public Number evaluate(SymbolLookup lookup) {
            return this;
        }

        @Override
        public int operandCount() {
            return 2;
        }

        @Override
        public Expression[] operands() {
            return new Expression[] { function, b };
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String toString() {
            return format.replace("$1", function.expr().toString()).replace("$2", b.toString());
        }
    }
}
