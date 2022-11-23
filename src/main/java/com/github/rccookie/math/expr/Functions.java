package com.github.rccookie.math.expr;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.function.BinaryOperator;

import com.github.rccookie.math.BigDecimalMath;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.math.Real;
import com.github.rccookie.math.Vector;

public final class Functions {

    private static final String SIGMA, PI;
    static {
        CharsetEncoder e = Charset.defaultCharset().newEncoder();
        SIGMA = e.canEncode('\u03A3') ? "\u03A3" : "sum";
        PI = e.canEncode('\u03A0') ? "\u03A0" : "product";
    }

    public static final Expression.Function ABS = new HardcodedFunction("abs", Number::abs);
    public static final Expression.Function SQRT = new HardcodedFunction("sqrt", Number::sqrt);
    public static final Expression.Function HYPOT = new HardcodedFunction("hypot", Functions::hypot);
    public static final Expression.Function EXP = new HardcodedFunction("exp", Functions::exp);
    public static final Expression.Function LN = new HardcodedFunction("ln", Functions::ln);
    public static final Expression.Function LD = new HardcodedFunction("ld", Functions::ld);
    public static final Expression.Function LOG = new HardcodedFunction("log", Functions::log);
    public static final Expression.Function FACTORIAL = new HardcodedFunction("factorial", Functions::factorial);
    public static final Expression.Function MIN = new HardcodedFunction("min", (BinaryOperator<Number>) Functions::min);
    public static final Expression.Function MAX = new HardcodedFunction("max", (BinaryOperator<Number>) Functions::max);
    public static final Expression.Function FLOOR = new HardcodedFunction("floor", Functions::floor);
    public static final Expression.Function CEIL = new HardcodedFunction("ceil", Functions::ceil);
    public static final Expression.Function ROUND = new HardcodedFunction("round", Functions::round);
    public static final Expression.Function SIN = new HardcodedFunction("sin", Functions::sin);
    public static final Expression.Function COS = new HardcodedFunction("cos", Functions::cos);
    public static final Expression.Function TAN = new HardcodedFunction("tan", Functions::tan);
    public static final Expression.Function ASIN = new HardcodedFunction("asin", Functions::asin);
    public static final Expression.Function ACOS = new HardcodedFunction("acos", Functions::acos);
    public static final Expression.Function ATAN = new HardcodedFunction("atan", Functions::atan);
    public static final Expression.Function ATAN2 = new HardcodedFunction("atan2", Functions::atan2);
    public static final Expression.Function GET = new HardcodedFunction("get", Functions::get);
    public static final Expression.Function SIZE = new HardcodedFunction("size", Functions::size);
    public static final Expression.Function CROSS = new HardcodedFunction("cross", Functions::cross);
    public static final Expression.Function RAD_TO_DEG = new HardcodedFunction("deg", Functions::radToDeg);
    public static final Expression.Function DEG_TO_RAD = new HardcodedFunction("rad", Functions::degToRad);
    public static final Expression.Function SUM = new HardcodedFunction(SIGMA, (l,p) -> sum(l,p[0], p[1], p[2]), "low", "high", "f");
    public static final Expression.Function PRODUCT = new HardcodedFunction(PI, (l,p) -> product(l,p[0], p[1], p[2]), "low", "high", "f");

    private static final Number LN_2 = ln(new Real(2));

    private Functions() { }

    public static Number min(Number a, Number b) {
        if(a instanceof Expression.Function f)
            return f.derive("min", "min($1,$2)", b, Functions::min);
        if(b instanceof Expression.Function f)
            return f.derive("min", "min($2,$1)", a, Functions::min);
        if(b == SymbolLookup.UNSPECIFIED)
            return min(a);
        Number relation = a.lessThan(b);
        return a.multiply(relation).add(b.multiply(Number.ONE().subtract(relation)));
    }

    public static Number min(Number x) {
        if(!(x instanceof Vector v)) return x;
        Number min = v.get(0);
        for(int i=1; i<v.size(); i++)
            min = min(min,v.get(i));
        return min;
    }

    public static Number max(Number a, Number b) {
        if(a instanceof Expression.Function f)
            return f.derive("max", "max($1,$2)", b, Functions::max);
        if(b instanceof Expression.Function f)
            return f.derive("max", "max($2,$1)", a, Functions::max);
        if(b == SymbolLookup.UNSPECIFIED)
            return max(a);
        Number relation = a.greaterThan(b);
        return a.multiply(relation).add(b.multiply(Number.ONE().subtract(relation)));
    }

    public static Number max(Number x) {
        if(!(x instanceof Vector v)) return x;
        Number max = v.get(0);
        for(int i=1; i<v.size(); i++)
            max = max(max,v.get(i));
        return max;
    }



