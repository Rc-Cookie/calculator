package com.github.rccookie.math.expr;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import com.github.rccookie.math.Number;

record FunctionBinaryOperation(String name,
                               String format,
                               Expression.Function a,
                               Expression.Function b,
                               BinaryOperator<Number> operator) implements AbstractFunction, Expression.BinaryOperation {

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
    public Number evaluate(SymbolLookup lookup, Number params) {
        return operator.apply(a.evaluate(lookup, params), b.evaluate(lookup, params));
    }

    @Override
    public String toString() {
        String[] paramNames = paramNames();
        return (paramNames.length == 1 ? paramNames[0] : "("+String.join(",", paramNames)+")")+" -> "+expr();
    }

    private boolean equalSignatures() {
        return !(a instanceof HardcodedFunction) && !(b instanceof HardcodedFunction) &&
                Arrays.equals(a.paramNames(), b.paramNames());
    }



    private final class Body implements Expression {

        @Override
        public Number evaluate(SymbolLookup lookup) {
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
            String[] paramNames = paramNames();
            String aExprStr, bExprStr;
            if(equalSignatures()) {
                aExprStr = a.expr().toString();
                bExprStr = b.expr().toString();
            }
            else {
                String paramsStr = ")(" + String.join(",", paramNames) + ")";
                aExprStr = "(" + a + paramsStr;
                bExprStr = "(" + b + paramsStr;
            }
            return format.replace("$1", aExprStr).replace("$2", bExprStr);
        }
    }
}
