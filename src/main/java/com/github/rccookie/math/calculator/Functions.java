package com.github.rccookie.math.calculator;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.github.rccookie.math.BigDecimalMath;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.math.Real;
import com.github.rccookie.math.Vector;

final class Functions {

    private static final Number LN_2 = ln(new Real(2));

    private Functions() { }

    public static Number min(Number a, Number b) {
        if(a instanceof Function f)
            return f.apply("min($1,$2)", b, Functions::min);
        if(b instanceof Function f)
            return f.apply("min($2,$1)", a, Functions::min);
        if(b == Calculator.UNSPECIFIED)
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
        if(a instanceof Function f)
            return f.apply("max($1,$2)", b, Functions::max);
        if(b instanceof Function f)
            return f.apply("max($2,$1)", a, Functions::max);
        if(b == Calculator.UNSPECIFIED)
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
            case Vector v -> v.apply(Functions::floor);
            case Function f -> f.apply("floor($x)", Functions::floor);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number ceil(Number x) {
        return switch(x) {
            case Rational f -> new Rational(f.n.add(f.d).subtract(BigInteger.ONE).divide(f.d));
            case Real d -> new Real(d.value.setScale(0, RoundingMode.UP), d.precise);
            case Vector v -> v.apply(Functions::ceil);
            case Function f -> f.apply("ceil($x)", Functions::ceil);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number round(Number x) {
        return switch(x) {
            case Rational r -> new Rational(new Real(r).value.setScale(0, RoundingMode.HALF_UP).toBigInteger());
            case Real d -> new Real(d.value.setScale(0, RoundingMode.HALF_UP), d.precise);
            case Vector v -> v.apply(Functions::round);
            case Function f -> f.apply("round($x)", Functions::round);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }



    public static Number sin(Number x) {
        return switch(x) {
            case Rational f -> sin(new Real(f));
            case Real d -> sin(d);
            case Vector v -> v.apply(Functions::sin);
            case Function f -> f.apply("sin($x)", Functions::sin);
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
            case Vector v -> v.apply(Functions::cos);
            case Function f -> f.apply("cos($x)", Functions::cos);
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
            case Vector v -> v.apply(Functions::asin);
            case Function f -> f.apply("asin($x)", Functions::asin);
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
            case Vector v -> v.apply(Functions::acos);
            case Function f -> f.apply("acos($x)", Functions::acos);
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
            case Vector v -> v.apply(Functions::atan);
            case Function f -> f.apply("atan($x)", Functions::atan);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number atan(Real x) {
        return new Real(BigDecimalMath.atan(x.value), false);
    }

    public static Number atan2(Number y, Number x) {
        if(y instanceof Function f)
            return f.apply("atan2($1,$2)", x, Functions::atan2);
        if(x instanceof Function f)
            //noinspection SuspiciousNameCombination
            return f.apply("atan2($2,1)", y, Functions::atan2);
        if(y instanceof Vector vy) {
            if(x instanceof Vector vx)
                return vy.apply(vx, Functions::atan2);
            return vy.apply(yc -> atan2(yc, x));
        }
        if(x instanceof Vector vx)
            return vx.apply(xc -> atan2(y, xc));
        return new Real(Math.atan2(y.toDouble(null), x.toDouble(null)), false);
    }



    public static Number exp(Number x) {
        return switch(x) {
            case Real d -> exp(d);
            case Rational f -> exp(f);
            case Vector v -> v.apply(Functions::exp);
            case Function f -> f.apply("exp($x)", Functions::exp);
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
            case Vector v -> v.apply(Functions::ln);
            case Function f -> f.apply("ln($x)", Functions::ln);
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



    public static Number factorial(Calculator c, Number x) {
        if(x instanceof Vector v)
            return v.apply(n -> factorial(c,n));
        double xd = x.toDouble(c);
        if(xd != (long) xd)
            throw new IllegalArgumentException("Factorial on non-integer");
        if(xd < 0)
            throw new IllegalArgumentException("Factorial on negative number");
        Number res = Number.ONE();
        for(; x.toDouble(c) > 0; x = x.subtract(Number.ONE()))
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



    public static Number sum(Calculator c) {
        return sum(c, c.getVar("$low"), c.getVar("$high"), c.getVar("$f"));
    }

    public static Number sum(Calculator c, Number low, Number high, Number f) {
        if(low instanceof Function lowF)
            return lowF.apply("sum($x,"+high+","+f+")", l -> sum(c, l, high, f));
        if(high instanceof Function highF)
            return highF.apply("sum("+low+",$x,"+f+")", h -> sum(c, low, h, f));
        if(low instanceof Vector lowV) {
            if(high instanceof Vector highV)
                return lowV.apply(highV, (l,h) -> sum(c,l,h,f));
            return lowV.apply(l -> sum(c, l, high, f));
        }
        if(high instanceof Vector highV)
            return highV.apply(h -> sum(c, low, h, f));
        return sum(c, low, high, f instanceof Function ff ? ff : new Function(f, "$?"));
    }

    private static Number sum(Calculator c, Number low, Number high, Function f) {
        Number res = Number.ZERO();
        Number i = low;
        for(double iD=low.toDouble(c), highD=high.toDouble(c); iD<=highD; iD++, i = i.add(Number.ONE()))
            res = res.add(f.evaluate(c, i));
        return res;
    }



    public static Number product(Calculator e) {
        return product(e, e.getVar("$low"), e.getVar("$high"), e.getVar("$f"));
    }

    public static Number product(Calculator e, Number low, Number high, Number f) {
        if(low instanceof Function lowF)
            return lowF.apply("product($x,"+high+","+f+")", l -> product(e, l, high, f));
        if(high instanceof Function highF)
            return highF.apply("product("+low+",$x,"+f+")", h -> product(e, low, h, f));
        if(low instanceof Vector lowV) {
            if(high instanceof Vector highV)
                return lowV.apply(highV, (l,h) -> product(e,l,h,f));
            return lowV.apply(l -> product(e, l, high, f));
        }
        if(high instanceof Vector highV)
            return highV.apply(h -> product(e, low, h, f));
        return product(e, low, high, f instanceof Function ff ? ff : new Function(f, "$?"));
    }

    private static Number product(Calculator c, Number low, Number high, Function f) {
        Number res = Number.ONE();
        Number i = low;
        for(double iD=low.toDouble(c), highD=high.toDouble(c); iD<=highD; iD++, i = i.add(Number.ONE()))
            res = res.multiply(f.evaluate(c, i));
        return res;
    }
}
