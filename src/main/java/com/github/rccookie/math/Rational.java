package com.github.rccookie.math;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class BigFraction implements Number {

    public static final BigFraction ZERO = new BigFraction(0);
    public static final BigFraction ONE = new BigFraction(1);
    public static final BigFraction TWO = new BigFraction(2);
    public static final BigFraction HALF = new BigFraction(1,2);
    public static final BigFraction MINUS_ONE = new BigFraction(-1);


    static final MathContext CONTEXT = BigDecimal.CONTEXT;



    public final BigInteger n,d;

    public BigFraction(long n) {
        this(BigInteger.valueOf(n));
    }

    public BigFraction(BigInteger n) {
        this(n, 1);
    }

    public BigFraction(long n, long d) {
        this(BigInteger.valueOf(n), d);
    }

    public BigFraction(long n, BigInteger d) {
        this(BigInteger.valueOf(n), d);
    }

    public BigFraction(BigInteger n, long d) {
        this(n, BigInteger.valueOf(d));
    }

    public BigFraction(BigInteger n, BigInteger d) {
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
        return (obj instanceof BigFraction f && n.equals(f.n) && d.equals(f.d)) ||
               (obj instanceof Vector v && v.isScalar() && equals(v.get(0))) ||
               (obj instanceof BigDecimal d && new BigDecimal(this).equals(d));
    }

    @Override
    public int hashCode() {
        return new BigDecimal(this).hashCode(); // Match with equal decimal
    }

    @Override
    public double toDouble() {
        return new BigDecimal(this).toDouble();
    }

    @Override
    public @NotNull Number equalTo(Number x) {
        return x instanceof BigFraction f ? equalTo(f) : x.equalTo(this);
    }

    public BigFraction equalTo(BigFraction x) {
        return n.equals(x.n) && d.equals(x.d) ? ONE : ZERO;
    }

    @Override
    public @NotNull Number lessThan(Number x) {
        return x instanceof BigFraction f ? lessThan(f) : x.greaterThanOrEqual(this);
    }

    public BigFraction lessThan(BigFraction x) {
        return n.multiply(x.d).compareTo(x.n.multiply(d)) < 0 ? ONE : ZERO;
    }

    @Override
    public @NotNull Number greaterThan(Number x) {
        return x instanceof BigFraction f ? greaterThan(f) : x.lessThanOrEqual(this);
    }

    public BigFraction greaterThan(BigFraction x) {
        return n.multiply(x.d).compareTo(x.n.multiply(d)) > 0 ? ONE : ZERO;
    }

    @Override
    public @NotNull Number add(Number x) {
        return x instanceof BigFraction f ? add(f) : x.add(this);
    }

    @NotNull
    public BigFraction add(BigFraction x) {
        return new BigFraction(n.multiply(x.d).add(x.n.multiply(d)), d.multiply(x.d));
    }

    @Override
    public @NotNull Number subtract(Number x) {
        return x instanceof BigFraction f ? subtract(f) : x.subtractFrom(this);
    }

    @NotNull
    public BigFraction subtract(BigFraction x) {
        return new BigFraction(n.multiply(x.d).subtract(x.n.multiply(d)), d.multiply(x.d));
    }

    @Override
    public @NotNull Number subtractFrom(Number x) {
        return x.subtract(this);
    }

    @Override
    public @NotNull Number multiply(Number x) {
        return x instanceof BigFraction f ? multiply(f) : x.multiply(this);
    }

    @NotNull
    public BigFraction multiply(BigFraction x) {
        return new BigFraction(n.multiply(x.n), d.multiply(x.d));
    }

    @Override
    public @NotNull Number divide(Number x) {
        return x instanceof BigFraction f ? divide(f) : x.divideOther(this);
    }

    @NotNull
    public BigFraction divide(BigFraction x) {
        return new BigFraction(n.multiply(x.d), d.multiply(x.n));
    }

    @Override
    public @NotNull Number divideOther(Number x) {
        return x.divide(this);
    }

    @Override
    public @NotNull Number raise(Number x) {
        return x instanceof BigFraction f ? raise(f) : x.raiseOther(this);
    }

    @NotNull
    public Number raise(BigFraction x) {
        if(Objects.equals(x.n, BigInteger.ONE)) return ONE;
        if(x.n.compareTo(BigInteger.ZERO) < 0) return raise(x.negate()).invert();
        if(x.n.equals(BigInteger.ONE)) {
            if(x.d.equals(BigInteger.ONE)) return this;
            if(x.d.equals(BigInteger.TWO)) {
                java.math.BigDecimal nd = new java.math.BigDecimal(n), dd;
                java.math.BigDecimal sqrtN = nd.sqrt(CONTEXT), sqrtD;
                if(sqrtN.round(CONTEXT).equals(sqrtN) && sqrtN.multiply(sqrtN, CONTEXT).equals(nd) &&
                        (sqrtD = (dd = new java.math.BigDecimal(d)).sqrt(CONTEXT)).round(CONTEXT).equals(sqrtD) &&
                        sqrtD.multiply(sqrtD, CONTEXT).equals(dd))
                    return new BigFraction(sqrtN.toBigInteger(), sqrtD.toBigInteger());
            }
        }
        else if(x.d.equals(BigInteger.ONE) && x.n.compareTo(BigInteger.valueOf(1000)) < 0)
            return raise(x.n.intValue());
        return new Decimal(this, false).raise(new Decimal(x, false));
    }

    public @NotNull Number raise(int x) {
        return new BigFraction(n.pow(x), d.pow(x));
    }

    @Override
    public @NotNull Number raiseOther(Number x) {
        return x.raise(this);
    }

    @Override
    public @NotNull Number abs() {
        return n.compareTo(BigInteger.ZERO) < 0 ? new BigFraction(n.negate(),d) : this;
    }

    @Override
    public @NotNull Number negate() {
        return new BigFraction(n.negate(),d);
    }

    @Override
    public @NotNull Number invert() {
        return n.equals(d) ? this : new BigFraction(d,n);
    }



    public static Number tryFromDecimal(BigDecimal x) {
        BigFraction f = fromDecimal(x);
        return f != null ? f : x;
    }

    public static BigFraction fromDecimal(BigDecimal decimal) {
        return fromDecimal(decimal.value);
    }
    public static BigFraction fromDecimal(double value) {
        return fromDecimal(new java.math.BigDecimal(value));
    }
    public static BigFraction fromDecimal(java.math.BigDecimal value) {
        BigFraction f = approximate(value);
        return new java.math.BigDecimal(f.n).divide(new java.math.BigDecimal(f.d), CONTEXT).equals(value) ? f : null;
    }
    public static BigFraction approximate(BigDecimal decimal) {
        return approximate(decimal.value);
    }
    public static BigFraction approximate(double value) {
        return approximate(new java.math.BigDecimal(value));
    }
    public static BigFraction approximate(java.math.BigDecimal value) {
        BigInteger remaining = value.toBigInteger();
        java.math.BigDecimal x = value.subtract(new java.math.BigDecimal(remaining), CONTEXT);
        return (BigFraction) (value.compareTo(java.math.BigDecimal.ZERO) < 0 ?
                approximate01(x.negate(CONTEXT)).negate() : approximate01(x)).add(new BigFraction(remaining));
    }
    private static BigFraction approximate01(java.math.BigDecimal x) {
        BigInteger a = BigInteger.ZERO, b = BigInteger.ONE;
        BigInteger c = BigInteger.ONE,  d = BigInteger.ONE;

        BigInteger limit = new BigInteger("10000000000000");

        for(int i=0; i<100000 && b.compareTo(limit) <= 0 && d.compareTo(limit) <= 0; i++) {
            java.math.BigDecimal mid = new java.math.BigDecimal(a.add(c), CONTEXT)
                    .divide(new java.math.BigDecimal(b.add(d), CONTEXT), CONTEXT);
            if(x.equals(mid))
                return new BigFraction(a.add(c), b.add(d));
            if(x.compareTo(mid) > 0) {
                a = a.add(c);
                b = b.add(d);
            }
            else {
                c = c.add(a);
                b = b.add(d);
            }
        }

        return b.compareTo(limit) > 0 ? new BigFraction(c,d) : new BigFraction(a,b);
    }
}
