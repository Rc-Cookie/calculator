package com.github.rccookie.math;

import org.jetbrains.annotations.NotNull;

public class Decimal implements Number {

    public static final Decimal ZERO = new Decimal(0);
    public static final Decimal ONE = new Decimal(1);
    public static final Decimal TWO = new Decimal(2);
    public static final Decimal MINUS_ONE = new Decimal(-1);

    public static final Decimal ABOUT_ONE = new Decimal(1, false);
    public static final Decimal PI = new Decimal(Math.PI, false);
    public static final Decimal E = new Decimal(Math.E, false);

    public static final Decimal RAD_TO_DEG = (Decimal) PI.divideOther(180);
    public static final Decimal DEG_TO_RAD = (Decimal) PI.divide(180);



    public final double value;
    public final boolean precise;

    public Decimal(double value) {
        this(value, true);
    }
    public Decimal(double value, boolean precise) {
        this.value = value;
        this.precise = precise;
    }
    public Decimal(Fraction fraction) {
        this(fraction, true);
    }
    Decimal(Fraction fraction, boolean precise) {
        this.value = fraction.n / (double) fraction.d;
        this.precise = precise && Fraction.approximate(value).equals(fraction);
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Decimal d && Double.compare(value, d.value) == 0) ||
               (obj instanceof Fraction f && equals(new Decimal(f, false))) ||
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
            case Decimal d -> equalTo(d);
            case Fraction f -> equalTo(f);
            default -> x.equalTo(this);
        };
    }

    public Number equalTo(Decimal x) {
        return new Decimal(Double.compare(value, x.value) == 0 ? 1 : 0, precise && x.precise);
    }

    public Number equalTo(Fraction x) {
        return equalTo(new Decimal(x, precise));
    }

    @Override
    public @NotNull Number lessThan(Number x) {
        return switch(x) {
            case Decimal d -> lessThan(d);
            case Fraction f -> lessThan(f);
            default -> x.greaterThanOrEqual(this);
        };
    }

    public Number lessThan(Decimal x) {
        return new Decimal(Double.compare(value, x.value) < 0 ? 1 : 0, precise && x.precise);
    }

    public Number lessThan(Fraction x) {
        return lessThan(new Decimal(x, precise));
    }

    @Override
    public @NotNull Number greaterThan(Number x) {
        return switch(x) {
            case Decimal d -> greaterThan(d);
            case Fraction f -> greaterThan(f);
            default -> x.lessThanOrEqual(this);
        };
    }

    public Number greaterThan(Decimal x) {
        return new Decimal(Double.compare(value, x.value) > 0 ? 1 : 0, precise && x.precise);
    }

    public Number greaterThan(Fraction x) {
        return greaterThan(new Decimal(x, this.precise));
    }

    @Override
    public @NotNull Number add(Number x) {
        return switch(x) {
            case Decimal d -> add(d);
            case Fraction f -> add(f);
            default -> x.add(this);
        };
    }

    @NotNull
    public Number add(Decimal x) {
        if(!(precise && x.precise))
            return new Decimal(value + x.value, false);

        Fraction f = Fraction.fromDecimal(this), xf;
        if(f == null || (xf = Fraction.fromDecimal(x)) == null)
            return new Decimal(value + x.value, (value + x.value) - x.value == value);

        return f.add(xf);
    }

    @NotNull
    public Number add(Fraction x) {
        if(!precise)
            return new Decimal(value + new Decimal(x, false).value, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.add(x);

        Decimal xd = new Decimal(x);
        return new Decimal(value + xd.value, xd.precise && (value + xd.value) - xd.value == value);
    }

    @Override
    public @NotNull Number subtract(Number x) {
        return switch(x) {
            case Decimal d -> subtract(d);
            case Fraction f -> subtract(f);
            default -> x.subtractFrom(this);
        };
    }

    @NotNull
    public Number subtract(Decimal x) {
        if(!(precise && x.precise))
            return new Decimal(value - x.value, false);

        Fraction f = Fraction.fromDecimal(this), xf;
        if(f == null || (xf = Fraction.fromDecimal(x)) == null)
            return new Decimal(value - x.value, (value - x.value) + x.value == value);

        return f.subtract(xf);
    }

    @NotNull
    public Number subtract(Fraction x) {
        if(!precise)
            return new Decimal(value - new Decimal(x, false).value, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.subtract(x);

        Decimal xd = new Decimal(x);
        return new Decimal(value - xd.value, xd.precise && (value - xd.value) + xd.value == value);
    }

    @Override
    public @NotNull Number subtractFrom(Number x) {
        return switch(x) {
            case Decimal d -> d.subtract(this);
            case Fraction f -> subtractFrom(f);
            default -> x.subtract(this);
        };
    }

    @NotNull
    public Number subtractFrom(Fraction x) {
        if(!precise)
            return new Decimal(new Decimal(x, false).value - value, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return x.subtract(f);

        Decimal xd = new Decimal(x);
        return new Decimal(xd.value - value, xd.precise && (xd.value - value) + value == xd.value);
    }

    @Override
    public @NotNull Number multiply(Number x) {
        return switch(x) {
            case Decimal d -> multiply(d);
            case Fraction f -> multiply(f);
            default -> x.multiply(this);
        };
    }

    @NotNull
    public Number multiply(Decimal x) {
        if(!(precise && x.precise))
            return new Decimal(value * x.value, false);

        Fraction f = Fraction.fromDecimal(this), xf;
        if(f == null || (xf = Fraction.fromDecimal(x)) == null)
            return new Decimal(value * x.value, (value * x.value) / x.value == value);

        return f.multiply(xf);
    }

    @NotNull
    public Number multiply(Fraction x) {
        if(!precise)
            return new Decimal(value * x.n / x.d, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.multiply(x);

        Decimal xd = new Decimal(x);
        return new Decimal(value * xd.value, xd.precise && (value * xd.value) / xd.value == value);
    }

    @Override
    public @NotNull Number divide(Number x) {
        return switch(x) {
            case Decimal d -> divide(d);
            case Fraction f -> divide(f);
            default -> x.divideOther(this);
        };
    }

    @NotNull
    public Number divide(Decimal x) {
        if(!(precise && x.precise))
            return new Decimal(value / x.value, false);

        Fraction f = Fraction.fromDecimal(this), xf;
        if(f == null || (xf = Fraction.fromDecimal(x)) == null)
            return new Decimal(value / x.value, (value / x.value) * x.value == value);

        return f.divide(xf);
    }

    @NotNull
    public Number divide(Fraction x) {
        if(!precise)
            return new Decimal(value / x.n * x.d, false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.divide(x);

        Decimal xd = new Decimal(x);
        return new Decimal(value / xd.value, xd.precise && (value / xd.value) * xd.value == value);
    }

    @Override
    public @NotNull Number divideOther(Number x) {
        return switch(x) {
            case Decimal d -> d.divide(this);
            case Fraction f -> divideOther(f);
            default -> x.divide(this);
        };
    }

    @NotNull
    public Number divideOther(Fraction x) {
        if(!precise)
            return new Decimal(x.n / (x.d * value), false);

        Fraction f = Fraction.fromDecimal(this);
        if(f != null)
            return f.divideOther(x);

        Decimal xd = new Decimal(x);
        return new Decimal(xd.value / value, xd.precise && (xd.value / value) * value == xd.value);
    }

    @Override
    public @NotNull Number raise(Number x) {
        return switch(x) {
            case Decimal d -> raise(d);
            case Fraction f -> raise(f);
            default -> x.raiseOther(this);
        };
    }

    @NotNull
    public Number raise(Decimal x) {
        if(x.value == 0 || value == 1)
            return one(precise && x.precise);
        if(x.value == 1)
            return x.precise || !this.precise ? this : new Decimal(value, false);
        if(precise && x.precise) {
            Fraction xf = Fraction.fromDecimal(x);
            if(xf != null)
                return raise(xf);
        }
        if(x.value == 2)
            return new Decimal(value * value, precise && x.precise && (value * value) / value == value);
        if(x.value == 0.5) {
            double sqrt = Math.sqrt(value);
            return new Decimal(sqrt, precise && x.precise && (long) sqrt == sqrt);
        }
        double pow = Math.pow(value, x.value);
        return new Decimal(pow, precise && x.precise && (long) pow == pow);
    }

    @NotNull
    public Number raise(Fraction x) {
        if(!precise)
            return raise(new Decimal(x, false));

        Fraction f = Fraction.fromDecimal(this);
        return f != null ? f.raise(x) : raise(new Decimal(x, false));
    }

    @Override
    public @NotNull Number raiseOther(Number x) {
        return switch(x) {
            case Decimal d -> d.raise(this);
            case Fraction f -> raiseOther(f);
            default -> x.raise(this);
        };
    }

    @NotNull
    public Number raiseOther(Fraction x) {
        if(!precise)
            return new Decimal(x, false).raise(this);

        Fraction f = Fraction.fromDecimal(this);
        return f != null ? x.raise(f) : new Decimal(x, false).raise(this);
    }

    @Override
    public @NotNull Number abs() {
        return value < 0 ? new Decimal(-value, precise) : this;
    }

    @Override
    public @NotNull Number negate() {
        return new Decimal(-value, precise);
    }

    @Override
    public @NotNull Number invert() {
        if(value == 1) return this;
        Fraction f = Fraction.fromDecimal(this);
        return f != null ? f.invert() : new Decimal(1 / value);
    }



    public static Number one(boolean precise) {
        return precise ? Number.ONE() : Number.ABOUT_ONE();
    }
}
