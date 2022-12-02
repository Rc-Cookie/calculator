package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;

record VariableDefinition(Expression nameExpr, String name, Expression expr) implements Expression.BinaryOperation {

    VariableDefinition(Expression name, Expression expr) {
        this(name, getName(name), expr);
    }

    @Override
    public Number evaluate(SymbolLookup lookup) {
        return evaluateHalf(lookup, null);
    }

    @Override
    public Number evaluateHalf(SymbolLookup lookup, Number ignored) {
        Number value = Expression.evaluate(expr, lookup);
        lookup.put(name, value);
        return value;
    }

    @Override
    public Expression simplify() {
        return new VariableDefinition(nameExpr, name, expr.simplify());
    }

    @Override
    public String toString() {
        return format("$1 := $2", nameExpr, expr);
    }

    @Override
    public Expression a() {
        return nameExpr;
    }

    @Override
    public Expression b() {
        return expr;
    }

    @Override
    public int precedence() {
        return Token.DEFINE.precedence();
    }

    private static String getName(Expression nameExpr) {
        if(!(nameExpr instanceof Expression.Symbol s))
            throw new IllegalArgumentException("Illegal variable declaration syntax");
        return s.name();
    }
}