    public static Number floor(Number x) {
        return switch(x) {
            case Rational f -> new Rational(f.n.divide(f.d));
            case Real d -> new Real(d.value.setScale(0, RoundingMode.DOWN), d.precise);
            case Vector v -> v.derive(Functions::floor);
            case Expression.Function f -> f.derive("floor", "floor($x)", Functions::floor);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number ceil(Number x) {
        return switch(x) {
            case Rational f -> new Rational(f.n.add(f.d).subtract(BigInteger.ONE).divide(f.d));
            case Real d -> new Real(d.value.setScale(0, RoundingMode.UP), d.precise);
            case Vector v -> v.derive(Functions::ceil);
            case Expression.Function f -> f.derive("ceil", "ceil($x)", Functions::ceil);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number round(Number x) {
        return switch(x) {
            case Rational r -> new Rational(new Real(r).value.setScale(0, RoundingMode.HALF_UP).toBigInteger());
            case Real d -> new Real(d.value.setScale(0, RoundingMode.HALF_UP), d.precise);
            case Vector v -> v.derive(Functions::round);
            case Expression.Function f -> f.derive("round", "round($x)", Functions::round);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }



    public static Number sin(Number x) {
        return switch(x) {
            case Rational f -> sin(new Real(f));
            case Real d -> sin(d);
            case Vector v -> v.derive(Functions::sin);
            case Expression.Function f -> f.derive("sin", "sin($x)", Functions::sin);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number sin(Real x) {
        if(x.precise && x.equals(Real.ZERO))
            return Number.ZERO();
        return new Real(BigDecimalMath.sin(x.value), false);
    }

    public static Number cos(Number x) {
        return switch(x) {
            case Rational f -> cos(new Real(f));
            case Real d -> cos(d);
            case Vector v -> v.derive(Functions::cos);
            case Expression.Function f -> f.derive("cos", "cos($x)", Functions::cos);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number cos(Real x) {
        if(x.precise && x.equals(Real.ZERO))
            return Number.ONE();
        return new Real(BigDecimalMath.cos(x.value), false);
    }

    public static Number tan(Number x) {
        return sin(x).divide(cos(x));
    }



    public static Number asin(Number x) {
        return switch(x) {
            case Rational f -> asin(new Real(f));
            case Real d -> asin(d);
            case Vector v -> v.derive(Functions::asin);
            case Expression.Function f -> f.derive("asin", "asin($x)", Functions::asin);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number asin(Real x) {
        if(x.equals(Real.MINUS_ONE)) return Number.PI().divide(-2);
        if(x.equals(Real.ZERO)) return x;
        if(x.equals(Real.ONE)) return Number.PI().divide(2);
        return new Real(BigDecimalMath.asin(x.value), false);
    }

    public static Number acos(Number x) {
        return switch(x) {
            case Rational f -> acos(new Real(f));
            case Real d -> acos(d);
            case Vector v -> v.derive(Functions::acos);
            case Expression.Function f -> f.derive("acos", "acos($x)", Functions::acos);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number acos(Real x) {
        if(x.equals(Real.ONE)) return Real.zero(x.precise);
        if(x.equals(Real.ZERO)) return Number.PI().divide(2);
        if(x.equals(Real.MINUS_ONE)) return Number.PI();
        return new Real(BigDecimalMath.acos(x.value), false);
    }

    public static Number atan(Number x) {
        return switch(x) {
            case Rational f -> atan(new Real(f));
            case Real d -> atan(d);
            case Vector v -> v.derive(Functions::atan);
            case Expression.Function f -> f.derive("atan", "atan($x)", Functions::atan);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number atan(Real x) {
        return new Real(BigDecimalMath.atan(x.value), false);
    }

    public static Number atan2(Number y, Number x) {
        if(y instanceof Expression.Function f)
            return f.derive("atan", "atan2($1,$2)", x, Functions::atan2);
        if(x instanceof Expression.Function f)
            return f.derive("atan", "atan2($2,1)", y, Functions::atan2);
        if(y instanceof Vector vy) {
            if(x instanceof Vector vx)
                return vy.derive(vx, Functions::atan2);
            return vy.derive(yc -> atan2(yc, x));
        }
        if(x instanceof Vector vx)
            return vx.derive(xc -> atan2(y, xc));
        return new Real(Math.atan2(y.toDouble(null), x.toDouble(null)), false);
    }



    public static Number exp(Number x) {
        return switch(x) {
            case Real d -> exp(d);
            case Rational f -> exp(f);
            case Vector v -> v.derive(Functions::exp);
            case Expression.Function f -> f.derive("exp", "exp($x)", Functions::exp);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static Number exp(Real x) {
        if(x.equals(Real.ZERO))
            return Real.one(x.precise);
        return new Real(BigDecimalMath.exp(x.value), false);
    }

    public static Number exp(Rational x) {
        if(x.n.equals(BigInteger.ZERO))
            return Number.ONE();
        return new Real(Math.exp(x.toDouble()), false);
    }


    public static Number ln(Number x) {
        return switch(x) {
            case Real d -> ln(d);
            case Rational f -> ln(f);
            case Vector v -> v.derive(Functions::ln);
            case Expression.Function f -> f.derive("ln", "ln($x)", Functions::ln);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static Number ln(Real x) {
        if(x.equals(Real.ONE))
            return new Real(0, x.precise);
        return new Real(BigDecimalMath.log(x.value), false);
    }

    public static Number ln(Rational x) {
        if(x.n.equals(x.d))
            return Number.ZERO();
        return new Real(Math.log(x.toDouble()), false);
    }

    public static Number ld(Number x) {
        return ln(x).divide(LN_2);
    }


    public static Number log(Number base, Number x) {
        return ln(x).divide(ln(base));
    }


    public static Vector cross(Number a, Number b) {
        if(!(a instanceof Vector av) || !(b instanceof Vector bv))
            throw new IllegalArgumentException("Cross product requires two vectors");
        return cross(av, bv);
    }

    public static Vector cross(Vector a, Vector b) {
        return a.cross(b);
    }


    public static Number get(Number v, Number i) {
        return Vector.asVector(v).get(i.subtract(1));
    }

    public static Number size(Number x) {
        return x instanceof Vector v ? new Rational(v.size()) : Number.ONE();
    }



    public static Number radToDeg(Number x) {
        return x.multiply(Number.RAD_TO_DEG());
    }

    public static Number degToRad(Number x) {
        return x.multiply(Number.DEG_TO_RAD());
    }

    public static Number fromPercent(Number x) {
        return x.divide(100);
    }



    public static Number square(Number x) {
        return x.multiply(x);
    }

    public static Number cube(Number x) {
        return x.multiply(x).multiply(x);
    }


    public static Number hypot(Number a, Number b) {
        return a.multiply(a).add(b.multiply(b)).sqrt();
    }



    public static Number factorial(Number x) {
        if(x instanceof Vector v)
            return v.derive(Functions::factorial);
        double xd = x.toDouble();
        if(xd != (long) xd)
            throw new IllegalArgumentException("Factorial on non-integer");
        if(xd < 0)
            throw new IllegalArgumentException("Factorial on negative number");
        Number res = Number.ONE();
        for(; x.toDouble() > 0; x = x.subtract(Number.ONE()))
            res = res.multiply(x);
        return res;
    }

//    public static Number factorial(Number x) {
//        if(x instanceof Vector v)
//            return v.apply(n -> factorial(n));
//        if(x instanceof Expression
//        double xd = x.toDouble(c);
//        if(xd != (long) xd)
//            throw new IllegalArgumentException("Factorial on non-integer");
//        if(xd < 0)
//            throw new IllegalArgumentException("Factorial on negative number");
//        Number res = Number.ONE();
//        for(; x.toDouble(c) > 0; x = x.subtract(Number.ONE()))
//            res = res.multiply(x);
//        return res;
//    }



    public static Number sum(SymbolLookup c, Number low, Number high, Number f) {
        if(low instanceof Expression.Function lowF)
            return lowF.derive(SIGMA, "sum($x,"+high+","+f+")", l -> sum(c, l, high, f));
        if(high instanceof Expression.Function highF)
            return highF.derive(SIGMA, "sum("+low+",$x,"+f+")", h -> sum(c, low, h, f));
        if(low instanceof Vector lowV) {
            if(high instanceof Vector highV)
                return lowV.derive(highV, (l, h) -> sum(c,l,h,f));
            return lowV.derive(l -> sum(c, l, high, f));
        }
        if(high instanceof Vector highV)
            return highV.derive(h -> sum(c, low, h, f));
        return sum(c, low, high, f instanceof Expression.Function ff ? ff : new HardcodedFunction("_f", "_", l -> f));
    }

    private static Number sum(SymbolLookup c, Number low, Number high, Expression.Function f) {
        Number res = Number.ZERO();
        Number i = low;
        for(double iD=low.toDouble(c), highD=high.toDouble(c); iD<=highD; iD++, i = i.add(Number.ONE()))
            res = res.add(f.evaluate(c, i));
        return res;
    }



    public static Number product(SymbolLookup e, Number low, Number high, Number f) {
        if(low instanceof Expression.Function lowF)
            return lowF.derive(PI, "product($x,"+high+","+f+")", l -> product(e, l, high, f));
        if(high instanceof Expression.Function highF)
            return highF.derive(PI, "product("+low+",$x,"+f+")", h -> product(e, low, h, f));
        if(low instanceof Vector lowV) {
            if(high instanceof Vector highV)
                return lowV.derive(highV, (l, h) -> product(e,l,h,f));
            return lowV.derive(l -> product(e, l, high, f));
        }
        if(high instanceof Vector highV)
            return highV.derive(h -> product(e, low, h, f));
        return product(e, low, high, f instanceof Expression.Function ff ? ff : new HardcodedFunction("_f", "_", l -> f));
    }

    private static Number product(SymbolLookup c, Number low, Number high, Expression.Function f) {
        Number res = Number.ONE();
        Number i = low;
        for(double iD=low.toDouble(c), highD=high.toDouble(c); iD<=highD; iD++, i = i.add(Number.ONE()))
            res = res.multiply(f.evaluate(c, i));
        return res;
    }
}
