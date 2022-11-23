package com.github.rccookie.math.expr;

import java.util.Arrays;

import com.github.rccookie.math.Number;

record RuntimeFunction(Expression expr, String... paramNames)
        implements AbstractFunction {

    @Override
    public String toString() {
        return (paramNames.length == 1 ? paramNames[0] : '('+String.join(",", paramNames)+')')+" -> "+expr;
    }

    @Override
    public String name() {
        return "function";
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
        throw new ArithmeticException("Too many arguments (" + l.size() + ") applied to operation, expected " + paramNames.length);
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



    static RuntimeFunction parseLambda(Expression signature, Expression body) {
        if(signature instanceof Symbol s) // x -> ...
            return new RuntimeFunction(body, s.name());
        if(!(signature instanceof Numbers n))
            throw new IllegalArgumentException("Illegal lambda signature");
        return new RuntimeFunction(body, n.stream().map(e -> {
            if(!(e instanceof Symbol s))
                throw new IllegalArgumentException("Illegal lambda signature");
            return s.name();
        }).toArray(String[]::new)); // (a,b,c) -> ...
    }

    static RuntimeFunction parseDefinition(Expression signature, Expression body) {
        if(!(signature instanceof ImplicitOperation o))
            throw new IllegalArgumentException("Invalid function signature");
        if(o.b() instanceof Symbol s)
            return new RuntimeFunction(body, s.name()); // f x := ...
        if(!(o.b() instanceof Numbers n))
            throw new IllegalArgumentException("Invalid function signature");
        return new RuntimeFunction(body, n.stream().map(e -> { // f(a,b,c) := ...
            if(!(e instanceof Symbol s))
                throw new IllegalArgumentException("Invalid function signature");
            return s.name();
        }).toArray(String[]::new));
    }
}
