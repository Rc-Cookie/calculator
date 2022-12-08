package com.github.rccookie.math.expr;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rccookie.math.Number;
import com.github.rccookie.util.Utils;

import org.jetbrains.annotations.NotNull;

record NumbersImpl(Expression... elements) implements Expression.Numbers {

    NumbersImpl {
        if(elements.length == 1)
            throw new AssertionError();
    }

    NumbersImpl(Number... elements) {
        this(Arrays.stream(elements).map(Expression::of).toArray(Expression[]::new));
    }

    @Override
    public String toString() {
        return Arrays.stream(elements).map(e -> e.toString(precedence(), false)).collect(Collectors.joining(", "));
    }

    @NotNull
    @Override
    public Iterator<Expression> iterator() {
        return Utils.iterator(elements);
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public Expression get(int index) {
        return index < elements.length ? elements[index] : Expression.UNSPECIFIED();
    }

    @Override
    public Expression[] toArray() {
        return elements.clone();
    }

    @Override
    public Numbers evaluate(SymbolLookup lookup) {
        for(int i=0; i<elements.length; i++)
            if(!(elements[i] instanceof Constant))
                return new NumbersImpl(evaluateToArray(lookup));
        return this;
    }

    @Override
    public String name() {
        return "Numbers";
    }

    @Override
    public Number evaluate(int index, SymbolLookup lookup) {
        return Expression.evaluate(elements[index], lookup);
    }

    @Override
    public Expression simplify() {
        Expression[] simplified = new Expression[size()];
        Arrays.setAll(simplified, i -> elements[i].simplify());
        return new NumbersImpl(simplified);
    }

    @Override
    public Stream<Expression> stream() {
        return Arrays.stream(elements);
    }

    public Expression[] evaluateToArray(SymbolLookup lookup) {
        return Arrays.stream(elements).map(e -> Expression.of(Expression.evaluate(e, lookup))).toArray(Expression[]::new);
    }

    @Override
    public double toDouble(SymbolLookup lookup) {
        if(elements.length == 1)
            return elements[0].toDouble(lookup);
        throw new ArithmeticException("Cannot convert multi element list to double");
    }
}
