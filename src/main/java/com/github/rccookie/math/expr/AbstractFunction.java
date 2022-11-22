package com.github.rccookie.math.expr;

import java.util.function.BiFunction;

import com.github.rccookie.math.Number;

interface AbstractFunction extends Expression.Function {

    @Override
    default Expression apply(BiFunction<Expression, Expression, BinaryOperation> operation, Number b) {
        BinaryOperation op = operation.apply(expr(), Expression.of(b));
        return RuntimeFunction.lambda(op, paramNames());
    }

    @Override
    default Expression applyInverse(BiFunction<Expression, Expression, BinaryOperation> operation, Number a) {
        BinaryOperation op = operation.apply(Expression.of(a), expr());
        return RuntimeFunction.lambda(op, paramNames());
    }

    @Override
    default Expression apply(java.util.function.Function<Expression, UnaryOperation> operation) {
        UnaryOperation op = operation.apply(expr());
        return RuntimeFunction.lambda(op, paramNames());
    }
}
