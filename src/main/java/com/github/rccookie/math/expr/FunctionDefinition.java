package com.github.rccookie.math.expr;

import java.util.Arrays;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

import static com.github.rccookie.math.rendering.RenderableExpression.*;

record FunctionDefinition(String name, Expression signature, Function function)
        implements Expression.BinaryOperation {

    public FunctionDefinition(Expression signature, Expression body) {
        this(parseName(signature), signature, RuntimeFunction.parseDefinition(signature, body));
    }

    @Override
    public Number evaluate(SymbolLookup lookup) {
        return evaluateHalf(lookup, null);
    }

    @Override
    public Function evaluateHalf(SymbolLookup lookup, Number ignored) {
        lookup.put(name(), function);
        return function;
    }

    @Override
    public Expression simplify() {
        return new FunctionDefinition(name, signature, function.simplify());
    }

    @Override
    public Expression a() {
        return signature;
    }

    @Override
    public Function b() {
        return function;
    }

    @Override
    public int precedence() {
        return Token.DEFINE.precedence();
    }

    @Override
    public String toString() {
        return format(name + '(' + String.join(",", function.paramNames()) + ") := $x", function.expr());
    }

    @Override
    public RenderableExpression toRenderable() {
        return def(call(name, Arrays.stream(function.paramNames()).map(RenderableExpression::name).toArray(RenderableExpression[]::new)), function.expr().toRenderable());
    }

    private static String parseName(Expression signature) {
        if(!(signature instanceof ImplicitOperation o) || !(o.a() instanceof Symbol s))
            throw new MathExpressionSyntaxException("Invalid operation signature");
        return s.name();
    }

    static Expression definition(Expression signature, Expression expr) {
        if(signature instanceof Symbol)
            return new VariableDefinition(signature, expr);
        return new FunctionDefinition(signature, expr);
    }
}
