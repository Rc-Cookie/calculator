package com.github.rccookie.math;

import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.math.expr.Functions;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.math.rendering.RenderableExpression;

import org.jetbrains.annotations.NotNull;

public class Complex implements Number {

    static {
        JsonDeserialization.register(Complex.class, json -> {
            if(json.containsKey("re")) {
                return new Complex(
                        json.get("re").as(SimpleNumber.class),
                        json.get("im").or(SimpleNumber.class, Rational.ONE)
                );
            }
            return new Complex(json.as(SimpleNumber.class));
        });
    }

    public static final Complex ZERO = new Complex(Number.ZERO());
    public static final Complex ONE = new Complex(Number.ONE());
    public static final Complex I = new Complex(Number.ZERO(), Number.ONE());


    public final SimpleNumber re, im;

    public Complex(SimpleNumber re, SimpleNumber im) {
        this.re = re;
        this.im = im;
    }

    public Complex(SimpleNumber re) {
        this(re, Number.ZERO());
    }


    @Override
    public String toString() {
        if(im.equals(Number.ZERO()))
            return re.toString();
        if(re.equals(Number.ZERO()))
            return im.equals(Number.ONE()) ? "i" : im.equals(Number.MINUS_ONE()) ? "-i" : im + "i";
        if(im.equals(Number.ONE()))
            return re + "+i";
        if(im.equals(Number.MINUS_ONE()))
            return re + "-i";
        String imStr = im + "i";
        return imStr.charAt(0) == '-' ? re + imStr : re + "+" + imStr;
    }

