package com.github.rccookie.math.expr;

import java.util.function.BinaryOperator;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

record SimpleBinaryOperation(String name, String format, BinaryOperator<RenderableExpression> renderer, Boolean aIsLeft, Expression a, Expression b, int precedence, BinaryOperator<Number> function)
        implements Expression.BinaryOperation {

    SimpleBinaryOperation(String name, String format, BinaryOperator<RenderableExpression> renderer, Boolean aIsLeft, Number a, Number b, int precedence, BinaryOperator<Number> function) {
        this(name, format, renderer, aIsLeft, Expression.of(a), Expression.of(b), precedence, function);
    }

    @Override
    public Number evaluateHalf(SymbolLookup lookup, Number ea) {
        return function.apply(ea, Expression.evaluate(b,lookup));
    }

    @Override
    public Expression simplify() {
        Expression as = a.simplify(), bs = b.simplify();
        if(as instanceof Constant an && bs instanceof Constant bn)
            return Expression.of(function.apply(an.value(), bn.value()));
        return new SimpleBinaryOperation(name, format, renderer, aIsLeft, as, bs, precedence, function);
    }

    @Override
    public String toString() {
        return format(format, a, b);
    }

    @Override
    public RenderableExpression toRenderable() {
        return toRenderable(renderer, aIsLeft, a, b);
    }
}
