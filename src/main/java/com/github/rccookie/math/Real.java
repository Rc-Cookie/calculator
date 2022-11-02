package com.github.rccookie.math;

import java.math.MathContext;
import java.math.RoundingMode;

import org.jetbrains.annotations.NotNull;

public class BigDecimal implements Number {

    public static final BigDecimal ZERO = new BigDecimal(0);
    public static final BigDecimal ONE = new BigDecimal(1);
    public static final BigDecimal TWO = new BigDecimal(2);
    public static final BigDecimal MINUS_ONE = new BigDecimal(-1);

    public static final BigDecimal ABOUT_ONE = new BigDecimal(1, false);
    public static final BigDecimal PI = new BigDecimal(Math.PI, false);
    public static final BigDecimal E = new BigDecimal(Math.E, false);

    public static final BigDecimal RAD_TO_DEG = (BigDecimal) PI.divideOther(180);
    public static final BigDecimal DEG_TO_RAD = (BigDecimal) PI.divide(180);



    static final MathContext CONTEXT = new MathContext(100, RoundingMode.HALF_UP);



    public final java.math.BigDecimal value;
    public final boolean precise;

    public BigDecimal(double value) {
        this(new java.math.BigDecimal(value));
    }

    public BigDecimal(BigDecimal value) {
        this(value, true);
    }
    public BigDecimal(double value, boolean precise) {
        this.value = value;
        this.precise = precise;
    }
    public BigDecimal(Fraction fraction) {
        this(fraction, true);
    }
    BigDecimal(Fraction fraction, boolean precise) {
        this.value = fraction.n / (double) fraction.d;
        this.precise = precise && Fraction.approximate(value).equals(fraction);
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BigDecimal d && Double.compare(value, d.value) == 0) ||
               (obj instanceof Fraction f && equals(new BigDecimal(f, false))) ||
               (obj instanceof Vector v && v.isScalar() && equals(v.x()));
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public double toDouble() {
        return value;
    }

    @Override
    public @NotNull Number equalTo(Number x) {
        return switch(x) {
            case BigDecimal d -> equalTo(d);
            case Fraction f -> equalTo(f);
            default -> x.equalTo(this);
        };
    }

    public Number equalTo(BigDecimal x) {
        return new BigDecimal(Double.compare(value, x.value) == 0 ? 1 : 0, precise && x.precise);
    }

    public Number equalTo(Fraction x) {
        return equalTo(new BigDecimal(x, precise));
    }

    @Override
    public @NotNull Number lessThan(Number x) {
        return switch(x) {
            case BigDecimal d -> lessThan(d);
            case Fraction f -> lessThan(f);
            default -> x.greaterThanOrEqual(this);
        };
    }

    public Number lessThan(BigDecimal x) {
        return new BigDecimal(Double.compare(value, x.value) < 0 ? 1 : 0, precise && x.precise);
    }

    public Number lessThan(Fraction x) {
        return lessThan(new BigDecimal(x, precise));
    }

    @Override
    public @NotNull Number greaterThan(Number x) {
        return switch(x) {
            case BigDecimal d -> greaterThan(d);
            case Fraction f -> greaterThan(f);
            default -> x.lessThanOrEqual(this);
        };
    }

    public Number greaterThan(BigDecimal x) {
        return new BigDecimal(Double.compare(value, x.value) > 0 ? 1 : 0, precise && x.precise);
    }

    public Number greaterThan(Fraction x) {
        return greaterThan(new BigDecimal(x, this.precise));
    }

    @Override
    public @NotNull Number add(Number x) {
        return switch(x) {
            case BigDecimal d -> add(d);
            case Fraction f -> add(f);
            default -> x.add(this);
        };
    }

    @NotNull
    public Number add(BigDecimal x) {
        if(!(precise && x.precise))
            return new BigDecimal(value + x.value, false);

        Fraction f = Fraction.fromDecimal(this), xf;
        if(f == null || (xf = Fraction.fromDecimal(x)) == null)
            return new BigDecimal(value + x.value, (value + x.value) - x.value == value);

        return f.add(xf);
    }

