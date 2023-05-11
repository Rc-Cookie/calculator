package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.rendering.RenderableExpression;

import org.jetbrains.annotations.NotNull;

public final class Wildcard implements Expression {

    public static final Wildcard INSTANCE = new Wildcard();

    private Wildcard() { }


    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return 12345678;
    }

    @Override
    public String toString() {
        return "?";
    }

    @Override
    public RenderableExpression toRenderable() {
        return RenderableExpression.num("?");
    }

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
        return 0;
    }

    @Override
    public Expression[] operands() {
        return new Expression[0];
    }

    @Override
    public String name() {
        return "wildcard";
    }

    @Override
    public int precedence() {
        return Integer.MIN_VALUE;
    }

    @Override
    public @NotNull BinaryOperation add(Number x) {
        return Expression.super.add(x);
    }

    @Override
    public @NotNull BinaryOperation subtract(Number x) {
        return Expression.super.subtract(x);
    }

    @Override
    public @NotNull BinaryOperation subtractFrom(Number x) {
        return Expression.super.subtractFrom(x);
    }

    @Override
    public @NotNull BinaryOperation multiply(Number x) {
        return Expression.super.multiply(x);
    }

    @Override
    public @NotNull BinaryOperation divide(Number x) {
        return Expression.super.divide(x);
    }

    @Override
    public @NotNull BinaryOperation divideOther(Number x) {
        return Expression.super.divideOther(x);
    }

    @Override
    public @NotNull BinaryOperation raise(Number x) {
        return Expression.super.raise(x);
    }

    @Override
    public @NotNull BinaryOperation raiseOther(Number x) {
        return Expression.super.raiseOther(x);
    }

    @Override
    public @NotNull UnaryOperation abs() {
        return Expression.super.abs();
    }

    @Override
    public @NotNull UnaryOperation negate() {
        return Expression.super.negate();
    }

    @Override
    public @NotNull UnaryOperation invert() {
        return Expression.super.invert();
    }

    @Override
    public @NotNull BinaryOperation equalTo(Number x) {
        return Expression.super.equalTo(x);
    }

    @Override
    public @NotNull BinaryOperation lessThan(Number x) {
        return Expression.super.lessThan(x);
    }

    @Override
    public BinaryOperation lessThanOrEqual(Number x) {
        return Expression.super.lessThanOrEqual(x);
    }

    @Override
    public @NotNull BinaryOperation greaterThan(Number x) {
        return Expression.super.greaterThan(x);
    }

    @Override
    public BinaryOperation greaterThanOrEqual(Number x) {
        return Expression.super.greaterThanOrEqual(x);
    }
}
