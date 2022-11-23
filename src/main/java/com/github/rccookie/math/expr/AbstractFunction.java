package com.github.rccookie.math.expr;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

import org.jetbrains.annotations.NotNull;

interface AbstractFunction extends Expression.Function {

    @Override
    @NotNull
    default Expression add(Number x) {
        return derive("+", "$1 + $2", x, Number::add);
    }

    @Override
    @NotNull
    default Expression subtract(Number x) {
        return derive("-", "$1 - $2", x, Number::subtract);
    }

    @Override
    @NotNull
    default Expression subtractFrom(Number x) {
        return derive("-", "$2 - $1", x, Number::subtractFrom);
    }

    @Override
    @NotNull
    default Expression multiply(Number x) {
        return new OptimizedDerivedBinaryFunction(
                derive("*", "($1) * ($2)", x, Number::multiply),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default Expression divide(Number x) {
        return derive("/", "($1) / ($2)", x, Number::divide);
    }

    @Override
    @NotNull
    default Expression divideOther(Number x) {
        return derive("/", "($2) / ($1)", x, Number::divideOther);
    }

    @Override
    @NotNull
    default Expression raise(Number x) {
        return new OptimizedDerivedBinaryFunction(
                derive("^", "($1)^($2)", x, Number::raise),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default Expression raiseOther(Number x) {
        return new OptimizedDerivedBinaryFunction(
                derive("^", "($2)^($1)", x, Number::raiseOther),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default Expression abs() {
        return derive("abs", "|$x|", Number::abs);
    }

    @Override
    @NotNull
    default Expression negate() {
        return derive("negate", "-($x)", Number::negate);
    }

    @Override
    @NotNull
    default Expression invert() {
        return derive("invert", "1/($y)", Number::invert);
    }

    @Override
    @NotNull
    default Expression equalTo(Number x) {
        return derive("=", "$1 = $2", x, Number::equalTo);
    }

    @Override
    @NotNull
    default Expression lessThan(Number x) {
        return derive("<", "$1 < $2", x, Number::lessThan);
    }

    @Override
    default Expression lessThanOrEqual(Number x) {
        return derive("<=", "$1 <= $2", x, Number::lessThanOrEqual);
    }

    @Override
    @NotNull
    default Expression greaterThan(Number x) {
        return derive(">", "$1 > $2", x, Number::greaterThan);
    }

    @Override
    default Expression greaterThanOrEqual(Number x) {
        return derive(">=", "$1 >= $2", x, Number::greaterThanOrEqual);
    }

    @Override
    default Function derive(String name, String format, Expression b, BinaryOperator<Number> operator) {
        if(b instanceof Function fb)
            return new FunctionBinaryOperation(name, format, this, fb, operator);
        return new DerivedBinaryFunction(name, format, this, b, operator);
    }

    @Override
    default Function derive(String name, String format, UnaryOperator<Number> operator) {
        return new DerivedUnaryFunction(name, format, this, operator);
    }
}
