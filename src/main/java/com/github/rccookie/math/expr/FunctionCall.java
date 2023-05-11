package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

record FunctionCall(Expression function, Expression params)
        implements Expression.ImplicitOperation {

    @Override
    public String toString() {
        return format("$x(" + params + ")", function);
    }

    @Override
    public RenderableExpression toRenderable() {
        return toRenderable(f -> RenderableExpression.call(f, params.toRenderable()), function);
    }

    @Override
    public Number evaluateHalf(SymbolLookup lookup, Number ea) {
        return ((Expression.Function) ea).evaluate(lookup, params.evaluate(lookup));
    }

    @Override
    public Expression simplify() {
        return new FunctionCall(function.simplify(), params.simplify());
    }

    @Override
    public String name() {
        return "implicit";
    }

    @Override
    public Expression a() {
        return function;
    }

    @Override
    public Expression b() {
        return params;
    }

    @Override
    public boolean isFunctionCall(SymbolLookup lookup) {
        return true;
    }
}
