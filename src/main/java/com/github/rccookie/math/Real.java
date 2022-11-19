package com.github.rccookie.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.github.rccookie.math.calculator.Calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Real implements Number {

    static MathContext context = new MathContext(50, RoundingMode.HALF_UP);



    public static final Real ZERO = new Real(0);
    public static final Real ONE = new Real(1);
    public static final Real TWO = new Real(2);
    public static final Real MINUS_ONE = new Real(-1);

    public static final Real ABOUT_ONE = new Real(1, false);
    public static final Real PI = new Real(BigDecimalMath.PI, false, false);
    public static final Real E = new Real(BigDecimalMath.E, false, false);

    public static final Real RAD_TO_DEG = (Real) PI.divideOther(180);
    public static final Real DEG_TO_RAD = (Real) PI.divide(180);



    public static boolean SCIENTIFIC_NOTATION = false;



    @NotNull
    public final BigDecimal value;
    public final boolean precise;


    public Real(double value) {
        this(new BigDecimal(""+value));
    }
    public Real(BigDecimal value) {
        this(value, true);
    }
    public Real(double value, boolean precise) {
        this(new BigDecimal(""+value), precise);
    }
    public Real(double value, int timesTenTo, boolean precise) {
        this(new BigDecimal(""+value).scaleByPowerOfTen(timesTenTo), precise);
    }
    public Real(@NotNull BigDecimal value, boolean precise) {
        this(value, precise, true);
    }
    public Real(Rational fraction) {
        this(fraction, true);
    }
    Real(Rational fraction, boolean precise) {
        this.value = new BigDecimal(fraction.n).setScale(context.getPrecision(), context.getRoundingMode()).divide(new BigDecimal(fraction.d), context);
        this.precise = precise && Rational.approximate(value).equals(fraction);
    }
    private Real(@NotNull BigDecimal value, boolean precise, boolean round) {
        this.value = round ? value.setScale(context.getPrecision(), RoundingMode.HALF_UP) : value;
        this.precise = precise;
    }

    @Override
    public String toString() {
        BigDecimal v = value.setScale(context.getPrecision(), context.getRoundingMode()).stripTrailingZeros();
        return SCIENTIFIC_NOTATION ? v.toString() : v.toPlainString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Real d && value.compareTo(d.value) == 0) ||
               (obj instanceof Rational r && equals(new Real(r, false))) ||
               (obj instanceof Vector v && v.isScalar() && equals(v.x()));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    public double toDouble(@Nullable Calculator ignored) {
        return toDouble();
    }

    public double toDouble() {
        return value.doubleValue();
    }

    @Override
    public @NotNull Number equalTo(Number x) {
        return switch(x) {
            case Real d -> equalTo(d);
            case Rational r -> equalTo(r);
            default -> x.equalTo(this);
        };
    }

    public Number equalTo(Real x) {
        return new Real(value.compareTo(x.value) == 0 ? 1 : 0, precise && x.precise);
    }

    public Number equalTo(Rational x) {
        return equalTo(new Real(x, precise));
    }

    @Override
    public @NotNull Number lessThan(Number x) {
        return switch(x) {
            case Real d -> lessThan(d);
            case Rational r -> lessThan(r);
            default -> x.greaterThanOrEqual(this);
        };
    }

    public Number lessThan(Real x) {
        return new Real(value.compareTo(x.value) < 0 ? 1 : 0, precise && x.precise);
    }

    public Number lessThan(Rational x) {
        return lessThan(new Real(x, precise));
    }

    @Override
    public @NotNull Number greaterThan(Number x) {
        return switch(x) {
            case Real d -> greaterThan(d);
            case Rational r -> greaterThan(r);
            default -> x.lessThanOrEqual(this);
        };
    }

    public Number greaterThan(Real x) {
        return new Real(value.compareTo(x.value) > 0 ? 1 : 0, precise && x.precise);
    }

    public Number greaterThan(Rational x) {
        return greaterThan(new Real(x, this.precise));
    }

    @Override
    public @NotNull Number add(Number x) {
        return switch(x) {
            case Real d -> add(d);
            case Rational r -> add(r);
            default -> x.add(this);
        };
    }

    @NotNull
    public Number add(Real x) {
        if(!(precise && x.precise))
            return new Real(value.add(x.value, context), false);

        Rational f = Rational.fromDecimal(this), xf;
        if(f == null || (xf = Rational.fromDecimal(x)) == null)
            return new Real(value.add(x.value), value.add(x.value, context).subtract(x.value, context).compareTo(value) == 0);

        return f.add(xf);
    }

    @NotNull
    public Number add(Rational x) {
        if(!precise)
            return new Real(value.add(new Real(x, false).value), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.add(x);

        Real xd = new Real(x);
        return new Real(value.add(xd.value), xd.precise && value.add(xd.value).subtract(xd.value).compareTo(value) == 0);
    }

    @Override
    public @NotNull Number subtract(Number x) {
        return switch(x) {
            case Real d -> subtract(d);
            case Rational r -> subtract(r);
            default -> x.subtractFrom(this);
        };
    }

    @NotNull
    public Number subtract(Real x) {
        if(!(precise && x.precise))
            return new Real(value.subtract(x.value), false);

        Rational f = Rational.fromDecimal(this), xf;
        if(f == null || (xf = Rational.fromDecimal(x)) == null)
            return new Real(value.subtract(x.value), value.subtract(x.value).add(x.value).compareTo(value) == 0);

        return f.subtract(xf);
    }

    @NotNull
    public Number subtract(Rational x) {
        if(!precise)
            return new Real(value.subtract(new Real(x, false).value), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.subtract(x);

        Real xd = new Real(x);
        return new Real(value.add(xd.value), xd.precise && value.subtract(xd.value).add(xd.value).compareTo(value) == 0);
    }

    @Override
    public @NotNull Number subtractFrom(Number x) {
        return switch(x) {
            case Real d -> d.subtract(this);
            case Rational r -> subtractFrom(r);
            default -> x.subtract(this);
        };
    }

    @NotNull
    public Number subtractFrom(Rational x) {
        if(!precise)
            return new Real(new Real(x, false).value.subtract(value), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return x.subtract(f);

        Real xd = new Real(x);
        return new Real(xd.value.subtract(value), xd.precise && xd.value.subtract(value).add(value).compareTo(xd.value) == 0);
    }

    @Override
    public @NotNull Number multiply(Number x) {
        return switch(x) {
            case Real d -> multiply(d);
            case Rational r -> multiply(r);
            default -> x.multiply(this);
        };
    }

    @NotNull
    public Number multiply(Real x) {
        if(!(precise && x.precise))
            return new Real(value.multiply(x.value, context), false);

        Rational f = Rational.fromDecimal(this), xf;
        if(f == null || (xf = Rational.fromDecimal(x)) == null)
            return new Real(value.multiply(x.value, context), value.multiply(x.value, context).divide(x.value, context).compareTo(value) == 0);

        return f.multiply(xf);
    }

    @NotNull
    public Number multiply(Rational x) {
        if(!precise)
            return new Real(value.multiply(new Real(x, false).value, context), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.multiply(x);

        Real xd = new Real(x);
        return new Real(value.multiply(xd.value), xd.precise && value.multiply(xd.value, context).divide(xd.value, context).compareTo(value) == 0);
    }

    @Override
    public @NotNull Number divide(Number x) {
        return switch(x) {
            case Real d -> divide(d);
            case Rational r -> divide(r);
            default -> x.divideOther(this);
        };
    }

    @NotNull
    public Number divide(Real x) {
        if(!(precise && x.precise))
            return new Real(value.divide(x.value, context), false);

        Rational f = Rational.fromDecimal(this), xf;
        if(f == null || (xf = Rational.fromDecimal(x)) == null)
            return new Real(value.divide(x.value, context), value.divide(x.value, context).multiply(x.value, context).compareTo(value) == 0);

        return f.divide(xf);
    }

    @NotNull
    public Number divide(Rational x) {
        if(!precise)
            return new Real(value.divide(new Real(x, false).value, context), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.divide(x);

        Real xd = new Real(x);
        return new Real(value.divide(xd.value, context), xd.precise && value.divide(xd.value, context).multiply(xd.value, context).compareTo(value) == 0);
    }

    @Override
    public @NotNull Number divideOther(Number x) {
        return switch(x) {
            case Real d -> d.divide(this);
            case Rational r -> divideOther(r);
            default -> x.divide(this);
        };
    }

    @NotNull
    public Number divideOther(Rational x) {
        if(!precise)
            return new Real(new Real(x, false).value.divide(value, context), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.divideOther(x);

        Real xd = new Real(x);
        return new Real(xd.value.divide(value, context), xd.precise && xd.value.divide(value, context).multiply(value, context).compareTo(xd.value) == 0);
    }

    @Override
    public @NotNull Number raise(Number x) {
        return switch(x) {
            case Real d -> raise(d);
            case Rational r -> raise(r);
            default -> x.raiseOther(this);
        };
    }

    @NotNull
    public Number raise(Real x) {
        if(x.value.compareTo(BigDecimal.ZERO) == 0 || value.compareTo(BigDecimal.ONE) == 0)
            return one(precise && x.precise);
        if(x.value.compareTo(BigDecimal.ONE) == 0)
            return x.precise || !this.precise ? this : new Real(value, false);
        if(precise && x.precise) {
            Rational xf = Rational.fromDecimal(x);
            if(xf != null)
                return raise(xf);
        }
        if(x.value.compareTo(new BigDecimal(2)) == 0)
            return new Real(value.multiply(value, context), precise && x.precise && value.multiply(value, context).divide(value, context).compareTo(value) == 0);
        if(x.value.compareTo(new BigDecimal("0.5")) == 0) {
            BigDecimal sqrt = value.sqrt(context);
            return new Real(sqrt, precise && x.precise && sqrt.setScale(0, RoundingMode.HALF_EVEN).compareTo(sqrt) == 0);
        }
        BigDecimal pow = BigDecimalMath.pow(value, x.value);
        return new Real(pow, precise && x.precise && pow.setScale(0, RoundingMode.HALF_EVEN).compareTo(pow) == 0);
    }

    @NotNull
    public Number raise(Rational x) {
        if(!precise)
            return raise(new Real(x, false));

        Rational f = Rational.fromDecimal(this);
        return f != null ? f.raise(x) : raise(new Real(x, false));
    }

    @Override
    public @NotNull Number raiseOther(Number x) {
        return switch(x) {
            case Real d -> d.raise(this);
            case Rational f -> raiseOther(f);
            default -> x.raise(this);
        };
    }

    @NotNull
    public Number raiseOther(Rational x) {
        if(!precise)
            return new Real(x, false).raise(this);

        Rational f = Rational.fromDecimal(this);
        return f != null ? x.raise(f) : new Real(x, false).raise(this);
    }

    @Override
    public @NotNull Number abs() {
        return value.signum() < 0 ? new Real(value.negate(), precise) : this;
    }

    @Override
    public @NotNull Number negate() {
        return new Real(value.negate(), precise);
    }

    @Override
    public @NotNull Number invert() {
        if(value.compareTo(BigDecimal.ONE) == 0) return this;
        Rational f = Rational.fromDecimal(this);
        return f != null ? f.invert() : new Real(BigDecimal.ONE.divide(value, context));
    }



    public static Number one(boolean precise) {
        return precise ? Number.ONE() : Number.ABOUT_ONE();
    }

    public static Number zero(boolean precise) {
        return precise ? Number.ZERO() : new Real(0, false);
    }

    public static Number minusOne(boolean precise) {
        return precise ? Number.MINUS_ONE() : Number.ABOUT_ONE().negate();
    }



    public static int getPrecision() {
        return context.getPrecision();
    }

    public static void setPrecision(int precision) {
        context = new MathContext(precision, RoundingMode.HALF_UP);
    }
}
