package com.github.rccookie.math.expr;

import java.util.Arrays;

import com.github.rccookie.math.Number;

record RuntimeFunction(String name, boolean lambda, Expression expr, String[] paramNames)
        implements AbstractFunction {

    @Override
    public String toString() {
        return (paramNames.length == 1 ? paramNames[0] : '('+String.join(", ", paramNames)+')')+" -> "+expr;
//        String params = lambda && paramNames.length == 1 ? paramNames[0] : '('+String.join(", ", paramNames)+')';
//        return lambda ? params + " -> " + expr : name + params + " := " + expr;
    }

    @Override
    public int paramCount() {
        return paramNames.length;
    }

    @Override
    public String[] paramNames() {
        return paramNames.clone();
    }

    @Override
    public Number evaluate(SymbolLookup lookup, Number params) {
        if(!(params instanceof Numbers l))
            return evaluateFunction(lookup, params);
        if(l.size() <= paramNames.length)
            return evaluateFunction(lookup, Arrays.stream(l.toArray()).map(x -> Expression.evaluate(x,lookup)).toArray(Number[]::new));
        if(paramNames.length == 1) {
            Expression[] results = new Expression[l.size()];
            for (int i = 0; i < results.length; i++)
                results[i] = Expression.of(evaluateFunction(lookup, l.evaluate(i, lookup)));
            return new NumbersImpl(results);
        }
        throw new ArithmeticException("Too many arguments (" + l.size() + ") applied to function"+(lambda?"":" "+name)+", expected " + paramNames.length);
    }

    private Number evaluateFunction(SymbolLookup lookup, Number... params) {
        assert params.length <= paramNames.length;

        for (int i = 0; i < paramNames.length; i++)
            lookup.pushLocal(paramNames[i], i < params.length ? params[i] : SymbolLookup.UNSPECIFIED);

        Number result = expr.evaluate(lookup);

        for (String paramName : paramNames)
            lookup.popLocal(paramName);
        return result;
    }

    @Override
    public int operandCount() {
        return 1;
    }

    @Override
    public Expression[] operands() {
        return new Expression[] { expr };
    }



    static RuntimeFunction definition(String name, Expression expr, String... paramNames) {
        return new RuntimeFunction(name, false, expr, paramNames);
    }

    static RuntimeFunction lambda(Expression expr, String... paramNames) {
        return new RuntimeFunction(null, true, expr, paramNames);
    }

    static RuntimeFunction lambda(Expression signature, Expression body) {
        if(signature instanceof Symbol s) // x -> ...
            return lambda(body, s.name());
        if(!(signature instanceof Numbers n))
            throw new IllegalArgumentException("Illegal lambda signature");
        return lambda(body, n.stream().map(e -> {
            if(!(e instanceof Symbol s))
                throw new IllegalArgumentException("Illegal lambda signature");
            return s.name();
        }).toArray(String[]::new)); // (a,b,c) -> ...
    }
}
