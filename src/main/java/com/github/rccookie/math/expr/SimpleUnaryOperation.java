package com.github.rccookie.math.expr;

import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

record SimpleUnaryOperation(String name, String format, UnaryOperator<RenderableExpression> renderer, Expression x, int precedence, UnaryOperator<Number> function)
        implements Expression.UnaryOperation {
    @Override
    public Number evaluate(SymbolLookup l) {
        return function.apply(x.evaluate(l));
    }

    @Override
    public Expression simplify() {
        Expression simplified = x.simplify();
        if(simplified instanceof Constant n)
            return Expression.of(function.apply(n.value()));
        return new SimpleUnaryOperation(name, format, renderer, simplified, precedence, function);
    }

    @Override
    public String toString() {
        return format(format, x);
    }

    @Override
    public RenderableExpression toRenderable() {
        return toRenderable(renderer, x);
    }
}
