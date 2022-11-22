package com.github.rccookie.math.expr;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

record HardcodedFunction(String name, BiFunction<SymbolLookup,Number[],Number> function, String... paramNames)
        implements AbstractFunction {

    HardcodedFunction(String name, java.util.function.Function<Number[],Number> function, String... paramNames) {
        this(name, (l,p) -> function.apply(p), paramNames);
    }

    HardcodedFunction(String name, String paramNameA, String paramNameB, BinaryOperator<Number> function) {
        this(name, p -> function.apply(p[0], p[1]), paramNameA, paramNameB);
    }

    HardcodedFunction(String name, BinaryOperator<Number> function) {
        this(name, "a", "b", function);
    }

    HardcodedFunction(String name, String paramName, UnaryOperator<Number> function) {
        this(name, p -> function.apply(p[0]), paramName);
    }

    HardcodedFunction(String name, UnaryOperator<Number> function) {
        this(name, "x", function);
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
    public String toString() {
        return name;
    }

    @Override
    public Expression expr() {
        return this;
    }

    @Override
    public Number evaluate(SymbolLookup lookup, Number params) {
        if(!(params instanceof Numbers l))
            return function.apply(lookup, new Number[] { params });
        if(l.size() <= paramCount())
            return function.apply(lookup, l.stream().map(e -> e.evaluate(lookup)).toArray(Number[]::new));
        if(paramCount() == 1) {
            Expression[] results = new Expression[l.size()];
            for (int i = 0; i < results.length; i++)
                results[i] = Expression.of(function.apply(lookup, new Number[] { l.evaluate(i, lookup) }));
            return new NumbersImpl(results);
        }
        throw new ArithmeticException("Too many arguments (" + l.size() + ") applied to function "+name+", expected "+paramCount());
    }

    @Override
    public int operandCount() {
        return 0;
    }

    @Override
    public Expression[] operands() {
        return new Expression[0];
    }
}
