package com.github.rccookie.math.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Vector;

import org.jetbrains.annotations.Nullable;

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
        return new VectorExpression(elements.toArray(Expression[]::new));
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
    public Expression simplify() {
        return this;
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

    @Override
    public int precedence() {
        return Integer.MIN_VALUE;
    }


    record VectorExpression(Expression[] elements) implements Expression {
        @Override
        public Number evaluate(SymbolLookup lookup) {
            Number[] evaluated = new Number[elements.length];
            Arrays.setAll(evaluated, i -> elements[i].evaluate(lookup));

            Expression es = toVectorExpressionIfNeeded(evaluated);
            if(es != null) return es;
            return new Vector(evaluated);
        }

        @Override
        public Expression simplify() {
            Number[] simplified = new Number[elements.length];
            Arrays.setAll(simplified, i -> elements[i].simplify());

            Expression es = toVectorExpressionIfNeeded(simplified);
            if(es != null) return es;
            return Expression.of(new Vector(simplified));
        }

        @Nullable
        private Expression toVectorExpressionIfNeeded(Number[] simplified) {
            for(int i=0; i<simplified.length; i++) {
                if(simplified[i] instanceof Expression) {
                    Expression[] es = new Expression[simplified.length];
                    System.arraycopy(simplified, 0, es, 0, es.length);
                    return new VectorExpression(es);
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return Arrays.toString(elements);
        }

        @Override
        public int operandCount() {
            return elements.length;
        }

        @Override
        public Expression[] operands() {
            return elements.clone();
        }

        @Override
        public String name() {
            return "Vector";
        }

        @Override
        public int precedence() {
            return Integer.MIN_VALUE;
        }
    }
}
