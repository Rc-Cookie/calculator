package com.github.rccookie.math.expr;

import java.util.function.BinaryOperator;

import com.github.rccookie.math.Number;

record SimpleBinaryOperation(String name, String format, Expression a, Expression b, BinaryOperator<Number> function)
        implements Expression.BinaryOperation {

    SimpleBinaryOperation(String name, String format, Number a, Number b, BinaryOperator<Number> function) {
        this(name, format, Expression.of(a), Expression.of(b), function);
    }

    @Override
    public Number evaluate(SymbolLookup c) {
        return function.apply(Expression.evaluate(a,c), Expression.evaluate(b,c));
    }

    @Override
    public String toString() {
        return format.replace("$1", a.toString()).replace("$2", b.toString());
    }
}
