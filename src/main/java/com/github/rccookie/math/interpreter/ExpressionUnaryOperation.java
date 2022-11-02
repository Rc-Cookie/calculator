package com.github.rccookie.math.interpreter;

import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

record ExpressionUnaryOperation(String format, Expression x, UnaryOperator<Number> function) implements Expression {
    @Override
    public Number evaluate(Calculator c) {
        return function.apply(x.evaluate(c));
    }

    @Override
    public String toString() {
        return format.replace("$x", x.toString());
    }
}
