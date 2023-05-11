package com.github.rccookie.math.expr;

import java.util.function.BinaryOperator;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

record DerivedBinaryFunction(
        String name,
        String format,
        BinaryOperator<RenderableExpression> renderer,
        boolean fIsLeft,
        Expression.Function function,
        Expression b,
        int opPrecedence,
        BinaryOperator<Number> operator) implements Expression.BinaryFunctionOperation {


    @Override
    public int operandCount() {
        return 2;
    }

    @Override
    public Function a() {
        return function;
    }

    @Override
    public Expression[] operands() {
        return new Expression[] { function, b };
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
    public Number evaluateHalf(SymbolLookup lookup, Number params, Number ea) {
        return /*Expression.evaluate(*/operator.apply(ea, b.evaluate(lookup));//, lookup);
    }

    @Override
    public Function simplify() {
        Function fs = function.simplify();
        Expression bs = b.simplify();
        if(fs.expr() instanceof Constant fn) {
            if(bs instanceof Constant bn)
                return new RuntimeFunction(Expression.of(operator.apply(fn.value(), bn.value())), paramNames());
            return new RuntimeFunction(new SimpleBinaryOperation(name, format, renderer, fIsLeft, fn, bs, opPrecedence, operator), paramNames());
        }
        return new DerivedBinaryFunction(name, format, renderer, fIsLeft, fs, bs, opPrecedence, operator);
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
            return 2;
        }

        @Override
        public Expression[] operands() {
            return new Expression[] { function, b };
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
            return format(format, function.expr(), b);
        }

        @Override
        public RenderableExpression toRenderable() {
            return toRenderable(renderer, fIsLeft, function.expr(), b);
        }
    }
}