    @NotNull
    public Number add(Fraction x) {
        if(!precise)
            return new BigDecimal(value + new BigDecimal(x, false).value, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.add(x);

        BigDecimal xd = new BigDecimal(x);
        return new BigDecimal(value + xd.value, xd.precise && (value + xd.value) - xd.value == value);
    }

    @Override
    public @NotNull Number subtract(Number x) {
        return switch(x) {
            case BigDecimal d -> subtract(d);
            case Fraction f -> subtract(f);
            default -> x.subtractFrom(this);
        };
    }

    @NotNull
    public Number subtract(BigDecimal x) {
        if(!(precise && x.precise))
            return new BigDecimal(value - x.value, false);

        Fraction f = Fraction.fromDecimal(this), xf;
        if(f == null || (xf = Fraction.fromDecimal(x)) == null)
            return new BigDecimal(value - x.value, (value - x.value) + x.value == value);

        return f.subtract(xf);
    }

    @NotNull
    public Number subtract(Fraction x) {
        if(!precise)
            return new BigDecimal(value - new BigDecimal(x, false).value, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.subtract(x);

        BigDecimal xd = new BigDecimal(x);
        return new BigDecimal(value - xd.value, xd.precise && (value - xd.value) + xd.value == value);
    }

    @Override
    public @NotNull Number subtractFrom(Number x) {
        return switch(x) {
            case BigDecimal d -> d.subtract(this);
            case Fraction f -> subtractFrom(f);
            default -> x.subtract(this);
        };
    }

    @NotNull
    public Number subtractFrom(Fraction x) {
        if(!precise)
            return new BigDecimal(new BigDecimal(x, false).value - value, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return x.subtract(f);

        BigDecimal xd = new BigDecimal(x);
        return new BigDecimal(xd.value - value, xd.precise && (xd.value - value) + value == xd.value);
    }

    @Override
    public @NotNull Number multiply(Number x) {
        return switch(x) {
            case BigDecimal d -> multiply(d);
            case Fraction f -> multiply(f);
            default -> x.multiply(this);
        };
    }

    @NotNull
    public Number multiply(BigDecimal x) {
        if(!(precise && x.precise))
            return new BigDecimal(value * x.value, false);

        Fraction f = Fraction.fromDecimal(this), xf;
        if(f == null || (xf = Fraction.fromDecimal(x)) == null)
            return new BigDecimal(value * x.value, (value * x.value) / x.value == value);

        return f.multiply(xf);
    }

    @NotNull
    public Number multiply(Fraction x) {
        if(!precise)
            return new BigDecimal(value * x.n / x.d, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.multiply(x);

        BigDecimal xd = new BigDecimal(x);
        return new BigDecimal(value * xd.value, xd.precise && (value * xd.value) / xd.value == value);
    }

    @Override
    public @NotNull Number divide(Number x) {
        return switch(x) {
            case BigDecimal d -> divide(d);
            case Fraction f -> divide(f);
            default -> x.divideOther(this);
        };
    }

    @NotNull
    public Number divide(BigDecimal x) {
        if(!(precise && x.precise))
            return new BigDecimal(value / x.value, false);

        Fraction f = Fraction.fromDecimal(this), xf;
        if(f == null || (xf = Fraction.fromDecimal(x)) == null)
            return new BigDecimal(value / x.value, (value / x.value) * x.value == value);

        return f.divide(xf);
    }

    @NotNull
    public Number divide(Fraction x) {
        if(!precise)
            return new BigDecimal(value / x.n * x.d, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.divide(x);

        BigDecimal xd = new BigDecimal(x);
        return new BigDecimal(value / xd.value, xd.precise && (value / xd.value) * xd.value == value);
    }

    @Override
    public @NotNull Number divideOther(Number x) {
        return switch(x) {
            case BigDecimal d -> d.divide(this);
            case Fraction f -> divideOther(f);
            default -> x.divide(this);
        };
    }

    @NotNull
    public Number divideOther(Fraction x) {
        if(!precise)
            return new BigDecimal(x.n / (x.d * value), false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.divideOther(x);

        BigDecimal xd = new BigDecimal(x);
        return new BigDecimal(xd.value / value, xd.precise && (xd.value / value) * value == xd.value);
    }

    @Override
    public @NotNull Number raise(Number x) {
        return switch(x) {
            case BigDecimal d -> raise(d);
            case Fraction f -> raise(f);
            default -> x.raiseOther(this);
        };
    }

    @NotNull
    public Number raise(BigDecimal x) {
        if(x.value == 0 || value == 1)
            return one(precise && x.precise);
        if(x.value == 1)
            return x.precise || !this.precise ? this : new BigDecimal(value, false);
        if(precise && x.precise) {
            Fraction xf = Fraction.fromDecimal(x);
            if(xf != null)
                return raise(xf);
        }
        if(x.value == 2)
            return new BigDecimal(value * value, precise && x.precise && (value * value) / value == value);
        if(x.value == 0.5) {
            double sqrt = Math.sqrt(value);
            return new BigDecimal(sqrt, precise && x.precise && (long) sqrt == sqrt);
        }
        double pow = Math.pow(value, x.value);
        return new BigDecimal(pow, precise && x.precise && (long) pow == pow);
    }

    @NotNull
    public Number raise(Fraction x) {
        if(!precise)
            return raise(new BigDecimal(x, false));

        Fraction f = Fraction.fromDecimal(this);
        return f != null ? f.raise(x) : raise(new BigDecimal(x, false));
    }

    @Override
    public @NotNull Number raiseOther(Number x) {
        return switch(x) {
            case BigDecimal d -> d.raise(this);
            case Fraction f -> raiseOther(f);
            default -> x.raise(this);
        };
    }

    @NotNull
    public Number raiseOther(Fraction x) {
        if(!precise)
            return new BigDecimal(x, false).raise(this);

        Fraction f = Fraction.fromDecimal(this);
        return f != null ? x.raise(f) : new BigDecimal(x, false).raise(this);
    }

    @Override
    public @NotNull Number abs() {
        return value < 0 ? new BigDecimal(-value, precise) : this;
    }

    @Override
    public @NotNull Number negate() {
        return new BigDecimal(-value, precise);
    }

    @Override
    public @NotNull Number invert() {
        if(value == 1) return this;
        Fraction f = Fraction.fromDecimal(this);
        return f != null ? f.invert() : new BigDecimal(1 / value);
    }



    public static Number one(boolean precise) {
        return precise ? Number.ONE() : Number.ABOUT_ONE();
    }
}
