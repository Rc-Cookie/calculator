package com.github.rccookie.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import com.github.rccookie.math.calculator.Calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Rational implements Number {


    public static final Rational ZERO = new Rational(0);
    public static final Rational ONE = new Rational(1);
    public static final Rational TWO = new Rational(2);
    public static final Rational HALF = new Rational(1,2);
    public static final Rational MINUS_ONE = new Rational(-1);



    @NotNull
    public final BigInteger n,d;

    public Rational(long n) {
        this(BigInteger.valueOf(n));
    }

    public Rational(BigInteger n) {
        this(n, 1);
    }

    public Rational(long n, long d) {
        this(BigInteger.valueOf(n), d);
    }

    public Rational(long n, BigInteger d) {
        this(BigInteger.valueOf(n), d);
    }

    public Rational(BigInteger n, long d) {
        this(n, BigInteger.valueOf(d));
    }

    public Rational(BigInteger n, BigInteger d) {
        if(d.equals(BigInteger.ZERO))
            throw new ArithmeticException("Division by zero");
        if(n.equals(BigInteger.ZERO)) {
            this.n = BigInteger.ZERO;
            this.d = BigInteger.ONE;
        }
        else if(n.equals(d))
            this.n = this.d = BigInteger.ONE;
        else {
            BigInteger gcd = n.abs().gcd(d.abs()).multiply(d.compareTo(BigInteger.ZERO) > 0 ? BigInteger.ONE : BigInteger.ONE.negate());
            this.n = n.divide(gcd);
            this.d = d.divide(gcd);
        }
    }

    @Override
    public String toString() {
        return n + (d.equals(BigInteger.ONE) ? "" : "/"+d);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Rational f && n.equals(f.n) && d.equals(f.d)) ||
               (obj instanceof Vector v && v.isScalar() && equals(v.get(0))) ||
               (obj instanceof Real d && new Real(this).equals(d));
    }

    @Override
    public int hashCode() {
        return new Real(this).hashCode(); // Match with equal decimal
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    public double toDouble(@Nullable Calculator ignored) {
        return toDouble();
    }

    public double toDouble() {
        return new Real(this).toDouble();
    }

    @Override
    public @NotNull Number equalTo(Number x) {
        return x instanceof Rational f ? equalTo(f) : x.equalTo(this);
    }

    public Rational equalTo(Rational x) {
        return n.equals(x.n) && d.equals(x.d) ? ONE : ZERO;
    }

    @Override
    public @NotNull Number lessThan(Number x) {
        return x instanceof Rational f ? lessThan(f) : x.greaterThanOrEqual(this);
    }

    public Rational lessThan(Rational x) {
        return n.multiply(x.d).compareTo(x.n.multiply(d)) < 0 ? ONE : ZERO;
    }

    @Override
    public @NotNull Number greaterThan(Number x) {
        return x instanceof Rational f ? greaterThan(f) : x.lessThanOrEqual(this);
    }

    public Rational greaterThan(Rational x) {
        return n.multiply(x.d).compareTo(x.n.multiply(d)) > 0 ? ONE : ZERO;
    }

    @Override
    public @NotNull Number add(Number x) {
        return x instanceof Rational f ? add(f) : x.add(this);
    }

    @NotNull
    public Rational add(Rational x) {
        return new Rational(n.multiply(x.d).add(x.n.multiply(d)), d.multiply(x.d));
    }

    @Override
    public @NotNull Number subtract(Number x) {
        return x instanceof Rational f ? subtract(f) : x.subtractFrom(this);
    }

    @NotNull
    public Rational subtract(Rational x) {
        return new Rational(n.multiply(x.d).subtract(x.n.multiply(d)), d.multiply(x.d));
    }

    @Override
    public @NotNull Number subtractFrom(Number x) {
        return x.subtract(this);
    }

    @Override
    public @NotNull Number multiply(Number x) {
        return x instanceof Rational f ? multiply(f) : x.multiply(this);
    }

    @NotNull
    public Rational multiply(Rational x) {
        return new Rational(n.multiply(x.n), d.multiply(x.d));
    }

    @Override
    public @NotNull Number divide(Number x) {
        return x instanceof Rational f ? divide(f) : x.divideOther(this);
    }

    @NotNull
    public Rational divide(Rational x) {
        return new Rational(n.multiply(x.d), d.multiply(x.n));
    }

    @Override
    public @NotNull Number divideOther(Number x) {
        return x.divide(this);
    }

    @Override
    public @NotNull Number raise(Number x) {
        return x instanceof Rational f ? raise(f) : x.raiseOther(this);
    }

    @NotNull
    public Number raise(Rational x) {
        if(Objects.equals(x.n, x.d)) return ONE;
        if(x.n.compareTo(BigInteger.ZERO) < 0) return raise(x.negate()).invert();
        if(x.n.equals(BigInteger.ONE)) {
            if(x.d.equals(BigInteger.ONE)) return this;
            if(x.d.equals(BigInteger.TWO)) {
                BigDecimal nd = new BigDecimal(n), dd;
                BigDecimal sqrtN = nd.sqrt(Real.context), sqrtD;
                if(sqrtN.round(Real.context).equals(sqrtN) && sqrtN.multiply(sqrtN, Real.context).equals(nd) &&
                        (sqrtD = (dd = new BigDecimal(d)).sqrt(Real.context)).round(Real.context).equals(sqrtD) &&
                        sqrtD.multiply(sqrtD, Real.context).equals(dd))
                    return new Rational(sqrtN.toBigInteger(), sqrtD.toBigInteger());
            }
        }
        else if(x.d.equals(BigInteger.ONE) && x.n.compareTo(BigInteger.valueOf(1000)) < 0)
            return raise(x.n.intValue());
        return new Real(this, false).raise(new Real(x, false));
    }

    public @NotNull Number raise(int x) {
        if(x == 0) return ONE;
        if(x < 0) return raise(-x).invert();
        return new Rational(n.pow(x), d.pow(x));
    }

    @Override
    public @NotNull Number raiseOther(Number x) {
        return x.raise(this);
    }

    @Override
    public @NotNull Number abs() {
        return n.compareTo(BigInteger.ZERO) < 0 ? new Rational(n.negate(),d) : this;
    }

    @Override
    public @NotNull Number negate() {
        return new Rational(n.negate(),d);
    }

    @Override
    public @NotNull Number invert() {
        return n.equals(d) ? this : new Rational(d,n);
    }



    public static Number tryFromDecimal(Real x) {
        Rational f = fromDecimal(x);
        return f != null ? f : x;
    }

    public static Rational fromDecimal(Real decimal) {
        return fromDecimal(decimal.value);
    }
    public static Rational fromDecimal(double value) {
        return fromDecimal(new BigDecimal(value));
    }
    public static Rational fromDecimal(BigDecimal value) {
        Rational f = approximate(value);
        return new BigDecimal(f.n).divide(new BigDecimal(f.d), Real.context).equals(value) ? f : null;
    }
    public static Rational approximate(Real decimal) {
        return approximate(decimal.value);
    }
    public static Rational approximate(double value) {
        return approximate(new BigDecimal(value));
    }
    public static Rational approximate(BigDecimal value) {
        BigInteger remaining = value.toBigInteger();
        BigDecimal x = value.subtract(new BigDecimal(remaining), Real.context);
        return (Rational) (value.compareTo(BigDecimal.ZERO) < 0 ?
                approximate01(x.negate(Real.context)).negate() : approximate01(x)).add(new Rational(remaining));
    }
    private static Rational approximate01(BigDecimal x) {
        BigInteger a = BigInteger.ZERO, b = BigInteger.ONE;
        BigInteger c = BigInteger.ONE,  d = BigInteger.ONE;

        BigInteger limit = new BigInteger("10000000000000");

        for(int i=0; i<100000 && b.compareTo(limit) <= 0 && d.compareTo(limit) <= 0; i++) {
            BigDecimal mid = new BigDecimal(a.add(c), Real.context)
                    .divide(new BigDecimal(b.add(d), Real.context), Real.context);
            if(x.equals(mid))
                return new Rational(a.add(c), b.add(d));
            if(x.compareTo(mid) > 0) a = a.add(c);
            else c = c.add(a);
            b = b.add(d);
        }

        return b.compareTo(limit) > 0 ? new Rational(c,d) : new Rational(a,b);
    }
}
