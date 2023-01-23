package com.github.rccookie.math;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.json.JsonArray;
import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class Vector implements Number {

    static {
        JsonDeserialization.register(Vector.class, json -> {
            if(!json.isArray())
                return new Vector(json.as(Number.class));
            return new Vector(json.as(Number[].class));
        });
    }

    private final Number[] components;

    public Vector(Number... components) {
        if(Arguments.checkNull(components, "components").length == 0)
            throw new IllegalArgumentException("Vector requires at least one component");
        this.components = components.clone();
    }

    private Vector(boolean ignored, Number... components) {
        if(components.length == 0)
            throw new IllegalArgumentException("Vector requires at least one component");
        this.components = components;
    }

    private Vector(Vector v, UnaryOperator<Number> operator) {
        this.components = new Number[v.components.length];
        for(int i=0; i<components.length; i++)
            components[i] = operator.apply(v.components[i]);
    }

    private Vector(Vector a, Vector b, BinaryOperator<Number> operator) {
        this.components = new Number[Math.max(a.components.length, b.components.length)];
        for(int i=0; i<components.length; i++)
            components[i] = operator.apply(a.get(i), b.get(i));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Vector v && Arrays.equals(components, v.components)) ||
               (obj instanceof Number n && Arrays.stream(components).allMatch(n::equals));
    }

    @Override
    public int hashCode() {
        return components.length == 1 ? components[0].hashCode() : Arrays.hashCode(components);
    }

    @Override
    public String toString() {
        if(components.length == 1)
            return components[0].toString();
        return Arrays.toString(components);
    }

    @Override
    public double toDouble(SymbolLookup c) {
        if(components.length == 1)
            return components[0].toDouble(c);
        throw new UnsupportedOperationException("Cannot convert multi-component vector to double");
    }

    @Override
    public Object toJson() {
        return new JsonArray((Object[]) components.clone());
    }

    @Override
    public @NotNull Vector equalTo(Number x) {
        return x instanceof Vector v ? equalTo(v) : derive(c -> c.equalTo(x));
    }

    public Vector equalTo(Vector x) {
        return derive(x, Number::equalTo);
    }

    @Override
    public @NotNull Vector lessThan(Number x) {
        return x instanceof Vector v ? lessThan(v) : derive(c -> c.lessThan(x));
    }

    public Vector lessThan(Vector x) {
        return derive(x, Number::lessThan);
    }

    @Override
    public @NotNull Vector greaterThan(Number x) {
        return x instanceof Vector v ? greaterThan(v) : derive(c -> c.greaterThan(x));
    }

    public Vector greaterThan(Vector x) {
        return derive(x, Number::greaterThan);
    }

    public Number get(int index) {
        return index < components.length ? components[index] : SymbolLookup.UNSPECIFIED;
    }

    public Number get(Number index) {
        return switch(index) {
            case Vector indices -> get(indices);
            case SimpleNumber n -> get((int) n.toDouble());
            default -> throw new UnsupportedOperationException();
        };
    }

    public Vector get(Vector indices) {
        return indices.derive(this, ($, i) -> get(i));
    }

    public Number x() {
        return components[0];
    }

    public Number y() {
        return get(1);
    }

    public Number z() {
        return get(2);
    }

    public int size() {
        return components.length;
    }

    public boolean isScalar() {
        return components.length == 1;
    }

    public boolean isZero() {
        for(Number c : components)
            if(!c.equals(Number.ZERO())) return false;
        return true;
    }

    @Override
    public @NotNull Vector add(Number x) {
        return x instanceof Vector v ? add(v) : derive(c -> c.add(x));
    }

    @NotNull
    public Vector add(Vector x) {
        return derive(x, Number::add);
    }

    @Override
    public @NotNull Vector subtract(Number x) {
        return x instanceof Vector v ? subtract(v) : derive(c -> c.subtract(x));
    }

    @NotNull
    public Vector subtract(Vector x) {
        return derive(x, Number::subtract);
    }

    @Override
    public @NotNull Vector subtractFrom(Number x) {
        return x instanceof Vector v ? subtractFrom(v) : derive(c -> c.subtractFrom(x));
    }

    @NotNull
    public Vector subtractFrom(Vector x) {
        return derive(x, Number::subtractFrom);
    }

    @Override
    @NotNull
    public Number multiply(Number x) {
        return x instanceof Vector v ? dot(v) : derive(c -> c.multiply(x));
    }

    @NotNull
    public Number dot(Vector x) {
        Number y = components[0].multiply(x.components[0]);
        for(int i=1; i<Math.min(components.length, x.components.length); i++)
            y = y.add(components[i].multiply(x.components[i]));
        return y;
    }

    @NotNull
    public Number multiplyComponentwise(Vector x) {
        return derive(x, Number::multiply);
    }

    @NotNull
    public Vector cross(Vector x) {
        if(components.length != 3 || x.components.length != 3)
            throw new ArithmeticException("Cross product only defined for 3d vectors");
        return new Vector(true,
                y().multiply(x.z()).subtract(z().multiply(x.y())),
                z().multiply(x.x()).subtract(x().multiply(x.z())),
                x().multiply(x.y()).subtract(y().multiply(x.x()))
        );
    }

    @Override
    public @NotNull Vector divide(Number x) {
        return x instanceof Vector v ? divide(v) : derive(c -> c.divide(x));
    }

    @NotNull
    public Vector divide(Vector x) {
        return derive(x, Number::divide);
    }

    @Override
    public @NotNull Vector divideOther(Number x) {
        return x instanceof Vector v ? divideOther(v) : derive(c -> c.divideOther(x));
    }

    @NotNull
    public Vector divideOther(Vector x) {
        return derive(x, Number::divideOther);
    }

    @Override
    public @NotNull Vector raise(Number x) {
        return x instanceof Vector v ? raise(v) : derive(c -> c.raise(x));
    }

    @NotNull
    public Vector raise(Vector x) {
        return derive(x, Number::raise);
    }

    @Override
    public @NotNull Vector raiseOther(Number x) {
        return x instanceof Vector v ? raiseOther(v) : derive(c -> c.raiseOther(x));
    }

    @NotNull
    public Vector raiseOther(Vector x) {
        return derive(x, Number::raiseOther);
    }

    @NotNull
    public Number sqrAbs() {
        return dot(this);
    }

    @Override
    public @NotNull Number abs() {
        return sqrAbs().sqrt();
    }

    @Override
    public @NotNull Vector negate() {
        return derive(Number::negate);
    }

    @Override
    public @NotNull Vector invert() {
        return derive(Number::invert);
    }

    public @NotNull Vector normalize() {
        if(isZero()) return this;
        return divide(abs());
    }


    public Vector derive(UnaryOperator<Number> operator) {
        return new Vector(this, operator);
    }

    public Vector derive(Vector x, BinaryOperator<Number> operator) {
        return new Vector(this, x, operator);
    }



    public static Vector asVector(Number x) {
        return x instanceof Vector v ? v : new Vector(x);
    }
}
