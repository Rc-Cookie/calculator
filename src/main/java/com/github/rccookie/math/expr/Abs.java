package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;

record Abs(Expression x) implements Expression.UnaryOperation {

    @Override
    public String toString(int parentPrecedence, boolean left) {
        return toString();
    }

    @Override
    public String toString() {
        return "|" + x + "|";
    }

    @Override
    public Number evaluate(SymbolLookup lookup) {
        return x.evaluate(lookup).abs();
    }

    @Override
    public Expression simplify() {
        Expression xs = x.simplify();
        return xs instanceof Constant n ? Expression.of(n.value().abs()) : new Abs(xs);
    }

    @Override
    public String name() {
        return "abs";
    }

    @Override
    public int precedence() {
        return Integer.MIN_VALUE;
    }
}
