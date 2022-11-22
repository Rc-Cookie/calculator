package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;

record FunctionCall(Expression function, Expression params)
        implements Expression.ImplicitOperation {

    @Override
    public String toString() {
        return "(" + function + ")(" + params + ")";
    }

    @Override
    public Number evaluate(SymbolLookup lookup) {
        return ((Expression.Function) function.evaluate(lookup)).evaluate(lookup, params.evaluate(lookup));
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
