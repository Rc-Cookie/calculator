package com.github.rccookie.math.interpreter;

import java.util.ArrayList;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Vector;

final class Builder implements Expression {

    private final java.util.List<Number> elements = new ArrayList<>();

    Builder(Number first, Number second) {
        elements.add(first);
        elements.add(second);
    }

    void append(Number x) {
        elements.add(x);
    }

    Number buildVector() {
        if(elements.stream().noneMatch(Expression.class::isInstance))
            return new Vector(elements.toArray(Number[]::new));
        return new VectorExpression();
    }

    List buildList() {
        return new List(elements.toArray(Number[]::new));
    }

    @Override
    public Number evaluate(Calculator calculator) {
        throw new AssertionError();
    }



    final class VectorExpression implements Expression {
        @Override
        public Number evaluate(Calculator c) {
            return new Vector(elements.stream()
                    .map(expr -> Expression.evaluate(expr, c))
                    .toArray(Number[]::new));
        }
    }
}
