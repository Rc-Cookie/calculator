package com.github.rccookie.math.expr;

record FunctionDefinition(Expression signature, Function function)
        implements Expression.BinaryOperation {

    public FunctionDefinition(Expression signature, Expression body) {
        this(signature, RuntimeFunction.definition(parseName(signature), body, parseParamNames(signature)));
    }

    @Override
    public Function evaluate(SymbolLookup lookup) {
        lookup.put(name(), function);
        return function;
    }

    @Override
    public String name() {
        return function.name();
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
        return function.toString();
    }


    private static String parseName(Expression signature) {
        if(!(signature instanceof ImplicitOperation o) || !(o.a() instanceof Symbol s))
            throw new IllegalArgumentException("Invalid function signature");
        return s.name();
    }

    private static String[] parseParamNames(Expression signature) {
        ImplicitOperation o = (ImplicitOperation) signature;
        if(o.b() instanceof Symbol s)
            return new String[] { s.name() }; // f x := ...
        if(!(o.b() instanceof Numbers n))
            throw new IllegalArgumentException("Invalid function signature");
        return n.stream().map(e -> { // f(a,b,c) := ...
            if(!(e instanceof Symbol s))
                throw new IllegalArgumentException("Invalid function signature");
            return s.name();
        }).toArray(String[]::new);
    }



    static Expression definition(Expression signature, Expression expr) {
        if(signature instanceof Symbol)
            return new VariableDefinition(signature, expr);
        return new FunctionDefinition(signature, expr);
    }
}
