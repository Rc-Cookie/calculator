package com.github.rccookie.math.expr;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

record BinaryFunctionOperationImpl(String name,
                                   String format,
                                   BinaryOperator<RenderableExpression> renderer,
                                   Boolean aIsLeft,
                                   Expression.Function a,
                                   Expression.Function b,
                                   int opPrecedence,
                                   BinaryOperator<Number> operator) implements Expression.BinaryFunctionOperation {

    @Override
    public int paramCount() {
        if(a.paramCount() == 1 || b.paramCount() == 1)
            return Math.max(a.paramCount(), b.paramCount());
        return Math.min(a.paramCount(), b.paramCount());
    }

    @Override
    public String[] paramNames() {
        String[] aParams = a.paramNames();
        if(Arrays.equals(aParams, b.paramNames()))
            return aParams;
        int c = paramCount();
        if(c == 0) return new String[0];
        if(c == 1) return new String[] { "x" };
        if(c == 2) return new String[] { "a", "b" };
        return Stream.iterate(1, i->i+1).limit(c).map(i -> "x"+i).toArray(String[]::new);
    }

    @Override
    public Expression expr() {
        return new Body();
    }

    @Override
    public Number evaluateHalf(SymbolLookup lookup, Number params, Number ea) {
        return operator.apply(ea, b.evaluate(lookup, params));
    }

    @Override
    public String toString() {
        String[] paramNames = paramNames();
        return (paramNames.length == 1 ? paramNames[0] : "("+String.join(",", paramNames)+")")+" -> "+expr();
    }

    @Override
    public RenderableExpression toRenderable() {
        return new RuntimeFunction(expr(), paramNames()).toRenderable();
    }

    private boolean equalSignatures() {
        return !(a instanceof HardcodedFunction) && !(b instanceof HardcodedFunction) &&
                Arrays.equals(a.paramNames(), b.paramNames());
    }

    @Override
    public Function simplify() {
        Function as = a.simplify(), bs = b.simplify();
        if(bs.expr() instanceof Constant bn) {
            if(as.expr() instanceof Constant an)
                return new RuntimeFunction(Expression.of(operator.apply(an.value(), bn.value())), paramNames());
            return as.derive(name, format, renderer, aIsLeft, as, opPrecedence, operator);
        }
        if(as.expr() instanceof Constant an)
            return bs.derive(name, formatFlipped(), (a,b) -> renderer.apply(b,a), !aIsLeft, an.value(), opPrecedence, (b,a) -> operator.apply(a,b));
        return new BinaryFunctionOperationImpl(name, format, renderer, aIsLeft, as, bs, opPrecedence, operator);
    }

    private String formatFlipped() {
        return format.replace("$1", "$3").replace("$2", "$1").replace("$3", "$2");
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
            return new Expression[] { a, b };
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String toString() {
            if(equalSignatures())
                return format(format, a.expr(), b.expr());

            String[] paramNames = paramNames();
            Expression params;
            if(paramNames.length == 1)
                params = Symbol.of(paramNames[0]);
            else {
                Expression[] elements = new Expression[paramNames.length];
                Arrays.setAll(elements, i -> Symbol.of(paramNames[i]));
                params = Numbers.of(elements);
            }
            return format(format, new FunctionCall(a, params), new FunctionCall(b, params));
        }

        @Override
        public int precedence() {
            return opPrecedence;
        }

        @Override
        public RenderableExpression toRenderable() {
            if(equalSignatures())
                return toRenderable(renderer, aIsLeft, a, b);

            String[] paramNames = paramNames();
            Expression params;
            if(paramNames.length == 1)
                params = Symbol.of(paramNames[0]);
            else {
                Expression[] elements = new Expression[paramNames.length];
                Arrays.setAll(elements, i -> Symbol.of(paramNames[i]));
                params = Numbers.of(elements);
            }
            return toRenderable(renderer, aIsLeft, new FunctionCall(a, params), new FunctionCall(b, params));
        }
    }
}
