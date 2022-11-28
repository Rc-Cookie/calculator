package com.github.rccookie.math.expr;

import com.github.rccookie.math.Complex;
import com.github.rccookie.math.Number;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

record NumericExpression(@NotNull Number value) implements Expression.Numeric {
    public NumericExpression {
        if(Arguments.checkNull(value, "value") instanceof Expression)
            throw new IllegalArgumentException("Expression not allowed");
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String name() {
        return "Numeric";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NumericExpression ne && ne.value.equals(value)) || value.equals(obj);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int precedence() {
        if(!(value instanceof Complex c) || c.im.equals(Number.ZERO()) || c.equals(Number.I()))
            return Integer.MAX_VALUE;
        String str = c.toString();
        return (str.contains("+") || str.contains("-") ? Token.PLUS : Token.MULTIPLY).precedence();
    }
}
