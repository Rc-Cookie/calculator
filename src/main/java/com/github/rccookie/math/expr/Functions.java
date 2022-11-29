package com.github.rccookie.math.expr;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.function.BinaryOperator;

import com.github.rccookie.math.BigDecimalMath;
import com.github.rccookie.math.Complex;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.math.Real;
import com.github.rccookie.math.SimpleNumber;
import com.github.rccookie.math.Vector;
import com.github.rccookie.math.solve.Polynom;

import static com.github.rccookie.math.Number.*;

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
    public static final Expression.Function ARGUMENT = new HardcodedFunction("arg", Functions::argument);
    public static final Expression.Function GET = new HardcodedFunction("get", Functions::get);
    public static final Expression.Function SIZE = new HardcodedFunction("size", Functions::size);
    public static final Expression.Function NORMALIZE = new HardcodedFunction("norm", Functions::normalize);
    public static final Expression.Function CROSS = new HardcodedFunction("cross", Functions::cross);
    public static final Expression.Function RAD_TO_DEG = new HardcodedFunction("deg", Functions::radToDeg);
    public static final Expression.Function DEG_TO_RAD = new HardcodedFunction("rad", Functions::degToRad);
    public static final Expression.Function SUM = new HardcodedFunction(SIGMA, (l,p) -> sum(l,p[0], p[1], p[2]), "low", "high", "f");
    public static final Expression.Function PRODUCT = new HardcodedFunction(PI, (l,p) -> product(l,p[0], p[1], p[2]), "low", "high", "f");

    public static final Expression.Function POLYNOM = new HardcodedFunction("poly", (l,p) -> polynom(l,p[0]), "p");
    public static final Expression.Function DERIVATIVE = new HardcodedFunction("der", (l,p) -> derivative(l,p[0],p[1],p[2]), "p", "deg", "ind");
    public static final Expression.Function ANTIDERIVATIVE = new HardcodedFunction("antiDer", (l,p) -> antiderivative(l,p[0],p[1],p[2]), "p", "deg", "ind");
    public static final Expression.Function INTEGRATE = new HardcodedFunction("int", (l,p) -> integrate(l,p[0],p[1],p[2],p[3]), "p", "a", "b", "ind");

    private static final Number LN_2 = ln(new Real(2));

    private static final int PRE = Token.MULTIPLY.precedence();

    private Functions() { }

    public static Number min(Number a, Number b) {
        if(a instanceof Expression.Function f)
            return f.derive("min", "min($1,$2)", b, PRE, Functions::min);
        if(b instanceof Expression.Function f)
            return f.derive("min", "min($2,$1)", a, PRE, Functions::min);
        if(b == SymbolLookup.UNSPECIFIED)
            return min(a);
        Number relation = a.lessThan(b);
        return a.multiply(relation).add(b.multiply(ONE().subtract(relation)));
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
            return f.derive("max", "max($1,$2)", b, PRE, Functions::max);
        if(b instanceof Expression.Function f)
            return f.derive("max", "max($2,$1)", a, PRE, Functions::max);
        if(b == SymbolLookup.UNSPECIFIED)
            return max(a);
        Number relation = a.greaterThan(b);
        return a.multiply(relation).add(b.multiply(ONE().subtract(relation)));
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
            case Complex c -> new Complex((SimpleNumber) floor(c.re), (SimpleNumber) floor(c.im));
            case Vector v -> v.derive(Functions::floor);
            case Expression.Function f -> f.derive("floor", "floor($x)", PRE, Functions::floor);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number ceil(Number x) {
        return switch(x) {
            case Rational f -> new Rational(f.n.add(f.d).subtract(BigInteger.ONE).divide(f.d));
            case Real d -> new Real(d.value.setScale(0, RoundingMode.UP), d.precise);
            case Complex c -> new Complex((SimpleNumber) ceil(c.re), (SimpleNumber) ceil(c.im));
            case Vector v -> v.derive(Functions::ceil);
            case Expression.Function f -> f.derive("ceil", "ceil($x)", PRE, Functions::ceil);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number round(Number x) {
        return switch(x) {
            case Rational r -> new Rational(new Real(r).value.setScale(0, RoundingMode.HALF_UP).toBigInteger());
            case Real d -> new Real(d.value.setScale(0, RoundingMode.HALF_UP), d.precise);
            case Complex c -> new Complex((SimpleNumber) round(c.re), (SimpleNumber) round(c.im));
            case Vector v -> v.derive(Functions::round);
            case Expression.Function f -> f.derive("round", "round($x)", PRE, Functions::round);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }



    public static Number sin(Number x) {
        return switch(x) {
            case SimpleNumber n -> sin(n);
            case Complex c -> sin(c);
            case Vector v -> v.derive(Functions::sin);
            case Expression.Function f -> f.derive("sin", "sin($x)", PRE, Functions::sin);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static SimpleNumber sin(SimpleNumber x) {
        return switch(x) {
            case Rational f -> sin(new Real(f));
            case Real d -> sin(d);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static SimpleNumber sin(Real x) {
        if(x.equals(Real.ZERO))
            return Real.zero(x.precise);
        return new Real(BigDecimalMath.sin(x.value), false);
    }

    public static Number sin(Complex x) {
        if(x.isReal())
            return sin(x.re);
        return im(exp(x));
    }


    public static Number cos(Number x) {
        return switch(x) {
            case SimpleNumber n -> cos(n);
            case Complex c -> cos(c);
            case Vector v -> v.derive(Functions::cos);
            case Expression.Function f -> f.derive("cos", "cos($x)", PRE, Functions::cos);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static SimpleNumber cos(SimpleNumber x) {
        return switch(x) {
            case Rational f -> cos(new Real(f));
            case Real d -> cos(d);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static SimpleNumber cos(Real x) {
        if(x.equals(Real.ZERO))
            return Real.one(x.precise);
        return new Real(BigDecimalMath.cos(x.value), false);
    }

    public static Number cos(Complex x) {
        if(x.isReal())
            return cos(x.re);
        return re(exp(x));
    }

    public static Number tan(Number x) {
        return sin(x).divide(cos(x));
    }



    public static Number asin(Number x) {
        return switch(x) {
            case Rational f -> asin(new Real(f));
            case Real d -> asin(d);
            case Complex c -> asin(c);
            case Vector v -> v.derive(Functions::asin);
            case Expression.Function f -> f.derive("asin", "asin($x)", PRE, Functions::asin);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number asin(Real x) {
        if(x.equals(Real.MINUS_ONE)) return PI().divide(-2);
        if(x.equals(Real.ZERO)) return x;
        if(x.equals(Real.ONE)) return PI().divide(2);
        return new Real(BigDecimalMath.asin(x.value), false);
    }

    public static Number asin(Complex x) {
        if(x.isReal())
            return asin(x.re);
        Number oneMinusXSqr = ONE().subtract(square(x));
        return I().invert().multiply(ln(
                I().multiply(x).add(
                        oneMinusXSqr.abs().sqrt()
                        .multiply(exp(I().divide(2).multiply(argument(oneMinusXSqr))))
                )
        ));
    }

    public static Number acos(Number x) {
        return switch(x) {
            case Rational f -> acos(new Real(f));
            case Real d -> acos(d);
            case Complex c -> acos(c);
            case Vector v -> v.derive(Functions::acos);
            case Expression.Function f -> f.derive("acos", "acos($x)", PRE, Functions::acos);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static Number acos(Real x) {
        if(x.equals(Real.ONE)) return Real.zero(x.precise);
        if(x.equals(Real.ZERO)) return PI().divide(2);
        if(x.equals(Real.MINUS_ONE)) return PI();
        return new Real(BigDecimalMath.acos(x.value), false);
    }

    public static Number acos(Complex x) {
        if(x.isReal())
            return acos(x.re);
        Number oneMinusXSqr = ONE().subtract(square(x));
        return I().invert().multiply(ln(
                x.add(
                        I().multiply(oneMinusXSqr.abs().sqrt())
                        .multiply(exp(I().divide(2).multiply(argument(oneMinusXSqr))))
                )
        ));
    }


    public static Number atan(Number x) {
        return switch(x) {
            case SimpleNumber n -> atan(n);
            case Complex c -> atan(c);
            case Vector v -> v.derive(Functions::atan);
            case Expression.Function f -> f.derive("atan", "atan($x)", PRE, Functions::atan);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static SimpleNumber atan(SimpleNumber x) {
        return switch(x) {
            case Rational r -> atan(new Real(r));
            case Real r -> atan(r);
            default -> throw new UnsupportedOperationException(""+x);
        };
    }

    public static SimpleNumber atan(Real x) {
        return new Real(BigDecimalMath.atan(x.value), false);
    }

    public static Number atan(Complex x) {
        // 1/(2i) * ln((i-x)/(i+x))
        return I().multiply(2).invert().multiply(ln(I().subtract(x).divide(I().add(x))));
    }


    public static Number atan2(Number y, Number x) {
        if(y instanceof Expression.Function f)
            return f.derive("atan2", "atan2($1,$2)", x, PRE, Functions::atan2);
        if(x instanceof Expression.Function f)
            return f.derive("atan2", "atan2($2,1)", y, PRE, Functions::atan2);
        if(y instanceof Vector vy) {
            if(x instanceof Vector vx)
                return vy.derive(vx, Functions::atan2);
            return vy.derive(yc -> atan2(yc, x));
        }
        if(x instanceof Vector vx)
            return vx.derive(xc -> atan2(y, xc));
        if(!(x instanceof SimpleNumber && y instanceof SimpleNumber))
            throw new UnsupportedOperationException();

        if(x.equals(ZERO())) {
            if(y.greaterThan(ZERO()).equals(ONE()))
                return PI().divide(2);
            if(!y.equals(ZERO()))
                return PI().divide(-2);
            throw new ArithmeticException("atan2 of 0,0");
        }
        Number atan = atan(y.divide(x));
        if(x.greaterThan(ZERO()).equals(ONE()))
            return atan;
        if(y.greaterThanOrEqual(ZERO()).equals(ONE()))
            return atan.add(PI());
        return atan.subtract(PI());
    }



    public static Number exp(Number x) {
        return switch(x) {
            case SimpleNumber n -> exp(n);
            case Complex c -> exp(c);
            case Vector v -> v.derive(Functions::exp);
            case Expression.Function f -> f.derive("exp", "exp($x)", PRE, Functions::exp);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static SimpleNumber exp(SimpleNumber x) {
        return switch(x) {
            case Real r -> exp(r);
            case Rational r -> exp(r);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static SimpleNumber exp(Real x) {
        if(x.equals(Real.ZERO))
            return Real.one(x.precise);
        return new Real(BigDecimalMath.exp(x.value), false);
    }

    public static SimpleNumber exp(Rational x) {
        if(x.n.equals(BigInteger.ZERO))
            return ONE();
        return new Real(Math.exp(x.toDouble()), false);
    }

    public static Number exp(Complex x) {
        if(x.isReal())
            return exp(x.re);
        return Complex.fromPolar(x.im, exp(x.re));
    }


    public static Number ln(Number x) {
        return switch(x) {
            case Real d -> ln(d);
            case Rational f -> ln(f);
            case Complex c -> ln(c);
            case Vector v -> v.derive(Functions::ln);
            case Expression.Function f -> f.derive("ln", "ln($x)", PRE, Functions::ln);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static Number ln(Real x) {
        if(x.equals(Number.ZERO()))
            throw new ArithmeticException("ln 0 is undefined");
        if(x.equals(Real.ONE))
            return Real.zero(x.precise);
        if(x.lessThan(ZERO()).equals(ONE()))
            return ln(new Complex(x));
        return new Real(BigDecimalMath.log(x.value), false);
    }

    public static Number ln(Rational x) {
        if(x.equals(Number.ZERO()))
            throw new ArithmeticException("ln 0 is undefined");
        if(x.n.equals(x.d))
            return ZERO();
        if(x.lessThan(ZERO()).equals(ONE()))
            return ln(new Complex(x));
        return new Real(Math.log(x.toDouble()), false);
    }

    public static Number ln(Complex x) {
        //noinspection EqualsBetweenInconvertibleTypes
        if(x.equals(Number.ZERO()))
            throw new ArithmeticException("ln 0 is undefined");
        if(x.isReal() && x.re.greaterThan(ZERO()).equals(ONE()))
            return ln(x.re);
        return ln(x.abs()).add(I().multiply(x.theta()));
    }


    public static Number ld(Number x) {
        return ln(x).divide(LN_2);
    }


    public static Number log(Number base, Number x) {
        return ln(x).divide(ln(base));
    }


    public static Number argument(Number x) {
        return switch(x) {
            case SimpleNumber n -> {
                if(n.greaterThan(ZERO()).equals(ONE()))
                    yield ZERO();
                if(!n.equals(ZERO()))
                    yield PI();
                throw new ArithmeticException("Argument of 0 is undefined");
            }
            case Complex c -> c.theta();
            case Vector v -> v.derive(Functions::argument);
            case Expression.Function f -> f.derive("argument", "arg($1,$2)", PRE, Functions::argument);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static Number re(Number x) {
        return switch(x) {
            case SimpleNumber n -> n;
            case Complex c -> c.re;
            case Vector v -> v.derive(Functions::re);
            case Expression.Function f -> f.derive("re", "re($1,$2)", PRE, Functions::re);
            default -> throw new UnsupportedOperationException();
        };
    }

    public static Number im(Number x) {
        return switch(x) {
            case SimpleNumber n -> n;
            case Complex c -> c.im;
            case Vector v -> v.derive(Functions::im);
            case Expression.Function f -> f.derive("im", "im($1,$2)", PRE, Functions::im);
            default -> throw new UnsupportedOperationException();
        };
    }


    public static Number cross(Number a, Number b) {
        if(a instanceof Expression.Function f)
            return f.derive("cross", "cross($1,$2)", b, PRE, Functions::cross);
        if(b instanceof Expression.Function f)
            return f.derive("cross", "cross($2,$1)", a, PRE, Functions::cross);
        if(!(a instanceof Vector av) || !(b instanceof Vector bv))
            throw new IllegalArgumentException("Cross product requires two vectors");
        return cross(av, bv);
    }

    public static Vector cross(Vector a, Vector b) {
        return a.cross(b);
    }


    public static Number get(Number v, Number i) {
        if(v instanceof Expression.Function f)
            return f.derive("get", "get($1,$2)", i, PRE, Functions::get);
        if(i instanceof Expression.Function f)
            return f.derive("get", "get($2,$1)", v, PRE, Functions::get);
        return Vector.asVector(v).get(i.subtract(1));
    }

    public static Number size(Number x) {
        if(x instanceof Expression.Function f)
            return f.derive("size", "size($x)", PRE, Functions::size);
        return x instanceof Vector v ? new Rational(v.size()) : ONE();
    }

    public static Number normalize(Number x) {
        return switch(x) {
            case Vector v -> v.normalize();
            case Complex c -> c.normalize();
            case Expression.Function f -> f.derive("norm", "norm($x)", PRE, Functions::normalize);
            case SimpleNumber n -> n.greaterThan(ZERO()).subtract(n.lessThan(ZERO()));
            default -> throw new UnsupportedOperationException(""+x);
        };
    }



    public static Number radToDeg(Number x) {
        return x.multiply(RAD_TO_DEG());
    }

    public static Number degToRad(Number x) {
        return x.multiply(DEG_TO_RAD());
    }

    public static Number fromPercent(Number x) {
        return x.divide(100);
    }



    public static Number square(Number x) {
        return x.raise(TWO());
    }

    public static Number cube(Number x) {
        return x.raise(new Rational(3));
    }


    public static Number hypot(Number a, Number b) {
        return square(a).add(square(b)).sqrt();
    }



    public static Number factorial(Number x) {
        if(x instanceof Vector v)
            return v.derive(Functions::factorial);
        double xd = x.toDouble();
        if(xd != (long) xd)
            throw new IllegalArgumentException("Factorial on non-integer");
        if(xd < 0)
            throw new IllegalArgumentException("Factorial on negative number");
        Number res = ONE();
        for(; x.toDouble() > 0; x = x.subtract(ONE()))
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
            return lowF.derive(SIGMA, "sum($x,"+high+","+f+")", PRE, l -> sum(c, l, high, f));
        if(high instanceof Expression.Function highF)
            return highF.derive(SIGMA, "sum("+low+",$x,"+f+")", PRE, h -> sum(c, low, h, f));
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
        Number res = ZERO();
        Number i = low;
        for(double iD=low.toDouble(c), highD=high.toDouble(c); iD<=highD; iD++, i = i.add(ONE()))
            res = res.add(f.evaluate(c, i));
        return res;
    }



    public static Number product(SymbolLookup e, Number low, Number high, Number f) {
        if(low instanceof Expression.Function lowF)
            return lowF.derive(PI, "product($x,"+high+","+f+")", PRE, l -> product(e, l, high, f));
        if(high instanceof Expression.Function highF)
            return highF.derive(PI, "product("+low+",$x,"+f+")", PRE, h -> product(e, low, h, f));
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
        Number res = ONE();
        Number i = low;
        for(double iD=low.toDouble(c), highD=high.toDouble(c); iD<=highD; iD++, i = i.add(ONE()))
            res = res.multiply(f.evaluate(c, i));
        return res;
    }



    public static Polynom polynom(SymbolLookup lookup, Number expr) {
        return Polynom.parse(lookup, expr);
    }

    public static Number derivative(SymbolLookup lookup, Number polynom, Number degree, Number indeterminant) {
        return derivative(lookup, Polynom.parse(lookup, polynom), degree, indeterminant);
    }

    private static Number derivative(SymbolLookup lookup, Polynom polynom, Number degree, Number indeterminant) {
        if(degree instanceof Vector v)
            return v.derive(c -> derivative(lookup, polynom, c, indeterminant));
        if(indeterminant instanceof Vector v)
            return v.derive(c -> derivative(lookup, polynom, degree, c));
        if(degree instanceof Expression.Function f)
            return f.derive("derivative", "($x)/d("+indeterminant+")", PRE, c -> derivative(lookup, polynom, c, indeterminant));
        if(indeterminant instanceof Expression.Function f)
            return f.derive("derivative", "($x)/d("+indeterminant+")", PRE, c -> derivative(lookup, polynom, degree, c));
        int n;
        if(degree == SymbolLookup.UNSPECIFIED) n = 1;
        else {
            double dn = degree.toDouble(lookup);
            if(dn != (int) dn)
                throw new ArithmeticException("Non-integer derivative degree");
            n = (int) dn;
        }
        return polynom.derivative(0,n).simplify();
    }

    public static Number antiderivative(SymbolLookup lookup, Number polynom, Number degree, Number indeterminant) {
        return antiderivative(lookup, Polynom.parse(lookup, polynom), degree, indeterminant);
    }

    private static Number antiderivative(SymbolLookup lookup, Polynom polynom, Number degree, Number indeterminant) {
        if(degree instanceof Vector v)
            return v.derive(c -> antiderivative(lookup, polynom, c, indeterminant));
        if(indeterminant instanceof Vector v)
            return v.derive(c -> antiderivative(lookup, polynom, degree, c));
        if(degree instanceof Expression.Function f)
            return f.derive("antiderivative", "($x)d("+indeterminant+")", PRE, c -> antiderivative(lookup, polynom, c, indeterminant));
        if(indeterminant instanceof Expression.Function f)
            return f.derive("antiderivative", "($x)d("+indeterminant+")", PRE, c -> antiderivative(lookup, polynom, degree, c));
        int n;
        if(degree == SymbolLookup.UNSPECIFIED) n = 1;
        else {
            double dn = degree.toDouble(lookup);
            if(dn != (int) dn)
                throw new ArithmeticException("Non-integer antiderivative degree");
            n = (int) dn;
        }
        return polynom.antiderivative(0,n).simplify();
    }

    public static Number integrate(SymbolLookup lookup, Number polynom, Number a, Number b, Number indeterminant) {
        return integrate(lookup, Polynom.parse(lookup, polynom), a, b, indeterminant);
    }

    private static Number integrate(SymbolLookup lookup, Polynom polynom, Number a, Number b, Number indeterminant) {
        if(a instanceof Vector v)
            return v.derive(c -> integrate(lookup, polynom, c, b, indeterminant));
        if(b instanceof Vector v)
            return v.derive(c -> integrate(lookup, polynom, a, c, indeterminant));
        if(indeterminant instanceof Vector v)
            return v.derive(c -> integrate(lookup, polynom, a, b, c));
        if(a instanceof Expression.Function f)
            return f.derive("integral", "int($x)d"+polynom.indeterminants()[0], PRE, c -> integrate(lookup, polynom, c, b, indeterminant));
        if(b instanceof Expression.Function f)
            return f.derive("integral", "int($x)d"+polynom.indeterminants()[0], PRE, c -> integrate(lookup, polynom, a, c, indeterminant));
        if(indeterminant instanceof Expression.Function f)
            return f.derive("integral", "int($x)d("+f+")", PRE, c -> integrate(lookup, polynom, a, b, c));
        double dInd = indeterminant.toDouble(lookup);
        if(dInd != (int) dInd) throw new ArithmeticException("Non-integer indeterminant");
        return polynom.integrate((int) dInd, lookup, a, b);
    }
}
