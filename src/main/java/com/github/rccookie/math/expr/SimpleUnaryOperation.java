package com.github.rccookie.math.expr;

import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

record SimpleUnaryOperation(String name, String format, Expression x, UnaryOperator<Number> function)
        implements Expression.UnaryOperation {
    @Override
    public Number evaluate(SymbolLookup l) {
        return function.apply(x.evaluate(l));
    }

    @Override
    public String toString() {
        return format.replace("$x", x.toString());
    }
}
