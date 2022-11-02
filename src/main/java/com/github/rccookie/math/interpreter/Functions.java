package com.github.rccookie.math.interpreter;

import com.github.rccookie.math.Decimal;
import com.github.rccookie.math.Fraction;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Vector;

final class Functions {

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
            case Fraction f -> new Fraction(Math.floorDiv(f.n, f.d));
            case Decimal d -> new Decimal(Math.floor(d.value), d.precise);
            case Vector v -> v.apply(Functions::floor);
            case Function f -> f.apply("floor($x)", Functions::floor);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number ceil(Number x) {
        return switch(x) {
            case Fraction f -> new Fraction(Math.ceilDiv(f.n, f.d));
            case Decimal d -> new Decimal(Math.ceil(d.value), d.precise);
            case Vector v -> v.apply(Functions::ceil);
            case Function f -> f.apply("ceil($x)", Functions::ceil);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number round(Number x) {
        return switch(x) {
            case Fraction f -> new Fraction((long) ((double) f.n/f.d + 0.5));
            case Decimal d -> new Decimal(Math.round(d.value), d.precise);
            case Vector v -> v.apply(Functions::round);
            case Function f -> f.apply("round($x)", Functions::round);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }



    public static Number sin(Number x) {
        return switch(x) {
            case Fraction f -> sin(new Decimal(f));
            case Decimal d -> sin(d);
            case Vector v -> v.apply(Functions::sin);
            case Function f -> f.apply("sin($x)", Functions::sin);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number sin(Decimal x) {
        if(x.precise && x.value == 0)
            return Number.ZERO();
        return new Decimal(Math.sin(x.value), false);
    }

    public static Number cos(Number x) {
        return switch(x) {
            case Fraction f -> cos(new Decimal(f));
            case Decimal d -> cos(d);
            case Vector v -> v.apply(Functions::cos);
            case Function f -> f.apply("cos($x)", Functions::cos);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number cos(Decimal x) {
        if(x.precise && x.value == 0)
            return Number.ONE();
        return new Decimal(Math.cos(x.value), false);
    }

    public static Number tan(Number x) {
        return switch(x) {
            case Fraction f -> tan(new Decimal(f));
            case Decimal d -> tan(d);
            case Vector v -> v.apply(Functions::tan);
            case Function f -> f.apply("tan($x)", Functions::tan);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number tan(Decimal x) {
        return new Decimal(Math.tan(x.value), false);
    }



    public static Number asin(Number x) {
        return switch(x) {
            case Fraction f -> asin(new Decimal(f));
            case Decimal d -> asin(d);
            case Vector v -> v.apply(Functions::asin);
            case Function f -> f.apply("asin($x)", Functions::asin);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number asin(Decimal x) {
        if(x.precise && x.value == 0)
            return Number.ZERO();
        return new Decimal(Math.asin(x.value), false);
    }

    public static Number acos(Number x) {
        return switch(x) {
            case Fraction f -> acos(new Decimal(f));
            case Decimal d -> acos(d);
            case Vector v -> v.apply(Functions::acos);
            case Function f -> f.apply("acos($x)", Functions::acos);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number acos(Decimal x) {
        if(x.precise && x.value == 1)
            return Number.ZERO();
        return new Decimal(Math.sin(x.value), false);
    }

    public static Number atan(Number x) {
        return switch(x) {
            case Fraction f -> atan(new Decimal(f));
            case Decimal d -> atan(d);
            case Vector v -> v.apply(Functions::atan);
            case Function f -> f.apply("atan($x)", Functions::atan);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number atan(Decimal x) {
        return new Decimal(Math.atan(x.value), false);
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
        return new Decimal(Math.atan2(y.toDouble(), x.toDouble()), false);
    }



    public static Number exp(Number x) {
        return switch(x) {
            case Decimal d -> exp(d);
            case Fraction f -> exp(f);
            case Vector v -> v.apply(Functions::exp);
            case Function f -> f.apply("exp($x)", Functions::exp);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static Number exp(Decimal x) {
        if(x.value == 0)
            return Decimal.one(x.precise);
        return new Decimal(Math.exp(x.value), false);
    }

    public static Number exp(Fraction x) {
        if(x.n == 0)
            return Number.ONE();
        return new Decimal(Math.exp(x.toDouble()), false);
    }


    public static Number ln(Number x) {
        return switch(x) {
            case Decimal d -> ln(d);
            case Fraction f -> ln(f);
            case Vector v -> v.apply(Functions::ln);
            case Function f -> f.apply("ln($x)", Functions::ln);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static Number ln(Decimal x) {
        if(x.value == 1)
            return new Decimal(0, x.precise);
        return new Decimal(Math.log(x.value), false);
    }

    public static Number ln(Fraction x) {
        if(x.n == x.d)
            return Number.ZERO();
        return new Decimal(Math.log(x.toDouble()), false);
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
        return x instanceof Vector v ? new Fraction(v.size()) : Number.ONE();
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



    public static Number factorial(Number x) {
        if(x instanceof Vector v)
            return v.apply(Functions::factorial);
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



    public static Number sum(Calculator e) {
        return sum(e, e.getVar("$low"), e.getVar("$high"), e.getVar("$f"));
    }

    public static Number sum(Calculator e, Number low, Number high, Number f) {
        if(low instanceof Function lowF)
            return lowF.apply("sum($x,"+high+","+f+")", l -> sum(e, l, high, f));
        if(high instanceof Function highF)
            return highF.apply("sum("+low+",$x,"+f+")", h -> sum(e, low, h, f));
        if(low instanceof Vector lowV) {
            if(high instanceof Vector highV)
                return lowV.apply(highV, (l,h) -> sum(e,l,h,f));
            return lowV.apply(l -> sum(e, l, high, f));
        }
        if(high instanceof Vector highV)
            return highV.apply(h -> sum(e, low, h, f));
        return sum(e, low, high, f instanceof Function ff ? ff : new Function(f, "$?"));
    }

    private static Number sum(Calculator e, Number low, Number high, Function f) {
        Number res = Number.ZERO();
        Number i = low;
        for(double iD=low.toDouble(), highD=high.toDouble(); iD<=highD; iD++, i = i.add(Number.ONE()))
            res = res.add(f.evaluate(e, i));
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

    private static Number product(Calculator e, Number low, Number high, Function f) {
        Number res = Number.ONE();
        Number i = low;
        for(double iD=low.toDouble(), highD=high.toDouble(); iD<=highD; iD++, i = i.add(Number.ONE()))
            res = res.multiply(f.evaluate(e, i));
        return res;
    }
}
