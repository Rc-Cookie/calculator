package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;

record OptimizedDerivedBinaryFunction(Expression.BinaryFunctionOperation base, Number optimize)
        implements Expression.BinaryFunctionOperation {

    @Override
    public String toString() {
        return base.toString();
    }

    @Override
    public int operandCount() {
        return base.operandCount();
    }

    @Override
    public Function a() {
        return base.a();
    }

    @Override
    public Expression b() {
        return base.b();
    }

    @Override
    public int precedence() {
        return base.precedence();
    }

    @Override
    public Expression[] operands() {
        return base.operands();
    }

    @Override
    public String name() {
        return base.name();
    }

    @Override
    public int paramCount() {
        return base.paramCount();
    }

    @Override
    public String[] paramNames() {
        return base.paramNames();
    }

    @Override
    public Expression expr() {
        return this;
    }

    @Override
    public Number evaluateHalf(SymbolLookup lookup, Number params, Number ea) {
        if(ea.equals(optimize)) return ea;
        return base.evaluateHalf(lookup, params, ea);
    }

    @Override
    public Function simplify() {
        Function sa = a().simplify();
        if(sa.expr().equals(optimize)) return sa;
        return base.simplify();
    }
}
