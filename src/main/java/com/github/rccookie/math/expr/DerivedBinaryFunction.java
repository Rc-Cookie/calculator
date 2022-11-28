package com.github.rccookie.math.expr;

import java.util.function.BinaryOperator;

import com.github.rccookie.math.Number;

record DerivedBinaryFunction(
        String name,
        String format,
        Expression.Function function,
        Expression b,
        int opPrecedence,
        BinaryOperator<Number> operator) implements Expression.Function, Expression.BinaryOperation {


    @Override
    public int operandCount() {
        return 2;
    }

    @Override
    public Expression a() {
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
    public Expression expr() {
        return new Body();
    }

    @Override
    public Number evaluate(SymbolLookup lookup, Number params) {
        return Expression.evaluate(operator.apply(function.evaluate(lookup, params), b), lookup);
    }

    @Override
    public Function simplify() {
        Function fs = function.simplify();
        Expression bs = b.simplify();
        if(fs.expr() instanceof Numeric fn) {
            if(bs instanceof Numeric bn)
                return new RuntimeFunction(Expression.of(operator.apply(fn.value(), bn.value())), paramNames());
            return new RuntimeFunction(new SimpleBinaryOperation(name, format, fn, bs, opPrecedence, operator), paramNames());
        }
        return new DerivedBinaryFunction(name, format, fs, bs, opPrecedence, operator);
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
    }
}
