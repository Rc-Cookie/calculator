package com.github.rccookie.math.expr;

import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

record DerivedUnaryFunction(
        String name,
        String format,
        UnaryOperator<RenderableExpression> renderer,
        Expression.Function function,
        int opPrecedence,
        UnaryOperator<Number> operator) implements Expression.UnaryFunctionOperation {

    @Override
    public int operandCount() {
        return 1;
    }

    @Override
    public Expression x() {
        return function;
    }

    @Override
    public Expression[] operands() {
        return new Expression[] { function };
    }

    @Override
    public int paramCount() {
        return function.paramCount();
    }

    @Override
    public String[] paramNames() {
        return function.paramNames();
    }

    @Override
    public String toString() {
        return (paramCount() == 1 ? paramNames()[0] : '('+String.join(",", paramNames())+')')+" -> "+expr();
    }

    @Override
    public RenderableExpression toRenderable() {
        return new RuntimeFunction(expr(), paramNames()).toRenderable();
    }

    @Override
    public Expression expr() {
        return new Body();
    }

    @Override
    public Number evaluate(SymbolLookup lookup, Number params) {
        return Expression.evaluate(operator.apply(function.evaluate(lookup, params)), lookup);
    }

    @Override
    public Function simplify() {
        Function simplified = function.simplify();
        if(simplified instanceof Constant n)
            return new RuntimeFunction(Expression.of(operator.apply(n.value())), paramNames());
        return new DerivedUnaryFunction(name, format, renderer, simplified, opPrecedence, operator);
    }

    private final class Body implements Expression {

        @Override
        public Number evaluate(SymbolLookup lookup) {
            return this;
        }

        @Override
        public Expression simplify() {
            return this;
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public Expression[] operands() {
            return new Expression[] { function };
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int precedence() {
            return opPrecedence;
        }

        @Override
        public String toString() {
            return format(format, function.expr());
        }

        @Override
        public RenderableExpression toRenderable() {
            return toRenderable(renderer, function.expr());
        }
    }
}
