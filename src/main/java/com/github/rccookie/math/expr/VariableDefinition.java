package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;

record VariableDefinition(Expression nameExpr, String name, Expression expr) implements Expression.BinaryOperation {

    VariableDefinition(Expression name, Expression expr) {
        this(name, getName(name), expr);
    }

    @Override
    public Number evaluate(SymbolLookup c) {
        Number value = Expression.evaluate(expr, c);
        c.put(name, value);
        return value;
    }

    @Override
    public String toString() {
        return nameExpr + ":=(" + expr + ')';
    }

    @Override
    public Expression a() {
        return nameExpr;
    }

    @Override
    public Expression b() {
        return expr;
    }

    private static String getName(Expression nameExpr) {
        if(!(nameExpr instanceof Expression.Symbol s))
            throw new IllegalArgumentException("Illegal variable declaration syntax");
        return s.name();
    }
}
