package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;

record OptimizedDerivedBinaryFunction(Function base, Number optimize)
        implements AbstractFunction, Expression.BinaryOperation {

    OptimizedDerivedBinaryFunction {
        if(!(base instanceof BinaryOperation))
            throw new IllegalArgumentException("Binary function required");
        if(!(((BinaryOperation) base).a() instanceof Function))
            throw new IllegalArgumentException("Function is not a deriving function");
    }

    @Override
    public String toString() {
        return base.toString();
    }

    @Override
    public int operandCount() {
        return base.operandCount();
    }

    @Override
    public Expression a() {
        return ((BinaryOperation) base).a();
    }

    @Override
    public Expression b() {
        return ((BinaryOperation) base).b();
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
    public Number evaluate(SymbolLookup lookup, Number params) {
        Number ea = ((Function) a()).evaluate(lookup, params);
        if(ea.equals(optimize)) return ea;
        return base.evaluate(lookup, params);
    }
}
