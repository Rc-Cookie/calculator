package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;

record ImplicitOperationImpl(Expression a, Expression b)
        implements Expression.ImplicitOperation {

    @Override
    public String name() {
        return "implicit";
    }

    public String name(SymbolLookup lookup) {
        Number name = Expression.evaluate(a, lookup);
        return name instanceof Function f ? f.name() : "*";
    }

    @Override
    public boolean isFunctionCall(SymbolLookup lookup) {
        return Expression.evaluate(a, lookup) instanceof Expression.Function;
    }

    @Override
    public String toString() {
        return "(" + a + ")(" + b + ")";
    }

    @Override
    public Number evaluate(SymbolLookup lookup) {
        Number name = Expression.evaluate(a, lookup);
        Number params = Expression.evaluate(this.b, lookup);
        if(name instanceof Expression.Function f)
            return f.evaluate(lookup, params);
        return name.multiply(params);
    }

    @Override
    public int operandCount() {
        return 2;
    }

    @Override
    public Expression[] operands() {
        return new Expression[] { a,b };
    }
}
