package com.github.rccookie.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;

import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Rational implements SimpleNumber {

    @NotNull
    private static ToStringMode toStringMode = ToStringMode.SMART;
    private static int precision = 50;


    public static final Rational ZERO = new Rational(0);
    public static final Rational ONE = new Rational(1);
    public static final Rational MINUS_ONE = new Rational(-1);
    public static final Rational TWO = new Rational(2);
    public static final Rational HALF = new Rational(1,2);
    public static final Rational TEN = new Rational(10);
    public static final Rational ABOUT_ZERO = new Rational(0,1, false);
    public static final Rational ABOUT_ONE = new Rational(1,1, false);
    public static final Rational PI = new Rational(BigDecimalMath.PI, 0, false, false);
    public static final Rational E = new Rational(BigDecimalMath.E, 0, false, false);
    public static final Rational RAD_TO_DEG = new Rational(new BigDecimal(180).divide(BigDecimalMath.PI, new MathContext(BigDecimalMath.PI.precision())), 0, false, false);
    public static final Rational DEG_TO_RAD = new Rational(BigDecimalMath.PI.divide(new BigDecimal(180), new MathContext(BigDecimalMath.PI.precision())), 0, false, false);



    @NotNull
    public final BigInteger n,d;
    public final boolean precise;


    public Rational(double value) {
        this(value, true);
    }
    public Rational(double value, boolean precise) {
        this(new BigDecimal(value+""), precise);
    }
    public Rational(double value, int exp, boolean precise, boolean round) {
        this(new BigDecimal(value+""), exp, precise, round);
    }
    public Rational(BigDecimal value) {
        this(value, true);
    }
    public Rational(BigDecimal value, boolean precise) {
        this(value, 0, precise, true);
    }
    public Rational(BigDecimal value, int exp, boolean precise, boolean round) {
        if(round)
            value = value.setScale(value.toBigInteger().abs().toString().length() + precision, RoundingMode.HALF_UP);
        BigInteger baseVal = value.unscaledValue();
        BigInteger n,d;
        if(value.scale() == 0) {
            n = baseVal;
            d = BigInteger.ONE;
        }
        else if(value.scale() < 0) {
            n = baseVal.multiply(BigInteger.TEN.pow(-value.scale()));
            d = BigInteger.ONE;
        }
        else {
            n = baseVal;
            d = BigInteger.TEN.pow(value.scale());
        }
        if(exp > 0) n = n.multiply(BigInteger.TEN.pow(exp));
        else if(exp < 0) d = d.multiply(BigInteger.TEN.pow(-exp));

        BigInteger gcd = n.abs().gcd(d.abs()).multiply(d.compareTo(BigInteger.ZERO) > 0 ? BigInteger.ONE : BigInteger.ONE.negate());
        this.n = n.divide(gcd);
        this.d = d.divide(gcd);
        this.precise = precise;
    }
    public Rational(long n, long d) {
        this(n,d,true);
    }
    public Rational(long n, long d, boolean precise) {
        this(BigInteger.valueOf(n), BigInteger.valueOf(d), precise);
    }
    public Rational(BigInteger n) {
        this(n, BigInteger.ONE);
    }
    public Rational(BigInteger n, BigInteger d) {
        this(n,d,true);
    }
    public Rational(@NotNull BigInteger n, @NotNull BigInteger d, boolean precise) {
        this(n, d, precise, true);
    }
    private Rational(@NotNull BigInteger n, @NotNull BigInteger d, boolean precise, boolean reduce) {
        if(d.equals(BigInteger.ZERO))
            throw new ArithmeticException("Division by zero");
        if(n.equals(BigInteger.ZERO)) {
            this.n = BigInteger.ZERO;
            this.d = BigInteger.ONE;
        }
        else if(n.equals(d))
            this.n = this.d = BigInteger.ONE;
        else if(reduce) {
            BigInteger gcd = n.abs().gcd(d.abs()).multiply(d.compareTo(BigInteger.ZERO) > 0 ? BigInteger.ONE : BigInteger.ONE.negate());
            this.n = n.divide(gcd);
            this.d = d.divide(gcd);
        }
        else {
            this.n = n;
            this.d = d;
        }
        this.precise = precise;
    }

    public BigDecimal toBigDecimal() {
        MathContext context = new MathContext(n.abs().divide(d).toString().length() + precision + 3, RoundingMode.HALF_UP);
        return new BigDecimal(n, context).divide(new BigDecimal(d, context), context).setScale(context.getPrecision(), context.getRoundingMode());
    }

    @Override
    public boolean precise() {
        return precise;
    }

    @Override
    public String toString() {
        return toString(toStringMode);
    }

    public String toString(ToStringMode mode) {
        return detailedToString(mode).str;
    }

    public DetailedToString detailedToString() {
        return detailedToString(toStringMode);
    }

    public DetailedToString detailedToString(ToStringMode mode) {
        return mode.toString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof Number)) return false;
        if(!(obj instanceof Rational r)) return obj.equals(this);
        return n.equals(r.n) && d.equals(r.d);
    }

    @Override
    public int hashCode() {
        return n.hashCode() ^ d.hashCode();
    }

    @Override
    public double toDouble(SymbolLookup lookup) {
        return new BigDecimal(n).divide(new BigDecimal(d), new MathContext(20, RoundingMode.HALF_UP)).doubleValue();
    }

    @Override
    public SimpleNumber add(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.add(this);
        return new Rational(n.multiply(r.d).add(r.n.multiply(d)), d.multiply(r.d), precise && r.precise);
    }

    @Override
    public SimpleNumber subtract(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.subtractFrom(this);
        return new Rational(n.multiply(r.d).subtract(r.n.multiply(d)), d.multiply(r.d), precise && r.precise);
    }

    @Override
    public SimpleNumber subtractFrom(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.subtract(this);
        return new Rational(r.n.multiply(d).subtract(n.multiply(r.d)), d.multiply(r.d), precise && r.precise);
    }

    @Override
    public SimpleNumber multiply(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.subtractFrom(this);
        return new Rational(n.multiply(r.n), d.multiply(r.d), precise && r.precise);
    }

    @Override
    public SimpleNumber divide(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.divideOther(this);
        return new Rational(n.multiply(r.d), d.multiply(r.n), precise && r.precise);
    }

    @Override
    public SimpleNumber divideOther(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.divide(this);
        return new Rational(r.n.multiply(d), r.d.multiply(n), precise && r.precise);
    }

    @Override
    public @NotNull Number raise(Number x) {
        if(!(x instanceof Rational r)) return x.raiseOther(this);

        if(r.n.signum() == 0) // x^0 = 1
            return ONE(r.precise);
        if(n.signum() == 0) // 0^x = 0
            return this;
        if(r.n.signum() < 0) // x^(-y) = 1/(x^y)
            return raise(r.negate()).invert();
        if(n.signum() < 0 && !r.d.equals(BigInteger.ONE)) // (-x)^(y/z) = i*(x^(y/z))  |  z > 1
            return new Complex(ZERO, (Rational) negate().raise(x));

        if(r.n.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
            Rational res = precise == r.precise ? this : new Rational(n, d, false);
            if(!r.n.equals(BigInteger.ONE))
                res = new Rational(res.n.pow(r.n.intValueExact()), res.d.pow(r.n.intValue()), res.precise, false);
            if(!r.d.equals(BigInteger.ONE)) {
                BigInteger[] tmpN, tmpD;
                if(r.d.equals(BigInteger.TWO) && (tmpN = n.sqrtAndRemainder())[1].equals(BigInteger.ZERO) &&
                        (tmpD = d.sqrtAndRemainder())[1].equals(BigInteger.ZERO)) {
                    res = new Rational(tmpN[0], tmpD[0], res.precise, false);
                } else {
                    res = new Rational(BigDecimalMath.pow(toBigDecimal(), new Rational(BigInteger.ONE, r.d).toBigDecimal()), false);
                }
            }
            return res;
        }
        return new Rational(BigDecimalMath.pow(toBigDecimal(), r.toBigDecimal()), false);
    }

    @Override
    public @NotNull Number raiseOther(Number base) {
        return base.raise(this);
    }

    public @NotNull Rational scale(int tenExp) {
        if(tenExp > 0) return new Rational(n.multiply(BigInteger.TEN.pow(tenExp)), d, precise);
        if(tenExp < 0) return new Rational(n.multiply(BigInteger.TEN.pow(-tenExp)), d, precise);
        return this;
    }

    @Override
    public @NotNull Rational abs() {
        if(n.compareTo(BigInteger.ZERO) >= 0) return this;
        return negate();
    }

    @Override
    public @NotNull Rational negate() {
        return new Rational(n.negate(), d, precise);
    }

    @Override
    public @NotNull Rational invert() {
        return new Rational(d, n, precise);
    }

    @Override
    public SimpleNumber equalTo(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.equalTo(this);
        return n.equals(r.n) && d.equals(r.d) ? ONE(precise && r.precise) : ZERO(precise && r.precise);
    }

    @Override
    public SimpleNumber lessThan(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.greaterThanOrEqual(this);
        return n.multiply(r.d).compareTo(r.n.multiply(d)) < 0 ? ONE(precise && r.precise) : ZERO(precise && r.precise);
    }

    @Override
    public SimpleNumber greaterThan(SimpleNumber x) {
        if(!(x instanceof Rational r)) return x.greaterThanOrEqual(this);
        return n.multiply(r.d).compareTo(r.n.multiply(d)) > 0 ? ONE(precise && r.precise) : ZERO(precise && r.precise);
    }



    public static Rational ZERO(boolean precise) {
        return precise ? ZERO : ABOUT_ZERO;
    }

    public static Rational ONE(boolean precise) {
        return precise ? ONE : ABOUT_ONE;
    }


    public static int getPrecision() {
        return precision;
    }

    public static void setPrecision(int precision) {
        Rational.precision = Arguments.checkRange(precision, 0, null);
    }

    @NotNull
    public static ToStringMode getToStringMode() {
        return toStringMode;
    }

    public static void setToStringMode(@NotNull ToStringMode toStringMode) {
        Rational.toStringMode = Arguments.checkNull(toStringMode, "toStringMode");
    }



    public record DetailedToString(String str, boolean precise, boolean isFull) { }

    public enum ToStringMode {
        SMART(ToStringMode::smart),
        DECIMAL_IF_POSSIBLE(ToStringMode::decimalIfPossible),
        FORCE_FRACTION(ToStringMode::forceFraction),
        FORCE_DECIMAL(ToStringMode::forceDecimal),
        SMART_SCIENTIFIC(x -> scientific(x, SMART)),
        DECIMAL_IF_POSSIBLE_SCIENTIFIC(x -> scientific(x, DECIMAL_IF_POSSIBLE)),
        FORCE_FRACTION_SCIENTIFIC(x -> scientific(x, FORCE_FRACTION)),
        FORCE_DECIMAL_SCIENTIFIC(x -> scientific(x, FORCE_DECIMAL));

        private final Function<Rational,DetailedToString> toString;

        ToStringMode(Function<Rational, DetailedToString> toString) {
            this.toString = toString;
        }

        private DetailedToString toString(Rational x) {
            return toString.apply(x);
        }



        private static DetailedToString smart(Rational x) {
            if(x.precise || x.d.compareTo(BigInteger.valueOf(1000)) <= 0) {
                DetailedToString str = decimalIfPossible(x);
                if(str.isFull || str.str.matches(".*[1-9].*"))
                    return str;
                return forceFraction(x);
            }
            return forceDecimal(x);
        }

        private static DetailedToString decimalIfPossible(Rational x) {
            DetailedToString n1 = tryToDecimalStr(x);
            if(n1 != null) return n1;
            return forceFraction(x);
        }

        private static DetailedToString forceFraction(Rational x) {
            if(x.d.equals(BigInteger.ONE))
                return new DetailedToString(x.n.toString(), x.precise, true);
            return new DetailedToString(x.n + "/" + x.d, x.precise, true);
        }

        private static DetailedToString forceDecimal(Rational x) {
            DetailedToString s = tryToDecimalStr(x);
            if(s != null) return s;
            String str = x.toBigDecimal().setScale(x.n.divide(x.d).abs().toString().length() + precision, RoundingMode.HALF_UP).toPlainString();
            int dot = str.indexOf('.');
            if(str.length() - dot - 1 <= precision)
                return new DetailedToString(str, false, true);
            return new DetailedToString(str.substring(0, dot + precision + 1), false, false);
        }

        private static DetailedToString scientific(Rational x, ToStringMode mode) {
            if(x.n.signum() < 0) {
                DetailedToString str = decimalIfPossible(x.negate());
                return new DetailedToString("-" + str.str, str.precise, str.isFull);
            }
            if((x.greaterThanOrEqual(new Rational(0.001)).equals(ONE) &&
                    x.lessThan(new Rational(10000)).equals(ONE)) ||
                    (x.d.equals(BigInteger.ONE) && x.n.compareTo(BigInteger.valueOf(10000000)) < 0)) return mode.toString(x);
            if(x.n.equals(BigInteger.ZERO)) return new DetailedToString("0", x.precise, true);

            // 2/1000 -> 2000/1000 -> 2E-3
            BigInteger n = x.n, d = x.d;
            int e = 0;
            if(n.compareTo(d) < 0) {
                do {
                    e--;
                    n = n.multiply(BigInteger.TEN);
                } while(n.compareTo(d) < 0);
                DetailedToString str = mode.toString(new Rational(n, d, x.precise));
                return new DetailedToString(str.str + "E" + e, str.precise, str.isFull);
            }
            else {
                while(n.compareTo(d) > 0) { // 1000/3 -> 1000/300 -> 10/3E2
                    e++;
                    d = d.multiply(BigInteger.TEN);
                }
                e--;
                n = n.multiply(BigInteger.TEN);
                DetailedToString str = mode.toString(new Rational(n, d, x.precise));
                if(e == 0) return str;
                return new DetailedToString(str.str + "E" + e, str.precise, str.isFull);
            }
        }

        @Nullable
        private static DetailedToString tryToDecimalStr(Rational x) {
            if(x.d.equals(BigInteger.ONE))
                return new DetailedToString(x.n.toString(), x.precise, true);

            if(x.n.signum() < 0) {
                DetailedToString str = decimalIfPossible(x.negate());
                return new DetailedToString("-" + str.str, str.precise, str.isFull);
            }

            BigInteger factor = BigDecimalMath.getFactorToPowerOfTen(x.d);
            if(factor != null)
                return decBase10Str(x, factor);
            return null;
        }

        @NotNull
        private static DetailedToString decBase10Str(Rational x, @NotNull BigInteger factor) {
            if(x.n.compareTo(x.d) < 0) {
                StringBuilder str = new StringBuilder("0.");
                BigInteger n = x.n.multiply(BigInteger.TEN);
                while(n.compareTo(x.d) < 0) {
                    str.append('0');
                    n = n.multiply(BigInteger.TEN);
                }
                str.append(x.n.multiply(factor));
                if(str.length() <= precision + 2)
                    return new DetailedToString(str.toString(), x.precise, true);
                return new DetailedToString(str.substring(0, precision + 3), x.precise, false);
            }
            // 14/10
            BigInteger n = x.n.multiply(factor), d = x.d.multiply(factor);
            String nStr = n.toString();

            int dot = BigDecimalMath.log(BigInteger.TEN, d);
            int dotIndex = nStr.length() - dot;
            if(dot <= precision)
                return new DetailedToString(nStr.substring(0, dotIndex) + '.' + nStr.substring(dotIndex), x.precise, true);
            return new DetailedToString(nStr.substring(0, dotIndex) + '.' + nStr.substring(dotIndex, dotIndex + precision), x.precise, false);
        }
    }
}
