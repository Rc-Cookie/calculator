package com.github.rccookie.math;

import java.math.BigDecimal;

import com.github.rccookie.json.JsonDeserialization;

import org.jetbrains.annotations.NotNull;

public interface SimpleNumber extends Number {

    Object _nothing = registerJson();
    private static Object registerJson() {
        JsonDeserialization.register(SimpleNumber.class, json -> json.as(Rational.class));
        return null;
    }


    boolean precise();

    BigDecimal toBigDecimal();

    @Override
    default @NotNull Number add(Number x) {
        return x instanceof SimpleNumber n ? add(n) : x.add(this);
    }

    SimpleNumber add(SimpleNumber x);

    @Override
    @NotNull
    default SimpleNumber add(long x) {
        return add(new Rational(x));
    }

    @Override
    @NotNull
    default SimpleNumber add(double x) {
        return add(new Rational(x));
    }

    @Override
    default @NotNull Number subtract(Number x) {
        return x instanceof SimpleNumber n ? subtract(n) : x.subtractFrom(this);
    }

    SimpleNumber subtract(SimpleNumber x);

    @Override
    @NotNull
    default SimpleNumber subtract(long x) {
        return subtract(new Rational(x));
    }

    @Override
    @NotNull
    default SimpleNumber subtract(double x) {
        return subtract(new Rational(x));
    }

    @Override
    default @NotNull Number subtractFrom(Number x) {
        return x instanceof SimpleNumber n ? subtractFrom(n) : x.subtract(this);
    }

    SimpleNumber subtractFrom(SimpleNumber x);

    @Override
    @NotNull
    default SimpleNumber subtractFrom(long x) {
        return subtractFrom(new Rational(x));
    }

    @Override
    @NotNull
    default SimpleNumber subtractFrom(double x) {
        return subtractFrom(new Rational(x));
    }

    @Override
    default @NotNull Number multiply(Number x) {
        return x instanceof SimpleNumber n ? multiply(n) : x.multiply(this);
    }

    SimpleNumber multiply(SimpleNumber x);

    @Override
    @NotNull
    default SimpleNumber multiply(long x) {
        return multiply(new Rational(x));
    }

    @Override
    @NotNull
    default SimpleNumber multiply(double x) {
        return multiply(new Rational(x));
    }

    @Override
    default @NotNull Number divide(Number x) {
        return x instanceof SimpleNumber n ? divide(n) : x.divideOther(this);
    }

    SimpleNumber divide(SimpleNumber x);

    @Override
    @NotNull
    default SimpleNumber divide(long x) {
        return divide(new Rational(x));
    }

    @Override
    @NotNull
    default SimpleNumber divide(double x) {
        return divide(new Rational(x));
    }

    @Override
    default @NotNull Number divideOther(Number x) {
        return x instanceof SimpleNumber n ? divideOther(n) : x.divide(this);
    }

    SimpleNumber divideOther(SimpleNumber x);

    @Override
    @NotNull
    default SimpleNumber divideOther(long x) {
        return divideOther(new Rational(x));
    }

    @Override
    @NotNull
    default SimpleNumber divideOther(double x) {
        return divideOther(new Rational(x));
    }

    @Override
    @NotNull SimpleNumber abs();

    @Override
    @NotNull SimpleNumber negate();

    @Override
    @NotNull SimpleNumber invert();

    @Override
    default @NotNull Number equalTo(Number x) {
        return x instanceof SimpleNumber n ? equalTo(n) : x.equalTo(this);
    }

    SimpleNumber equalTo(SimpleNumber x);

    @Override
    default @NotNull Number lessThan(Number x) {
        return x instanceof SimpleNumber n ? lessThan(n) : x.greaterThanOrEqual(this);
    }

    SimpleNumber lessThan(SimpleNumber x);

    @Override
    default @NotNull Number greaterThan(Number x) {
        return x instanceof SimpleNumber n ? greaterThan(n) : x.lessThanOrEqual(this);
    }

    SimpleNumber greaterThan(SimpleNumber x);

    default SimpleNumber lessThanOrEqual(SimpleNumber x) {
        return Number.ONE().subtract(greaterThan(x));
    }

    default SimpleNumber greaterThanOrEqual(SimpleNumber x) {
        return Number.ONE().subtract(lessThan(x));
    }
}
