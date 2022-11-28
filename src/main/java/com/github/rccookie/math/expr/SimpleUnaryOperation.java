package com.github.rccookie.math.expr;

import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

record SimpleUnaryOperation(String name, String format, Expression x, int precedence, UnaryOperator<Number> function)
        implements Expression.UnaryOperation {
    @Override
    public Number evaluate(SymbolLookup l) {
        return function.apply(x.evaluate(l));
    }

    @Override
    public Expression simplify() {
        Expression simplified = x.simplify();
        if(simplified instanceof Numeric n)
            return Expression.of(function.apply(n.value()));
        return new SimpleUnaryOperation(name, format, simplified, precedence, function);
    }

    @Override
    public String toString() {
        return format(format, x);
    }
}
