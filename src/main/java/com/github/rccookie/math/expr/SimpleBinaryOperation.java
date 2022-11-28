package com.github.rccookie.math.expr;

import java.util.function.BinaryOperator;

import com.github.rccookie.math.Number;

record SimpleBinaryOperation(String name, String format, Expression a, Expression b, int precedence, BinaryOperator<Number> function)
        implements Expression.BinaryOperation {

    SimpleBinaryOperation(String name, String format, Number a, Number b, int precedence, BinaryOperator<Number> function) {
        this(name, format, Expression.of(a), Expression.of(b), precedence, function);
    }

    @Override
    public Number evaluate(SymbolLookup c) {
        return function.apply(Expression.evaluate(a,c), Expression.evaluate(b,c));
    }

    @Override
    public Expression simplify() {
        Expression as = a.simplify(), bs = b.simplify();
        if(as instanceof Numeric an && bs instanceof Numeric bn)
            return Expression.of(function.apply(an.value(), bn.value()));
        return new SimpleBinaryOperation(name, format, as, bs, precedence, function);
    }

    @Override
    public String toString() {
        return format(format, a, b);
    }
}
