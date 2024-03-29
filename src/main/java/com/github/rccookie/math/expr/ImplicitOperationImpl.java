package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

import static com.github.rccookie.math.rendering.RenderableExpression.*;

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
        if(a instanceof Constant || a instanceof Builder.VectorExpression) {
            String as = a.toString(precedence(), true), bs = b.toString(precedence(), false);
            if(endIsClear(as) || startIsClear(bs))
                return as + bs;
            return as + '\u00B7' + bs;
        }
        return format("$x(" + b + ")", a);
    }

    @Override
    public RenderableExpression toRenderable() {
        if(a instanceof Constant || a instanceof Builder.VectorExpression) {
            RenderableExpression ae = a.toRenderable(precedence(), true), be = b.toRenderable(precedence(), false);
            if(endIsClear(ae.renderInline(RenderOptions.DEFAULT)) || startIsClear(be.renderInline(RenderOptions.DEFAULT)))
                return concat(ae, be);
            return mult(ae, be);
        }
        return toRenderable(n -> call(n, par(b.toRenderable())), a);
    }

    private static boolean endIsClear(String x) {
        return x.endsWith("]");
    }

    private static boolean startIsClear(String x) {
        char c = x.charAt(0);
        return c < '0' || c > '9';
    }

    @Override
    public Number evaluateHalf(SymbolLookup lookup, Number ea) {
        if(ea instanceof Expression.Function f)
            return f.evaluate(lookup, b.evaluate(lookup));
        return ea.multiply(b.evaluate(lookup));
    }

    @Override
    public Expression simplify() {
        Expression sa = a.simplify(), sb = b.simplify();
        if(sa instanceof Function fa)
            return new FunctionCall(fa, sb);
        if(!(sa instanceof Constant na && sb instanceof Constant nb))
            return new ImplicitOperationImpl(sa, sb);
        return Expression.of(na.value().multiply(nb.value()));
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
