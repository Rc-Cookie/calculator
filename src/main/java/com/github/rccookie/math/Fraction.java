package com.github.rccookie.math;

import org.jetbrains.annotations.NotNull;

public class Fraction implements Number {

    public static final Fraction ZERO = new Fraction(0);
    public static final Fraction ONE = new Fraction(1);
    public static final Fraction TWO = new Fraction(2);
    public static final Fraction HALF = new Fraction(1,2);
    public static final Fraction MINUS_ONE = new Fraction(-1);



    public final long n,d;

    public Fraction(long n) {
        this(n, 1);
    }

    public Fraction(long n, long d) {
        if(d == 0)
            throw new ArithmeticException("Division by zero");
        if(n == 0) {
            this.n = 0;
            this.d = 1;
        }
        else if(n == d)
            this.n = this.d = 1;
        else {
            long gcd = gcd(Math.abs(n), Math.abs(d)) * (d > 0 ? 1 : -1);
            this.n = n / gcd;
            this.d = d / gcd;
        }
    }

    @Override
    public String toString() {
        return n + (d == 1 ? "" : "/"+d);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Fraction f && n == f.n && d == f.d) ||
               (obj instanceof Vector v && v.isScalar() && equals(v.get(0))) ||
               (obj instanceof Decimal d && new Decimal(this).equals(d));
    }

    @Override
    public int hashCode() {
        return Double.hashCode((double) n/d); // Match with equal decimal
    }

    @Override
    public double toDouble() {
        return n / (double) d;
    }

    @Override
    public @NotNull Number equalTo(Number x) {
        return x instanceof Fraction f ? equalTo(f) : x.equalTo(this);
    }

    public Fraction equalTo(Fraction x) {
        return n == x.n && d == x.d ? ONE : ZERO;
    }

    @Override
    public @NotNull Number lessThan(Number x) {
        return x instanceof Fraction f ? lessThan(f) : x.greaterThanOrEqual(this);
    }

    public Fraction lessThan(Fraction x) {
        return n * x.d < x.n * d ? ONE : ZERO;
    }

    @Override
    public @NotNull Number greaterThan(Number x) {
        return x instanceof Fraction f ? greaterThan(f) : x.lessThanOrEqual(this);
    }

    public Fraction greaterThan(Fraction x) {
        return n * x.d > x.n * d ? ONE : ZERO;
    }

    @Override
    public @NotNull Number add(Number x) {
        return x instanceof Fraction f ? add(f) : x.add(this);
    }

    @NotNull
    public Fraction add(Fraction x) {
        return new Fraction(n*x.d + x.n*d, d*x.d);
    }

    @Override
    public @NotNull Number subtract(Number x) {
        return x instanceof Fraction f ? subtract(f) : x.subtractFrom(this);
    }

    @NotNull
    public Fraction subtract(Fraction x) {
        return new Fraction(n*x.d - x.n*d, d*x.d);
    }

    @Override
    public @NotNull Number subtractFrom(Number x) {
        return x.subtract(this);
    }

    @Override
    public @NotNull Number multiply(Number x) {
        return x instanceof Fraction f ? multiply(f) : x.multiply(this);
    }

    @NotNull
    public Fraction multiply(Fraction x) {
        return new Fraction(n*x.n, d*x.d);
    }

    @Override
    public @NotNull Number divide(Number x) {
        return x instanceof Fraction f ? divide(f) : x.divideOther(this);
    }

    @NotNull
    public Fraction divide(Fraction x) {
        return new Fraction(n*x.d, d*x.n);
    }

    @Override
    public @NotNull Number divideOther(Number x) {
        return x.divide(this);
    }

    @Override
    public @NotNull Number raise(Number x) {
        return x instanceof Fraction f ? raise(f) : x.raiseOther(this);
    }

    @NotNull
    public Number raise(Fraction x) {
        if(x.n == 0) return ONE;
        if(x.n < 0) return raise(x.negate()).invert();
        if(x.n == 1) {
            if(x.d == 1) return this;
            if(x.d == 2) {
                double sqrtN = Math.sqrt(n), sqrtD;
                if((long) sqrtN==sqrtN && sqrtN*sqrtN == n && (long) (sqrtD=Math.sqrt(d))==sqrtD && sqrtD*sqrtD == d)
                    return new Fraction((long) sqrtN, (long) sqrtD);
            }
            else if(x.d == 3) {
                double cbrtN = Math.cbrt(n), cbrtD;
                if((long) cbrtN==cbrtN && cbrtN*cbrtN == n && (long) (cbrtD=Math.cbrt(d))==cbrtD && cbrtD*cbrtD == d)
                    return new Fraction((long) cbrtN, (long) cbrtD);
            }
        }
        else if(x.d == 1 && x.n < 64)
            return raise((int) x.n);
        return new Decimal(this, false).raise(new Decimal(x, false));
    }

    public @NotNull Number raise(int x) {
        return new Fraction(pow(n,x), pow(d,x));
    }

    @Override
    public @NotNull Number raiseOther(Number x) {
        return x.raise(this);
    }

    @Override
    public @NotNull Number abs() {
        return n < 0 ? new Fraction(-n,d) : this;
    }

    @Override
    public @NotNull Number negate() {
        return new Fraction(-n,d);
    }

    @Override
    public @NotNull Number invert() {
        return n == 1 && d == 1 ? this : new Fraction(d,n);
    }

    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a%b);
    }

    private static long pow(long a, int b) {
        if(b == 0)        return 1;
        if(b == 1)        return a;
        if(b == 2)        return a*a;
        if(b%2 == 0)      return     pow (a * a, b/2); //even a=(a^2)^b/2
        else              return a * pow (a * a, b/2); //odd  a=a*(a^2)^b/2
    }

    public static Number tryFromDecimal(Decimal x) {
        Fraction f = fromDecimal(x);
        return f != null ? f : x;
    }

    public static Fraction fromDecimal(Decimal decimal) {
        return fromDecimal(decimal.value);
    }
    public static Fraction fromDecimal(double value) {
        Fraction f = approximate(value);
        return (double) f.n / f.d == value ? f : null;
    }
    public static Fraction approximate(Decimal decimal) {
        return approximate(decimal.value);
    }
    public static Fraction approximate(double value) {
        long remaining = (long) value;
        double x = value - remaining;
        return (Fraction) (value < 0 ? approximate01(-x).negate() : approximate01(x)).add(remaining);
    }
    private static Fraction approximate01(double x) {
        //noinspection DuplicatedCode
        long a = 0, b = 1;
        long c = 1, d = 1;

        for(int i = 0; i<1000 && b <= 100000000L && d <= 100000000L; i++) {
            double mid = (double) (a+c) / (b+d);
            if (x == mid) {
                if(b+d <= 100000000L)
                    return new Fraction(a+c, b+d);
                return d>b ? new Fraction(c, d):
                        new Fraction(a, b);
            }
            if (x > mid) {
                a += c;
                b += d;
            }
            else {
                c += a;
                d += b;
            }
        }

        return b > 100000000L ? new Fraction(c, d):
                new Fraction(a, b);
    }
}
