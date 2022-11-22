package com.github.rccookie.math.expr;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Vector;

final class Builder implements Expression {

    private final List<Expression> elements = new ArrayList<>();

    Builder(Expression first, Expression second) {
        elements.add(first);
        elements.add(second);
    }

    void append(Expression x) {
        elements.add(x);
    }

    Expression buildVector() {
        return new VectorExpression();
    }

    Expression buildList() {
        if(elements.size() == 1)
            return elements.get(0);
        return new NumbersImpl(elements.toArray(Expression[]::new));
    }

    @Override
    public Number evaluate(SymbolLookup lookup) {
        throw new AssertionError();
    }

    @Override
    public int operandCount() {
        return elements.size();
    }

    @Override
    public Expression[] operands() {
        return elements.toArray(new Expression[0]);
    }

    @Override
    public String name() {
        return "[Builder]";
    }


    final class VectorExpression implements Expression {
        @Override
        public Number evaluate(SymbolLookup c) {
            return new Vector(elements.stream()
                    .map(expr -> Expression.evaluate(expr, c))
                    .toArray(Number[]::new));
        }

        @Override
        public int operandCount() {
            return Builder.this.operandCount();
        }

        @Override
        public Expression[] operands() {
            return Builder.this.operands();
        }

        @Override
        public String name() {
            return "Vector";
        }
    }
}
