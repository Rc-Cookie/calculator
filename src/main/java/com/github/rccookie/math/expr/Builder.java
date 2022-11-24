package com.github.rccookie.math.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Vector;

final class Builder implements Expression {

    private final List<Expression> elements = new ArrayList<>();

    Builder(Expression first, Expression second) {
        elements.add(first);
        elements.add(second);
    }

    static Expression append(Stack<? extends Expression> stack) {
        Expression element = stack.pop();
        Expression elements = stack.pop();
        if(elements instanceof Builder b) {
            b.append(element);
            return b;
        }
        return new Builder(elements, element);
    }

    static Expression buildList(Stack<? extends Expression> stack) {
        Expression x = stack.pop();
        return x instanceof Builder b ? b.buildList() : x;
    }

    static Expression buildVector(Stack<? extends Expression> stack) {
        Expression x = stack.pop();
        return x instanceof Builder b ? b.buildVector() : x;
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
        public String toString() {
            return "[" + elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
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
