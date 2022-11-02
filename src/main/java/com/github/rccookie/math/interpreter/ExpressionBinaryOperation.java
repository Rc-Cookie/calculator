package com.github.rccookie.math.interpreter;

import java.util.function.BinaryOperator;

import com.github.rccookie.math.Number;

record ExpressionBinaryOperation(String format, Expression a, Number b, BinaryOperator<Number> function) implements Expression {
    @Override
    public Number evaluate(Calculator c) {
        return function.apply(a.evaluate(c), Expression.evaluate(b, c));
    }

    @Override
    public String toString() {
        return format.replace("$1", a.toString()).replace("$2", b.toString());
    }
}
