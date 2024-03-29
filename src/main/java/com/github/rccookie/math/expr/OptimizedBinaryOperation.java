package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

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
    public Number evaluateHalf(SymbolLookup lookup, Number ea) {
        if(ea.equals(optimize)) return ea;
        return base.evaluateHalf(lookup, ea);
    }

    @Override
    public Expression simplify() {
        Expression sa = a().simplify();
        if(sa.equals(optimize)) return sa;
        Expression sBase = base.simplify();
        if(sBase instanceof BinaryOperation b)
            return new OptimizedBinaryOperation(b, optimize);
        return sBase;
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
    public RenderableExpression toRenderable() {
        return base.toRenderable();
    }

    @Override
    public Expression a() {
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
}
