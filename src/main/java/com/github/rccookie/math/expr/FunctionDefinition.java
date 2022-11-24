package com.github.rccookie.math.expr;

record FunctionDefinition(String name, Expression signature, Function function)
        implements Expression.BinaryOperation {

    public FunctionDefinition(Expression signature, Expression body) {
        this(parseName(signature), signature, RuntimeFunction.parseDefinition(signature, body));
    }

    @Override
    public Function evaluate(SymbolLookup lookup) {
        lookup.put(name(), function);
        return function;
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
    public String toString() {
        return name + '(' + String.join(",", function.paramNames()) + ") := " + function.expr();
    }


    private static String parseName(Expression signature) {
        if(!(signature instanceof ImplicitOperation o) || !(o.a() instanceof Symbol s))
            throw new IllegalArgumentException("Invalid operation signature");
        return s.name();
    }

    static Expression definition(Expression signature, Expression expr) {
        if(signature instanceof Symbol)
            return new VariableDefinition(signature, expr);
        return new FunctionDefinition(signature, expr);
    }
}