package com.github.rccookie.math.interpreter;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

import org.jetbrains.annotations.NotNull;

interface Expression extends Number {

    static Expression ZERO() { return of(Number.ZERO()); }
    static Expression UNSPECIFIED() { return Calculator.UNSPECIFIED_EXPR; }


    Number evaluate(Calculator calculator);


    @Override
    default @NotNull Expression add(Number x) {
        return apply("$1 + $2", x, Number::add);
    }

    @Override
    @NotNull
    default Expression subtract(Number x) {
        return apply("$1 - $2", x, Number::subtract);
    }

    @Override
    @NotNull
    default Expression subtractFrom(Number x) {
        return apply("$2 - $1", x, Number::subtractFrom);
    }

    @Override
    @NotNull
    default Expression multiply(Number x) {
        return apply("($1)*($2)", x, Number::multiply);
    }

    @Override
    @NotNull
    default Expression divide(Number x) {
        return apply("($1)/($2)", x, Number::divide);
    }

    @Override
    @NotNull
    default Expression divideOther(Number x) {
        return apply("($2)/($1)", x, Number::divideOther);
    }

    @Override
    @NotNull
    default Expression raise(Number x) {
        return apply("($1)^($2)", x, Number::raise);
    }

    @Override
    @NotNull
    default Expression raiseOther(Number x) {
        return apply("($2)^($1)", x, Number::raiseOther);
    }

    @Override
    @NotNull
    default Expression abs() {
        return apply("|$x|", Number::negate);
    }

    @Override
    @NotNull
    default Expression negate() {
        return apply("-($x)", Number::negate);
    }

    @Override
    @NotNull
    default Expression invert() {
        return apply("1/($y)", Number::invert);
    }

    @Override
    @NotNull
    default Expression equalTo(Number x) {
        return apply("$1 = $2", x, Number::equalTo);
    }

    @Override
    @NotNull
    default Expression lessThan(Number x) {
        return apply("$1 < $2", x, Number::lessThan);
    }

    @Override
    default Number lessThanOrEqual(Number x) {
        return apply("$1 <= $2", x, Number::lessThanOrEqual);
    }

    @Override
    @NotNull
    default Expression greaterThan(Number x) {
        return apply("$1 > $2", x, Number::greaterThan);
    }

    @Override
    default Number greaterThanOrEqual(Number x) {
        return apply("$1 >= $2", x, Number::greaterThanOrEqual);
    }


    default Expression apply(String format, Number x, BinaryOperator<Number> function) {
        return new ExpressionBinaryOperation(format, this, x, function);
    }

    default Expression apply(String format, UnaryOperator<Number> function) {
        return new ExpressionUnaryOperation(format, this, function);
    }

    @Override
    default double toDouble() {
        throw new UnsupportedOperationException("Cannot convert expression to double");
    }



    static Expression of(Number x) {
        return x instanceof Expression e ? e : new Expression() {
            @Override
            public Number evaluate(Calculator calculator) {
                return x;
            }

            @Override
            public String toString() {
                return x.toString();
            }
        };
    }

    static Number evaluate(Number x, Calculator e) {
        return x instanceof Expression expr ? expr.evaluate(e) : x;
    }
}
