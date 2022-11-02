package com.github.rccookie.math.interpreter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;
import com.github.rccookie.util.Utils;

import org.jetbrains.annotations.NotNull;

record List(Number... elements) implements Expression, Iterable<Number> {

    List {
        if(elements.length == 0)
            throw new AssertionError();
    }

    @Override
    public String toString() {
        return "List" + Arrays.toString(elements).replace('[', '(').replace(']', ')');
    }

    @NotNull
    @Override
    public Iterator<Number> iterator() {
        return Utils.iterator(elements);
    }

    public int size() {
        return elements.length;
    }

    public Number get(int index) {
        return index < elements.length ? elements[index] : Expression.UNSPECIFIED();
    }

    @Override
    public List evaluate(Calculator calculator) {
        return new List(evaluateToArray(calculator));
    }

    public Number evaluate(Calculator calculator, int index) {
        return Expression.evaluate(get(index), calculator);
    }

    public Number[] evaluateToArray(Calculator calculator) {
        return Arrays.stream(elements).map(e -> Expression.evaluate(e, calculator)).toArray(Number[]::new);
    }

    @Override
    public Expression apply(String format, UnaryOperator<Number> function) {
        throw new ArithmeticException("List arithmetics");
    }

    @Override
    public Expression apply(String format, Number x, BinaryOperator<Number> function) {
        throw new ArithmeticException("List arithmetics");
    }
}
