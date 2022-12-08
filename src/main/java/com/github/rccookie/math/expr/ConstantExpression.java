package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

record ConstantExpression(@NotNull Number value) implements Expression.Constant {
    public ConstantExpression {
        if(Arguments.checkNull(value, "value") instanceof Expression)
            throw new IllegalArgumentException("Expression not allowed");
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ConstantExpression ne && ne.value.equals(value)) || value.equals(obj);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