    @Override
    public Object toJson() {
        return new JsonObject("re", re, "im", im);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Complex c)
            return re.equals(c.re) && im.equals(c.im);
        return im.equals(Number.ZERO()) && re.equals(obj);
    }

    @Override
    public int hashCode() {
        if(im.equals(Number.ZERO()))
            return re.hashCode();
        return re.hashCode() ^ im.hashCode();
    }

    @Override
    public boolean isZero() {
        return re.isZero() && im.isZero();
    }

    @Override
    public boolean isOne() {
        return re.isOne() && im.isZero(); // TODO: this is bad
    }

    public boolean isReal() {
        return im.equals(Number.ZERO());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean precise() {
//        return !(re instanceof Real r1 && !r1.precise) &&
//               !(im instanceof Real r2 && !r2.precise);
        return re.precise() && im.precise();
    }

    public Complex normalize() {
        if(re.equals(Number.ZERO()) && im.equals(Number.ZERO()))
            return this;
        return (Complex) divide(abs());
    }

    @Override
    public @NotNull Number add(Number x) {
        return switch(x) {
            case SimpleNumber n -> new Complex(re.add(n), im);
            case Complex c -> new Complex(re.add(c.re), im.add(c.im));
            default -> x.add(this);
        };
    }

    @Override
    public @NotNull Number subtract(Number x) {
        return switch(x) {
            case SimpleNumber n -> new Complex(re.subtract(n), im);
            case Complex c -> new Complex(re.subtract(c.re), im.subtract(c.im));
            default -> x.subtractFrom(this);
        };
    }

    @Override
    public @NotNull Number subtractFrom(Number x) {
        return switch(x) {
            case SimpleNumber n -> new Complex(re.subtractFrom(n), im.negate());
            case Complex c -> new Complex(re.subtractFrom(c.re), im.subtractFrom(c.im));
            default -> x.subtract(this);
        };
    }

    @Override
    public @NotNull Number multiply(Number x) {
        // (a+ib)(c+id) = ac + iad + ibc - bd = ac-bd + i(ad+bc)
        return switch(x) {
            case SimpleNumber n -> new Complex(re.multiply(n), im.multiply(n));
            case Complex c -> new Complex(
                    re.multiply(c.re).subtract(im.multiply(c.im)),
                    re.multiply(c.im).add(im.multiply(c.re)));
            default -> x.multiply(this);
        };
    }

    @Override
    public @NotNull Number divide(Number x) {
        return switch(x) {
            case SimpleNumber n -> new Complex(re.divide(n), im.divide(n));
            case Complex c -> divide(c);
            default -> x.divideOther(this);
        };
    }

    public @NotNull Complex divide(Complex x) {
        // (a+ib)/(c+id) = (ac+bd)/(c²+d²) + i(bc-ad)/(c²+d²)
        // b = 0   ->    = (ac+0d)/(c²+d²) + i(0c-ad)/(c²+d²) = ac/(c²+d²) - iad/(c²+d²)
        SimpleNumber d = x.sqrAbs();
        return new Complex(
                re.multiply(x.re).add(im.multiply(x.im)).divide(d),
                im.multiply(x.re).subtract(re.multiply(x.im)).divide(d));
    }

    @Override
    public @NotNull Number divideOther(Number x) {
        return switch(x) {
            case SimpleNumber n -> new Complex(n).divide(this);
            case Complex c -> c.divide(this);
            default -> x.divide(this);
        };
    }

    @Override
    public @NotNull Number raise(Number x) {
        return switch(x) {
            case SimpleNumber n -> fromPolar(theta().multiply(n), (SimpleNumber) abs().raise(n));
            case Complex c -> Functions.exp(Functions.ln(abs()).multiply(c)
                    .add(I.multiply(theta()).multiply(c)));
            default -> x.raiseOther(this);
        };
    }

    @Override
    public @NotNull Number raiseOther(Number base) {
        return switch(base) {
            case SimpleNumber n -> {
                if(base.equals(Number.ZERO()))
                    yield equalTo(ZERO);
                if(base.lessThan(Number.ZERO()).equals(Number.ONE()))
                    yield raiseOther(base.negate()).invert();
                SimpleNumber theta = (SimpleNumber) im.multiply(Functions.ln(n));
                yield fromPolar(theta).multiply(n.raise(re));
            }
            case Complex c -> c.raise(this);
            default -> base.raise(this);
        };
    }

    public @NotNull Complex conjugate() {
        return new Complex(re, im.negate());
    }

    public @NotNull SimpleNumber theta() {
        if(equals(ZERO))
            throw new ArithmeticException("Argument of 0 is undefined");
        return (SimpleNumber) Functions.atan2(im, re);
    }

    @Override
    public @NotNull SimpleNumber abs() {
        return (SimpleNumber) sqrAbs().sqrt(); // Always non-negative root
    }

    public @NotNull SimpleNumber sqrAbs() {
        return re.multiply(re).add(im.multiply(im));
    }

    @Override
    public @NotNull Complex negate() {
        return new Complex(re.negate(), im.negate());
    }

    @Override
    public @NotNull Number invert() {
        SimpleNumber sqrAbs = sqrAbs();
        return new Complex(re.divide(sqrAbs), im.negate().divide(sqrAbs));
    }

    @Override
    public @NotNull Number equalTo(Number x) {
        return x instanceof Complex c && re.equals(c.re) && im.equals(c.im) ? Number.ONE() : Number.ZERO();
    }

    @Override
    public @NotNull Number lessThan(Number x) {
        if(im.equals(Number.ZERO()))
            return re.lessThan(x);
        throw new ArithmeticException("Cannot compare complex numbers");
    }

    @Override
    public @NotNull Number greaterThan(Number x) {
        if(im.equals(Number.ZERO()))
            return re.greaterThan(x);
        throw new ArithmeticException("Cannot compare complex numbers");
    }

    @Override
    public double toDouble(SymbolLookup lookup) {
        if(im.equals(Number.ZERO()))
            return re.toDouble(lookup);
        throw new ArithmeticException("Cannot convert complex to double");
    }

    @Override
    public RenderableExpression toRenderable() {
        return RenderableExpression.plus(re.toRenderable(), im.toRenderable());
    }

    public static Complex fromPolar(SimpleNumber theta) {
        return new Complex(Functions.cos(theta), Functions.sin(theta));
    }

    public static Complex fromPolar(SimpleNumber theta, SimpleNumber magnitude) {
        return new Complex(Functions.cos(theta).multiply(magnitude),
                           Functions.sin(theta).multiply(magnitude));
    }
}
