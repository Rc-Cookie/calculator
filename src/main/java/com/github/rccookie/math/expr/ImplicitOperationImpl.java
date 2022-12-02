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
        if(a instanceof Numeric || a instanceof Builder.VectorExpression) {
            String as = a.toString(precedence(), true), bs = b.toString(precedence(), false);
            if(endIsClear(as) || startIsClear(bs))
                return as + bs;
            return as + '\u00B7' + bs;
        }
        return format("$x(" + b + ")", a);
    }

    private static boolean endIsClear(String x) {
        return x.endsWith("]");
    }

    private static boolean startIsClear(String x) {
        char c = x.charAt(0);
        return c < '0' || c > '9';
    }

    @Override
    public Number evaluateHalf(SymbolLookup lookup, Number funcOrElse) {
        if(funcOrElse instanceof Expression.Function f)
            return f.evaluate(lookup, b.evaluate(lookup));
        return ((BinaryOperation) a.multiply(b)).evaluateHalf(lookup, funcOrElse);
    }

    @Override
    public Expression simplify() {
        Expression sa = a.simplify(), sb = b.simplify();
        if(sa instanceof Function fa)
            return new FunctionCall(fa, sb);
        if(!(sa instanceof Numeric na && sb instanceof Numeric nb))
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
