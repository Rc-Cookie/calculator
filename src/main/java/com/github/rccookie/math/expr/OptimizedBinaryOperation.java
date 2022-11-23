package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;

/**
 * First evaluates the first argument. If the evaluated number is equal to
 * the specified constant, it will be returned immediately. Otherwise, the
 * second operand is evaluated and the operator is applied normally.
 *
 * @param optimize The value a should have if the operation does not need to
 *                 be performed
 */
record OptimizedBinaryOperation(BinaryOperation base, Number optimize)
        implements Expression.BinaryOperation {

    @Override
    public Number evaluate(SymbolLookup lookup) {
        Number ea = a().evaluate(lookup);
        if(ea.equals(optimize)) return ea;
        return base.evaluate(lookup);
    }

    @Override
    public String name() {
        return base.name();
    }

    @Override
    public String toString() {
        return base.toString();
    }

    @Override
    public Expression a() {
        return base.a();
    }

    @Override
    public Expression b() {
        return base.b();
    }
}
