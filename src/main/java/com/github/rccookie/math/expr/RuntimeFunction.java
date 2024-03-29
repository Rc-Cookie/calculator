package com.github.rccookie.math.expr;

import java.util.Arrays;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

import static com.github.rccookie.math.rendering.RenderableExpression.*;

record RuntimeFunction(Expression expr, String... paramNames)
        implements Expression.Function {

    @Override
    public String toString() {
        return format((paramNames.length == 1 ? paramNames[0] : '('+String.join(",", paramNames)+')')+" -> $x", expr);
    }

    @Override
    public RenderableExpression toRenderable() {
        String[] paramNames = paramNames();
        RenderableExpression params;
        if(paramNames.length == 1)
            params = RenderableExpression.name(paramNames[0]);
        else params = list(Arrays.stream(paramNames).map(RenderableExpression::name).toArray(RenderableExpression[]::new));
        return infix(arrow(true, false), params, expr().toRenderable());
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
        throw new MathEvaluationException("Too many arguments (" + l.size() + ") applied to operation, expected " + paramNames.length);
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
    public Function simplify() {
        return new RuntimeFunction(expr.simplify(), paramNames); // Function should stay function, even if expression is constant
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
            throw new MathExpressionSyntaxException("Illegal lambda signature");
        return new RuntimeFunction(body, n.stream().map(e -> {
            if(!(e instanceof Symbol s))
                throw new MathExpressionSyntaxException("Illegal lambda signature");
            return s.name();
        }).toArray(String[]::new)); // (a,b,c) -> ...
    }

    static RuntimeFunction parseDefinition(Expression signature, Expression body) {
        if(!(signature instanceof ImplicitOperation o))
            throw new MathExpressionSyntaxException("Invalid function signature");
        if(o.b() instanceof Symbol s)
            return new RuntimeFunction(body, s.name()); // f x := ...
        if(!(o.b() instanceof Numbers n))
            throw new MathExpressionSyntaxException("Invalid function signature");
        return new RuntimeFunction(body, n.stream().map(e -> { // f(a,b,c) := ...
            if(!(e instanceof Symbol s))
                throw new MathExpressionSyntaxException("Invalid function signature");
            return s.name();
        }).toArray(String[]::new));
    }
}
