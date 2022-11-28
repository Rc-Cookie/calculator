package com.github.rccookie.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Real implements SimpleNumber {

    static MathContext context = new MathContext(53, RoundingMode.HALF_UP);



    public static final Real ZERO = new Real(0);
    public static final Real ONE = new Real(1);
    public static final Real TWO = new Real(2);
    public static final Real MINUS_ONE = new Real(-1);

    public static final Real ABOUT_ONE = new Real(1, false);
    public static final Real PI = new Real(BigDecimalMath.PI, false, false);
    public static final Real E = new Real(BigDecimalMath.E, false, false);

    public static final Real RAD_TO_DEG = new Real(new BigDecimal(180).divide(BigDecimalMath.PI, new MathContext(BigDecimalMath.PI.precision())), false, false);
    public static final Real DEG_TO_RAD = new Real(BigDecimalMath.PI.divide(new BigDecimal(180), new MathContext(BigDecimalMath.PI.precision())), false, false);



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
        this(value, timesTenTo, precise, true);
    }
    public Real(double value, int timesTenTo, boolean precise, boolean round) {
        this(new BigDecimal(""+value).scaleByPowerOfTen(timesTenTo), precise, round);
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
    public Real(double value, boolean precise, boolean round) {
        this(new BigDecimal(""+value), precise, round);
    }
    public Real(@NotNull BigDecimal value, boolean precise, boolean round) {
        this.value = round ? value.setScale(context.getPrecision(), RoundingMode.HALF_UP) : value;
        this.precise = precise;
    }

    @Override
    public String toString() {
        BigDecimal v = roundedValue().stripTrailingZeros();
        return SCIENTIFIC_NOTATION ? v.toString() : v.toPlainString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Real d && roundedValue().compareTo(d.roundedValue()) == 0) ||
               (obj instanceof Rational r && equals(new Real(r, false))) ||
               (obj instanceof Vector v && v.isScalar() && equals(v.x()));
    }

    private BigDecimal roundedValue() {
        return value.setScale(context.getPrecision() - 3, context.getRoundingMode());
    }

    @Override
    public int hashCode() {
        return roundedValue().hashCode();
    }

    @Deprecated
    @Override
    public double toDouble(@Nullable SymbolLookup ignored) {
        return toDouble();
    }

    @Override
    public double toDouble() {
        return value.doubleValue();
    }

    @Override
    public @NotNull SimpleNumber equalTo(SimpleNumber x) {
        return switch(x) {
            case Real d -> equalTo(d);
            case Rational r -> equalTo(r);
            default -> x.equalTo(this);
        };
    }

    public SimpleNumber equalTo(Real x) {
        return new Real(roundedValue().compareTo(x.roundedValue()) == 0 ? 1 : 0, precise && x.precise);
    }

    public SimpleNumber equalTo(Rational x) {
        return equalTo(new Real(x, precise));
    }

    @Override
    public @NotNull SimpleNumber lessThan(SimpleNumber x) {
        return switch(x) {
            case Real d -> lessThan(d);
            case Rational r -> lessThan(r);
            default -> x.greaterThanOrEqual(this);
        };
    }

    public SimpleNumber lessThan(Real x) {
        return new Real(value.compareTo(x.value) < 0 ? 1 : 0, precise && x.precise);
    }

    public SimpleNumber lessThan(Rational x) {
        return lessThan(new Real(x, precise));
    }

    @Override
    public @NotNull SimpleNumber greaterThan(SimpleNumber x) {
        return switch(x) {
            case Real d -> greaterThan(d);
            case Rational r -> greaterThan(r);
            default -> x.lessThanOrEqual(this);
        };
    }

    public SimpleNumber greaterThan(Real x) {
        return new Real(value.compareTo(x.value) > 0 ? 1 : 0, precise && x.precise);
    }

    public SimpleNumber greaterThan(Rational x) {
        return greaterThan(new Real(x, this.precise));
    }

    @Override
    public @NotNull SimpleNumber add(SimpleNumber x) {
        return switch(x) {
            case Real d -> add(d);
            case Rational r -> add(r);
            default -> x.add(this);
        };
    }

    @NotNull
    public SimpleNumber add(Real x) {
        if(!(precise && x.precise))
            return new Real(value.add(x.value, context), false);

        Rational f = Rational.fromDecimal(this), xf;
        if(f == null || (xf = Rational.fromDecimal(x)) == null)
            return new Real(value.add(x.value), value.add(x.value, context).subtract(x.value, context).compareTo(value) == 0);

        return f.add(xf);
    }

    @NotNull
    public SimpleNumber add(Rational x) {
        if(!precise)
            return new Real(value.add(new Real(x, false).value), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.add(x);

        Real xd = new Real(x);
        return new Real(value.add(xd.value), xd.precise && value.add(xd.value).subtract(xd.value).compareTo(value) == 0);
    }

    @Override
    public @NotNull SimpleNumber subtract(SimpleNumber x) {
        return switch(x) {
            case Real d -> subtract(d);
            case Rational r -> subtract(r);
            default -> x.subtractFrom(this);
        };
    }

    @NotNull
    public SimpleNumber subtract(Real x) {
        if(!(precise && x.precise))
            return new Real(value.subtract(x.value), false);

        Rational f = Rational.fromDecimal(this), xf;
        if(f == null || (xf = Rational.fromDecimal(x)) == null)
            return new Real(value.subtract(x.value), value.subtract(x.value).add(x.value).compareTo(value) == 0);

        return f.subtract(xf);
    }

    @NotNull
    public SimpleNumber subtract(Rational x) {
        if(!precise)
            return new Real(value.subtract(new Real(x, false).value), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.subtract(x);

        Real xd = new Real(x);
        return new Real(value.subtract(xd.value), xd.precise && value.subtract(xd.value).add(xd.value).compareTo(value) == 0);
    }

    @Override
    public @NotNull SimpleNumber subtractFrom(SimpleNumber x) {
        return switch(x) {
            case Real d -> d.subtract(this);
            case Rational r -> subtractFrom(r);
            default -> x.subtract(this);
        };
    }

    @NotNull
    public SimpleNumber subtractFrom(Rational x) {
        if(!precise)
            return new Real(new Real(x, false).value.subtract(value), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return x.subtract(f);

        Real xd = new Real(x);
        return new Real(xd.value.subtract(value), xd.precise && xd.value.subtract(value).add(value).compareTo(xd.value) == 0);
    }

    @Override
    public @NotNull SimpleNumber multiply(SimpleNumber x) {
        return switch(x) {
            case Real d -> multiply(d);
            case Rational r -> multiply(r);
            default -> x.multiply(this);
        };
    }

    @NotNull
    public SimpleNumber multiply(Real x) {
        if(value.equals(BigDecimal.ZERO) || x.value.equals(BigDecimal.ZERO))
            return zero(precise || x.precise);

        if(!(precise && x.precise))
            return new Real(value.multiply(x.value, context), false);

        Rational f = Rational.fromDecimal(this), xf;
        if(f == null || (xf = Rational.fromDecimal(x)) == null)
            return new Real(value.multiply(x.value, context), value.multiply(x.value, context).divide(x.value, context).compareTo(value) == 0);

        return f.multiply(xf);
    }

    @NotNull
    public SimpleNumber multiply(Rational x) {
        if(x.n.equals(BigInteger.ZERO))
            return Number.ZERO();
        if(value.equals(BigDecimal.ZERO))
            return one(precise);

        if(!precise)
            return new Real(value.multiply(new Real(x, false).value, context), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.multiply(x);

        Real xd = new Real(x);
        return new Real(value.multiply(xd.value), xd.precise && value.multiply(xd.value, context).divide(xd.value, context).compareTo(value) == 0);
    }

    @Override
    public @NotNull SimpleNumber divide(SimpleNumber x) {
        return switch(x) {
            case Real d -> divide(d);
            case Rational r -> divide(r);
            default -> x.divideOther(this);
        };
    }

    @NotNull
    public SimpleNumber divide(Real x) {
        if(x.value.equals(BigDecimal.ZERO))
            throw new ArithmeticException("Division by zero");
        if(!(precise && x.precise))
            return new Real(value.divide(x.value, context), false);

        Rational f = Rational.fromDecimal(this), xf;
        if(f == null || (xf = Rational.fromDecimal(x)) == null)
            return new Real(value.divide(x.value, context), value.divide(x.value, context).multiply(x.value, context).compareTo(value) == 0);

        return f.divide(xf);
    }

    @NotNull
    public SimpleNumber divide(Rational x) {
        if(x.n.equals(BigInteger.ZERO))
            throw new ArithmeticException("Division by zero");
        if(!precise)
            return new Real(value.divide(new Real(x, false).value, context), false);

        Rational f = Rational.fromDecimal(this);
        if(f != null)
            return f.divide(x);

        Real xd = new Real(x);
        return new Real(value.divide(xd.value, context), xd.precise && value.divide(xd.value, context).multiply(xd.value, context).compareTo(value) == 0);
    }

    @Override
    public @NotNull SimpleNumber divideOther(SimpleNumber x) {
        return switch(x) {
            case Real d -> d.divide(this);
            case Rational r -> divideOther(r);
            default -> x.divide(this);
        };
    }

    @NotNull
    public SimpleNumber divideOther(Rational x) {
        if(value.equals(BigDecimal.ZERO))
            throw new ArithmeticException("Division by zero");
        if(x.equals(Rational.ZERO))
            return Number.ZERO();

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
            return one(precise || x.precise);
        if(x.value.compareTo(BigDecimal.ONE) == 0)
            return x.precise || !this.precise ? this : new Real(value, false);
        if(x.value.compareTo(BigDecimal.ONE) < 0 && x.value.compareTo(BigDecimal.ONE.negate()) > 0 && value.compareTo(BigDecimal.ZERO) < 0)
            return new Complex(Number.ZERO(), (SimpleNumber) negate().raise(x));
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
    public @NotNull SimpleNumber abs() {
        return value.signum() < 0 ? new Real(value.negate(), precise) : this;
    }

    @Override
    public @NotNull SimpleNumber negate() {
        return new Real(value.negate(), precise);
    }

    @Override
    public @NotNull SimpleNumber invert() {
        if(value.compareTo(BigDecimal.ONE) == 0) return this;
        Rational f = Rational.fromDecimal(this);
        return f != null ? f.invert() : new Real(BigDecimal.ONE.divide(value, context));
    }



    public static SimpleNumber one(boolean precise) {
        return precise ? Number.ONE() : Number.ABOUT_ONE();
    }

    public static SimpleNumber zero(boolean precise) {
        return precise ? Number.ZERO() : new Real(0, false);
    }

    public static SimpleNumber minusOne(boolean precise) {
        return precise ? Number.MINUS_ONE() : Number.ABOUT_ONE().negate();
    }



    public static int getPrecision() {
        return context.getPrecision() - 3;
    }

    public static void setPrecision(int precision) {
        Arguments.checkRange(precision, 2, null);
        context = new MathContext(precision + 3, RoundingMode.HALF_UP);
    }
}
