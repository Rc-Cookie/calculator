package com.github.rccookie.math.expr;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BinaryOperator;

import com.github.rccookie.math.BigDecimalMath;
import com.github.rccookie.math.Complex;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.math.SimpleNumber;
import com.github.rccookie.math.Vector;
import com.github.rccookie.math.rendering.RenderableExpression;
import com.github.rccookie.math.solve.LinearEquationSystem;
import com.github.rccookie.math.solve.Polynom;

import org.jetbrains.annotations.Contract;

import static com.github.rccookie.math.Number.*;
import static com.github.rccookie.math.rendering.RenderableExpression.*;

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
    public static final Expression.Function BINOMIAL_COEFF = new HardcodedFunction("bin", Functions::binCoeff);
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
    public static final Expression.Function RE = new HardcodedFunction("re", Functions::re);
    public static final Expression.Function IM = new HardcodedFunction("im", Functions::im);
    public static final Expression.Function COMPLEX_CONJUGATE = new HardcodedFunction("conj", Functions::complexConjugate);
    public static final Expression.Function VECTOR = new HardcodedFunction("vec", (l,p) -> vector(l, p[0], p[1]), "size", "comp");
    public static final Expression.Function MATRIX = new HardcodedFunction("mat", (l,p) -> matrix(l, p[0], p[1], p[2]), "m", "n", "comp");
    public static final Expression.Function IDENTITY_MATRIX = new HardcodedFunction("idm", Functions::identityMatrix);
    public static final Expression.Function GET = new HardcodedFunction("get", Functions::get);
    public static final Expression.Function SIZE = new HardcodedFunction("size", Functions::size);
    public static final Expression.Function NORMALIZE = new HardcodedFunction("norm", Functions::normalize);
    public static final Expression.Function DOT = new HardcodedFunction("dot", Functions::dot);
    public static final Expression.Function CROSS = new HardcodedFunction("cross", Functions::cross);
    public static final Expression.Function MATRIX_MULTIPLY = new HardcodedFunction("mmult", Functions::matrixMultiply);
    public static final Expression.Function TRANSPOSITION = new HardcodedFunction("transp", Functions::transposition);
    public static final Expression.Function RAD_TO_DEG = new HardcodedFunction("deg", Functions::radToDeg);
    public static final Expression.Function DEG_TO_RAD = new HardcodedFunction("rad", Functions::degToRad);
    public static final Expression.Function SUM = new HardcodedFunction(SIGMA, (l,p) -> sum(l,p[0], p[1], p[2]), "low", "high", "f");
    public static final Expression.Function PRODUCT = new HardcodedFunction(PI, (l,p) -> product(l,p[0], p[1], p[2]), "low", "high", "f");

    public static final Expression.Function POLYNOM = new HardcodedFunction("poly", (l,p) -> polynom(l,p[0]), "p");
    public static final Expression.Function DERIVATIVE = new HardcodedFunction("der", (l,p) -> derivative(l,p[0],p[1],p[2]), "p", "deg", "ind");
    public static final Expression.Function ANTIDERIVATIVE = new HardcodedFunction("antiDer", (l,p) -> antiderivative(l,p[0],p[1],p[2]), "p", "deg", "ind");
    public static final Expression.Function INTEGRATE = new HardcodedFunction("int", (l,p) -> integrate(l,p[0],p[1],p[2],p[3]), "p", "a", "b", "ind");

    public static final Expression.Function REDUCE = new HardcodedFunction("reduce", Functions::gaussReduction);
    public static final Expression.Function GAUSS = new HardcodedFunction("gauss", Functions::gauss);
    public static final Expression.Function RANK = new HardcodedFunction("rank", Functions::rank);

    private static final Number LN_2 = ln(new Rational(2));

    private static final int PRE = Precedence.FUNCTION_CALL;

    private Functions() { }

    public static Number min(Number a, Number b) {
        a = value(a);
        b = value(b);
        if(a instanceof Expression.Function f)
            return f.derive("min", "min($1,$2)", (a1,b1) -> call("min", a1, b1), null, b, PRE, Functions::min);
        if(b instanceof Expression.Function f)
            return f.derive("min", "min($2,$1)", (a1,b1) -> call("min", b1, a1), null, a, PRE, Functions::min);
        if(b == SymbolLookup.UNSPECIFIED)
            return min(a);
        Number relation = a.lessThan(b);
        return a.multiply(relation).add(b.multiply(ONE().subtract(relation)));
    }

    public static Number min(Number x) {
        x = value(x);
        if(!(x instanceof Vector v)) return x;
        Number min = v.get(0);
        for(int i=1; i<v.size(); i++)
            min = min(min,v.get(i));
        return min;
    }

    public static Number max(Number a, Number b) {
        a = value(a);
        b = value(b);
        if(a instanceof Expression.Function f)
            return f.derive("max", "max($1,$2)", (a1,b1) -> call("max", a1, b1), null, b, PRE, Functions::max);
        if(b instanceof Expression.Function f)
            return f.derive("max", "max($2,$1)", (a1,b1) -> call("max", b1, a1), null, a, PRE, Functions::max);
        if(b == SymbolLookup.UNSPECIFIED)
            return max(a);
        Number relation = a.greaterThan(b);
        return a.multiply(relation).add(b.multiply(ONE().subtract(relation)));
    }

    public static Number max(Number x) {
        x = value(x);
        if(!(x instanceof Vector v)) return x;
        Number max = v.get(0);
        for(int i=1; i<v.size(); i++)
            max = max(max,v.get(i));
        return max;
    }



    public static Number floor(Number x) {
        x = value(x);
        return switch(x) {
            case Rational r -> new Rational(r.n.divide(r.d));
            case SimpleNumber n -> new Rational(n.toBigDecimal().setScale(0, RoundingMode.FLOOR));
            case Complex c -> new Complex((SimpleNumber) floor(c.re), (SimpleNumber) floor(c.im));
            case Vector v -> v.derive(Functions::floor);
            case Expression.Function f -> f.derive("floor", "floor($x)", RenderableExpression::floor, PRE, Functions::floor);
            default -> throw new UnsupportedMathOperationException("floor", x);
        };
    }

    public static Number ceil(Number x) {
        x = value(x);
        return switch(x) {
            case Rational r -> new Rational(r.n.add(r.d).subtract(BigInteger.ONE).divide(r.d));
            case SimpleNumber n -> new Rational(n.toBigDecimal().setScale(0, RoundingMode.CEILING));
            case Complex c -> new Complex((SimpleNumber) ceil(c.re), (SimpleNumber) ceil(c.im));
            case Vector v -> v.derive(Functions::ceil);
            case Expression.Function f -> f.derive("ceil", "ceil($x)", RenderableExpression::ceil, PRE, Functions::ceil);
            default -> throw new UnsupportedMathOperationException("ceil", x);
        };
    }

    public static Number round(Number x) {
        x = value(x);
        return switch(x) {
            case SimpleNumber n -> new Rational(n.toBigDecimal().setScale(0, RoundingMode.HALF_UP));
            case Complex c -> new Complex((SimpleNumber) round(c.re), (SimpleNumber) round(c.im));
            case Vector v -> v.derive(Functions::round);
            case Expression.Function f -> f.derive("round", "round($x)", x1 -> call("round", x1), PRE, Functions::round);
            default -> throw new UnsupportedMathOperationException("round", x);
        };
    }



    public static Number sin(Number x) {
        x = value(x);
        return switch(x) {
            case SimpleNumber n -> sin(n);
            case Complex c -> sin(c);
            case Vector v -> v.derive(Functions::sin);
            case Expression.Function f -> f.derive("sin", "sin($x)", x1 -> call("sin", x1), PRE, Functions::sin);
            default -> throw new UnsupportedMathOperationException("sin", x);
        };
    }

    public static SimpleNumber sin(SimpleNumber x) {
        if(x.equals(Rational.ZERO)) return x;
        return new Rational(BigDecimalMath.sin(x.toBigDecimal()), false);
    }

    public static Number sin(Complex x) {
        if(x.isReal())
            return sin(x.re);
        return im(exp(x));
    }


    public static Number cos(Number x) {
        x = value(x);
        return switch(x) {
            case SimpleNumber n -> cos(n);
            case Complex c -> cos(c);
            case Vector v -> v.derive(Functions::cos);
            case Expression.Function f -> f.derive("cos", "cos($x)", x1 -> call("cos", x1), PRE, Functions::cos);
            default -> throw new UnsupportedMathOperationException("cos", x);
        };
    }

    public static SimpleNumber cos(SimpleNumber x) {
        if(x.equals(Rational.ZERO))
            return Rational.ONE(x.precise());
        return new Rational(BigDecimalMath.cos(x.toBigDecimal()), false);
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
        x = value(x);
        return switch(x) {
            case SimpleNumber n -> asin(n);
            case Complex c -> asin(c);
            case Vector v -> v.derive(Functions::asin);
            case Expression.Function f -> f.derive("asin", "asin($x)", x1 -> call("asin", x1), PRE, Functions::asin);
            default -> throw new UnsupportedMathOperationException("asin", x);
        };
    }

    public static Number asin(SimpleNumber x) {
        if(x.equals(MINUS_ONE())) return PI().divide(-2);
        if(x.equals(ZERO())) return x;
        if(x.equals(ONE())) return PI().divide(2);
        return new Rational(BigDecimalMath.asin(x.toBigDecimal()), false);
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
        x = value(x);
        return switch(x) {
            case SimpleNumber r -> acos(r);
            case Complex c -> acos(c);
            case Vector v -> v.derive(Functions::acos);
            case Expression.Function f -> f.derive("acos", "acos($x)", x1 -> call("acos", x1), PRE, Functions::acos);
            default -> throw new UnsupportedMathOperationException("acos", x);
        };
    }

    public static Number acos(SimpleNumber x) {
        if(x.equals(ONE())) return Rational.ZERO(x.precise());
        if(x.equals(ZERO())) return PI().divide(2);
        if(x.equals(MINUS_ONE())) return PI();
        return new Rational(BigDecimalMath.acos(x.toBigDecimal()), false);
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
        x = value(x);
        return switch(x) {
            case SimpleNumber n -> atan(n);
            case Complex c -> atan(c);
            case Vector v -> v.derive(Functions::atan);
            case Expression.Function f -> f.derive("atan", "atan($x)", x1 -> call("atan", x1), PRE, Functions::atan);
            default -> throw new UnsupportedMathOperationException("atan", x);
        };
    }

    public static SimpleNumber atan(SimpleNumber x) {
        return new Rational(BigDecimalMath.atan(x.toBigDecimal()), false);
    }

    public static Number atan(Complex x) {
        // 1/(2i) * ln((i-x)/(i+x))
        return I().multiply(2).invert().multiply(ln(I().subtract(x).divide(I().add(x))));
    }


    public static Number atan2(Number y, Number x) {
        //noinspection SuspiciousNameCombination
        Number _y = value(y);
        Number _x = value(x);
        if(_y instanceof Expression.Function f)
            return f.derive("atan2", "atan2($1,$2)", (y1,x1) -> call("atan2", y1, x1), null, _x, PRE, Functions::atan2);
        if(_x instanceof Expression.Function f)
            return f.derive("atan2", "atan2($2,$1)", (x1,y1) -> call("atan2", y1, x1), null, _y, PRE, Functions::atan2);
        if(_y instanceof Vector vy) {
            if(_x instanceof Vector vx)
                return vy.derive(vx, Functions::atan2);
            return vy.derive(yc -> atan2(yc, _x));
        }
        if(_x instanceof Vector vx)
            return vx.derive(xc -> atan2(_y, xc));
        if(!(_x instanceof SimpleNumber && _y instanceof SimpleNumber))
            throw new UnsupportedMathOperationException("atan", _y, _x);

        if(_x.equals(ZERO())) {
            if(_y.greaterThan(ZERO()).equals(ONE()))
                return PI().divide(2);
            if(!_y.equals(ZERO()))
                return PI().divide(-2);
            throw new ArithmeticException("atan2 of 0,0");
        }
        Number atan = atan(_y.divide(_x));
        if(_x.greaterThan(ZERO()).equals(ONE()))
            return atan;
        if(_y.greaterThanOrEqual(ZERO()).equals(ONE()))
            return atan.add(PI());
        return atan.subtract(PI());
    }



    public static Number exp(Number x) {
        x = value(x);
        return switch(x) {
            case SimpleNumber n -> exp(n);
            case Complex c -> exp(c);
            case Vector v -> v.derive(Functions::exp);
            case Expression.Function f -> f.derive("exp", "exp($x)", RenderableExpression::exp, PRE, Functions::exp);
            default -> throw new UnsupportedMathOperationException("exp", x);
        };
    }

    public static SimpleNumber exp(SimpleNumber x) {
        if(x.equals(Rational.ZERO))
            return Rational.ONE(x.precise());
        return new Rational(BigDecimalMath.exp(x.toBigDecimal()), false);
    }

    public static Number exp(Complex x) {
        if(x.isReal())
            return exp(x.re);
        return Complex.fromPolar(x.im, exp(x.re));
    }


    public static Number ln(Number x) {
        x = value(x);
        return switch(x) {
            case SimpleNumber r -> ln(r);
            case Complex c -> ln(c);
            case Vector v -> v.derive(Functions::ln);
            case Expression.Function f -> f.derive("ln", "ln($x)", x1 -> call("ln", x1), PRE, Functions::ln);
            default -> throw new UnsupportedMathOperationException("ln", x);
        };
    }

    public static Number ln(SimpleNumber x) {
        if(x.equals(ZERO()))
            throw new ArithmeticException("ln 0 is undefined");
        if(x.equals(ONE()))
            return Rational.ZERO(x.precise());
        if(x.lessThan(ZERO()).equals(ONE()))
            return ln(new Complex(x));
        return new Rational(BigDecimalMath.log(x.toBigDecimal()), false);
    }

    public static Number ln(Complex x) {
        //noinspection EqualsBetweenInconvertibleTypes
        if(x.equals(ZERO()))
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
        x = value(x);
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
            case Expression.Function f -> f.derive("argument", "arg($x)", x1 -> call("arg", x1), PRE, Functions::argument);
            default -> throw new UnsupportedMathOperationException("argument", x);
        };
    }

    public static Number re(Number x) {
        x = value(x);
        return switch(x) {
            case SimpleNumber n -> n;
            case Complex c -> c.re;
            case Vector v -> v.derive(Functions::re);
            case Expression.Function f -> f.derive("re", "re($x)", x1 -> call("re", x1), PRE, Functions::re);
            default -> throw new UnsupportedMathOperationException("re", x);
        };
    }

    public static Number im(Number x) {
        x = value(x);
        return switch(x) {
            case SimpleNumber ignored -> ZERO();
            case Complex c -> c.im;
            case Vector v -> v.derive(Functions::im);
            case Expression.Function f -> f.derive("im", "im($x)", x1 -> call("im", x1), PRE, Functions::im);
            default -> throw new UnsupportedMathOperationException("im", x);
        };
    }

    public static Number complexConjugate(Number x) {
        x = value(x);
        return switch(x) {
            case SimpleNumber n -> n;
            case Complex c -> c.conjugate();
            case Vector v -> v.derive(Functions::complexConjugate);
            case Expression.Function f -> f.derive("conj", "conj($x)", x1 -> call("conj", x1), PRE, Functions::complexConjugate);
            default -> throw new UnsupportedMathOperationException("conj", x);
        };
    }


    public static Number vector(SymbolLookup l, Number size, Number componentF) {
        size = value(size);
        Number _componentF = value(componentF);
        if(size instanceof Expression.Function f)
            return f.derive("vec", "vec($1,$2)", (s,cf) -> call("vec", cf, s), null, componentF, PRE, (s,cf) -> vector(l,s,cf));
        int s = (int) size.toDouble(l);
        if(s < 1) throw new MathEvaluationException("Non-positive vector size");
        Number[] c = new Number[s];
        if(_componentF instanceof Expression.Function f)
            for(int i=0; i<s; i++)
                c[i] = f.evaluate(l, new Rational(i+1));
        else Arrays.fill(c, _componentF);
        return new Vector(c);
    }


    public static Number matrix(SymbolLookup l, Number m, Number n, Number componentF) {
        Number _m = value(m);
        Number _n = value(n);
        Number _componentF = value(componentF);
        if(_m instanceof Expression.Function f)
            return f.derive("mat", "mat($x,"+_n+","+_componentF+")", mm -> call("matrix", mm, n.toRenderable(), componentF.toRenderable()), PRE, mm -> matrix(l,mm, _n, _componentF));
        if(_n instanceof Expression.Function f)
            return f.derive("mat", "mat("+_m+",$x,"+_componentF+")", nn -> call("matrix", m.toRenderable(), nn, componentF.toRenderable()), PRE, nn -> matrix(l,_m, nn, _componentF));
        int mm = (int) _m.toDouble(l), nn = (int) _n.toDouble(l);
        if(mm < 1) throw new MathEvaluationException("Non-positive matrix row count");
        if(nn < 1) throw new MathEvaluationException("Non-positive matrix columns count");
        Number[][] c = new Number[mm][nn];
        if(_componentF instanceof Expression.Function f)
            for(int i=0; i<mm; i++)
                for(int j=0; j<nn; j++)
                    c[i][j] = f.evaluate(l, Expression.Numbers.of(new Rational(i+1), new Rational(j+1)));
        else for(int i=0; i<mm; i++)
            Arrays.fill(c[i], _componentF);

        Vector[] rows = new Vector[mm];
        for(int i=0; i<rows.length; i++)
            rows[i] = new Vector(c[i]);
        return new Vector(rows);
    }

    public static Number identityMatrix(Number size) {
        size = value(size);
        if(size instanceof Expression.Function f)
            return f.derive("idm", "idm($x)", s -> call("idm", s), PRE, Functions::identityMatrix);

        int s = (int) size.toDouble();
        if(s < 1) throw new MathEvaluationException("Non-positive matrix size");

        Number[][] rows = new Number[s][s];
        for(int i=0; i<s; i++) for(int j=0; j<s; j++)
            rows[i][j] = i == j ? Number.ONE() : Number.ZERO();
        return Vector.matrixFromRows(Arrays.stream(rows).map(Vector::new).toArray(Vector[]::new));
    }


    public static Number dot(Number a, Number b) {
        a = value(a);
        b = value(b);
        if(a instanceof Expression.Function f)
            return f.derive("dot", "dot($1,$2)", (aa,bb) -> call("dot", aa, bb), null, b, PRE, Functions::dot);
        if(b instanceof Expression.Function f)
            return f.derive("dot", "dot($2,$1)", (bb,aa) -> call("dot", aa, bb), null, a, PRE, Functions::dot);
        return dot(Vector.asVector(a), Vector.asVector(b));
    }

    public static Number dot(Vector a, Vector b) {
        return a.dot(b);
    }


    public static Number cross(Number a, Number b) {
        a = value(a);
        b = value(b);
        if(a instanceof Expression.Function f)
            return f.derive("cross", "cross($1,$2)", (aa,bb) -> call("cross", aa, bb), null, b, PRE, Functions::cross);
        if(b instanceof Expression.Function f)
            return f.derive("cross", "cross($2,$1)", (bb,aa) -> call("cross", aa, bb), null, a, PRE, Functions::cross);
        if(!(a instanceof Vector av) || !(b instanceof Vector bv))
            throw new MathEvaluationException("Cross product requires two vectors");
        return cross(av, bv);
    }

    public static Vector cross(Vector a, Vector b) {
        return a.cross(b);
    }



    public static Number matrixMultiply(Number a, Number b) {
        a = value(a);
        b = value(b);
        if(a instanceof Expression.Function f)
            return f.derive("mmult", "mmult($1,$2)", (aa,bb) -> call("mmult", aa, bb), null, b, PRE, Functions::matrixMultiply);
        if(b instanceof Expression.Function f)
            return f.derive("mmult", "mmult($2,$1)", (bb,aa) -> call("mmult", aa, bb), null, a, PRE, Functions::matrixMultiply);
        return Vector.asVector(a).matrixMultiply(Vector.asVector(b));
    }

    public static Number transposition(Number x) {
        x = value(x);
        if(x instanceof Expression.Function f)
            return f.derive("transp", "transp($x)", xx -> call("transp", xx), PRE, Functions::transposition);
        Vector m = Vector.asVector(x);
        if(!m.isMatrix()) throw new MathEvaluationException("Cannot calculate transposition of non-matrix");
        Vector[] rows = new Vector[m.size()];
        for(int i=0; i<rows.length; i++)
            rows[i] = Vector.asVector(m.get(i));
        return Vector.matrixFromColumns(rows);
    }



    public static Number gauss(Number m, Number b) {
        if(b == SymbolLookup.UNSPECIFIED)
            return homogenousGauss(m);
        m = value(m);
        b = value(b);
        if(m instanceof Expression.Function f)
            return f.derive("gauss", "gauss($1,$2)", (aa,bb) -> call("gauss", aa, bb), null, b, PRE, Functions::gauss);
        if(b instanceof Expression.Function f)
            return f.derive("gauss", "gauss($2,$1)", (bb,aa) -> call("gauss", aa, bb), null, m, PRE, Functions::gauss);

        Vector mv = Vector.asVector(m), bv = Vector.asVector(b);
        if(!mv.isMatrix() || !bv.isMatrix())
            throw new MathEvaluationException("Matrices expected for gauss elimination");
        if(mv.rowCount() != bv.rowCount())
            throw new MathEvaluationException("Matrices must have same height for gauss elimination");

        int unknowns = mv.columnCount(), bCount = bv.columnCount();
        Number[][] aug = new Number[mv.rowCount()][unknowns + bCount];
        for(int i=0; i<aug.length; i++) {
            for(int j=0; j<unknowns; j++)
                aug[i][j] = mv.get(i,j);
            for(int j=0; j<bCount; j++)
                aug[i][j+unknowns] = bv.get(i,j);
        }

        Number[][] solutions = new LinearEquationSystem(unknowns, aug).solve().rows();
        for(int i=0; i<solutions.length; i++) for(int j=0; j<solutions[0].length; j++)
            if(solutions[i][j] == null) solutions[i][j] = Expression.WILDCARD();

        return Vector.matrixFromRows(Arrays.stream(solutions).map(Vector::new).toArray(Vector[]::new));
    }

    private static Number homogenousGauss(Number m) {
        m = value(m);
        if(m instanceof Expression.Function f)
            return f.derive("gauss", "gauss($x)", mm -> call("gauss", mm), PRE, Functions::homogenousGauss);

        Vector mv = Vector.asVector(m);
        if(!mv.isMatrix())
            throw new MathEvaluationException("Matrix expected for gauss elimination");

        int height = mv.rowCount(), width = mv.columnCount();
        Number[][] arr = new Number[height][width];
        for(int i=0; i<height; i++) for(int j=0; j<width; j++)
            arr[i][j] = mv.get(i,j);

        Number[] solution = new LinearEquationSystem(width, arr).solve().getHomogenousResult();
        for(int i=0; i<solution.length; i++)
            if(solution[i] == null)
                solution[i] = new RuntimeFunction(Expression.Symbol.of("x"+i), "x"+i);
        return new Vector(solution);
    }

    public static Number gaussReduction(Number m, Number b) {
        if(b == SymbolLookup.UNSPECIFIED)
            return homogenousGaussReduction(m);
        m = value(m);
        b = value(b);
        if(m instanceof Expression.Function f)
            return f.derive("reduce", "reduce($1,$2)", (mm,bb) -> call("reduce", mm, bb), null, b, PRE, Functions::gaussReduction);
        if(b instanceof Expression.Function f)
            return f.derive("reduce", "reduce($2,$1)", (bb,mm) -> call("reduce", mm, bb), null, m, PRE, Functions::gaussReduction);

        Vector mv = Vector.asVector(m), bv = Vector.asVector(b);
        if(!mv.isMatrix() || !bv.isMatrix())
            throw new MathEvaluationException("Matrices expected for gauss reduction");
        if(mv.rowCount() != bv.rowCount())
            throw new MathEvaluationException("Matrices must have same height for gauss reduction");

        int unknowns = mv.columnCount(), bCount = bv.columnCount();
        Number[][] aug = new Number[mv.rowCount()][unknowns + bCount];
        for(int i=0; i<aug.length; i++) {
            for(int j=0; j<unknowns; j++)
                aug[i][j] = mv.get(i,j);
            for(int j=0; j<bCount; j++)
                aug[i][j+unknowns] = bv.get(i,j);
        }

        LinearEquationSystem solution = new LinearEquationSystem(unknowns, aug).toReducedEchelonForm();
        Vector[] rows = new Vector[aug.length];
        for(int i=0; i<aug.length; i++)
            rows[i] = new Vector(solution.getRow(i));
        return new Vector(rows);
    }

    private static Number homogenousGaussReduction(Number m) {
        m = value(m);
        if(m instanceof Expression.Function f)
            return f.derive("reduce", "reduce($x)", mm -> call("reduce", mm), PRE, Functions::homogenousGaussReduction);

        LinearEquationSystem solution = toLinearEquations(m, "gauss reduction").toReducedEchelonForm();
        Vector[] rows = new Vector[solution.rowCount()];
        for(int i=0; i<rows.length; i++)
            rows[i] = new Vector(solution.getRow(i));
        return new Vector(rows);
    }


    public static Number rank(Number x) {
        x = value(x);
        if(x instanceof Expression.Function f)
            return f.derive("rank", "rank($x)", xx -> call("rank", xx), PRE, Functions::rank);

        LinearEquationSystem system = toLinearEquations(x, "rank");
        Number[] solution = system.solve().getHomogenousResult();
        return new Rational(Arrays.stream(solution).filter(Objects::nonNull).count());
    }


    private static LinearEquationSystem toLinearEquations(Number m, String task) {
        Vector mv = Vector.asVector(m);
        if(!mv.isMatrix())
            throw new MathEvaluationException("Matrix expected for "+task);

        int height = mv.rowCount(), width = mv.columnCount();
        Number[][] arr = new Number[height][width];
        for(int i=0; i<height; i++) for(int j=0; j<width; j++)
            arr[i][j] = mv.get(i,j);

        return new LinearEquationSystem(width, arr);
    }


//    public static Number get(Number v, Number i, Number j) {
//        if(j == SymbolLookup.UNSPECIFIED)
//            return get(v, i);
//        if(v instanceof Expression.Function f)
//            return f.derive("get", "get($x,"+i+","+j+")", PRE, vv -> get(vv,i,j));
//        if(i instanceof Expression.Function f)
//            return f.derive("get", "get("+v+",$x,"+j+")", PRE, ii -> get(v,ii,j));
//        if(j instanceof Expression.Function f)
//            return f.derive("get", "get("+v+","+i+",$x)", PRE, jj -> get(v,i,jj));
//        if(i instanceof Expression.Function f)
//            return f.derive("get", "get($2,$1)", v, PRE, Functions::get);
//        return Vector.asVector(v).get(i.subtract(1));
//    }

    public static Number get(Number v, Number i) {
        v = value(v);
        i = value(i);
        if(v instanceof Expression.Constant c) v = c.value();
        if(i instanceof Expression.Constant c) i = c.value();
        if(v instanceof Expression.Function f)
            return f.derive("get", "get($1,$2)", (vv,ii) -> call("get", vv, ii), null, i, PRE, Functions::get);
        if(i instanceof Expression.Function f)
            return f.derive("get", "get($2,$1)", (ii,vv) -> call("get", vv, ii), v, PRE, Functions::get);
        if(i instanceof Expression.Numbers n) {
            if(n.size() == 0)
                throw new MathEvaluationException("Indices expected, got empty list");
            Number x = v;
            for(Number index : n)
                x = get(x,index);
            return x;
        }
        return Vector.asVector(v).get(i.subtract(1));
    }

    public static Number size(Number x) {
        x = value(x);
        if(x instanceof Expression.Function f)
            return f.derive("size", "size($x)", xx -> call("size", xx), PRE, Functions::size);
        return x instanceof Vector v ? new Rational(v.size()) : ONE();
    }

    public static Number normalize(Number x) {
        x = value(x);
        return switch(x) {
            case Vector v -> v.normalize();
            case Complex c -> c.normalize();
            case Expression.Function f -> f.derive("norm", "norm($x)", xx -> call("norm", xx), PRE, Functions::normalize);
            case SimpleNumber n -> n.greaterThan(ZERO()).subtract(n.lessThan(ZERO()));
            default -> throw new UnsupportedMathOperationException(""+x);
        };
    }



    public static Number radToDeg(Number x) {
        x = value(x);
        return x.multiply(RAD_TO_DEG());
    }

    public static Number degToRad(Number x) {
        x = value(x);
        return x.multiply(DEG_TO_RAD());
    }

    public static Number fromPercent(Number x) {
        x = value(x);
        return x.divide(100);
    }



    public static Number square(Number x) {
        x = value(x);
        return x.raise(TWO());
    }

    public static Number cube(Number x) {
        x = value(x);
        return x.raise(new Rational(3));
    }


    public static Number hypot(Number a, Number b) {
        return square(a).add(square(b)).sqrt();
    }



    public static Number factorial(Number x) {
        x = value(x);
        if(x instanceof Vector v)
            return v.derive(Functions::factorial);
        double xd = x.toDouble();
        if(xd != (long) xd)
            throw new ArithmeticException("Factorial on non-integer");
        if(xd < 0)
            throw new ArithmeticException("Factorial on negative number");
        Number res = ONE();
        for(; x.toDouble() > 0; x = x.subtract(ONE()))
            res = res.multiply(x);
        return res;
    }

    public static Number binCoeff(Number n, Number k) {
        n = value(n);
        k = value(k);
        return factorial(n).divide(factorial(k).multiply(factorial(n.subtract(k))));
    }



    public static Number sum(SymbolLookup c, Number low, Number high, Number f) {
        Number _low = value(low);
        Number _high = value(high);
        Number _f = value(f);
        if(_low instanceof Expression.Function lowF)
            return lowF.derive(SIGMA, "sum($x,"+_high+","+_f+")", l -> RenderableExpression.sum(l, _high.toRenderable(), _f.toRenderable()), PRE, l -> sum(c, l, _high, _f));
        if(_high instanceof Expression.Function highF)
            return highF.derive(SIGMA, "sum("+_low+",$x,"+_f+")", h -> RenderableExpression.sum(_low.toRenderable(), h, _f.toRenderable()), PRE, h -> sum(c, _low, h, _f));
        if(_low instanceof Vector lowV) {
            if(_high instanceof Vector highV)
                return lowV.derive(highV, (l, h) -> sum(c,l,h,_f));
            return lowV.derive(l -> sum(c, l, _high, _f));
        }
        if(_high instanceof Vector highV)
            return highV.derive(h -> sum(c, _low, h, _f));
        return sum(c, _low, _high, _f instanceof Expression.Function ff ? ff : new HardcodedFunction("_f", "_", l -> _f));
    }

    private static Number sum(SymbolLookup c, Number low, Number high, Expression.Function f) {
        Number res = ZERO();
        Number i = low;
        for(double iD=low.toDouble(c), highD=high.toDouble(c); iD<=highD; iD++, i = i.add(ONE()))
            res = res.add(f.evaluate(c, i));
        return res;
    }



    public static Number product(SymbolLookup e, Number low, Number high, Number f) {
        Number _low = value(low);
        Number _high = value(high);
        Number _f = value(f);
        if(_low instanceof Expression.Function lowF)
            return lowF.derive(PI, "product($x,"+_high+","+f+")", l -> prod(l, _high.toRenderable(), _f.toRenderable()), PRE, l -> product(e, l, _high, _f));
        if(_high instanceof Expression.Function highF)
            return highF.derive(PI, "product("+_low+",$x,"+_f+")", h -> prod(_low.toRenderable(), h, _f.toRenderable()), PRE, h -> product(e, _low, h, _f));
        if(_low instanceof Vector lowV) {
            if(_high instanceof Vector highV)
                return lowV.derive(highV, (l, h) -> product(e,l,h,_f));
            return lowV.derive(l -> product(e, l, _high, _f));
        }
        if(_high instanceof Vector highV)
            return highV.derive(h -> product(e, _low, h, _f));
        return product(e, _low, _high, _f instanceof Expression.Function ff ? ff : new HardcodedFunction("_f", "_", l -> _f));
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
        Number deg = value(degree);
        Number ind = value(indeterminant);
        if(degree instanceof Vector v)
            return v.derive(c -> derivative(lookup, polynom, c, ind));
        if(ind instanceof Vector v)
            return v.derive(c -> derivative(lookup, polynom, deg, c));
        if(deg instanceof Expression.Function f)
            return f.derive("derivative", "($x)/d("+ind+")", c -> null, PRE, c -> derivative(lookup, polynom, c, ind));
        if(ind instanceof Expression.Function f)
            return f.derive("derivative", "($x)/d("+ind+")", c -> null, PRE, c -> derivative(lookup, polynom, deg, c));
        int n;
        if(deg == SymbolLookup.UNSPECIFIED) n = 1;
        else {
            double dn = deg.toDouble(lookup);
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
            return f.derive("antiderivative", "($x)d("+indeterminant+")", c -> null, PRE, c -> antiderivative(lookup, polynom, c, indeterminant));
        if(indeterminant instanceof Expression.Function f)
            return f.derive("antiderivative", "($x)d("+indeterminant+")", c -> null, PRE, c -> antiderivative(lookup, polynom, degree, c));
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
            return f.derive("integral", "int($x)d"+polynom.indeterminants()[0], c -> integral(c, b.toRenderable(), polynom.toRenderable(), indeterminant.toRenderable()), PRE, c -> integrate(lookup, polynom, c, b, indeterminant));
        if(b instanceof Expression.Function f)
            return f.derive("integral", "int($x)d"+polynom.indeterminants()[0], c -> integral(a.toRenderable(), c, polynom.toRenderable(), indeterminant.toRenderable()), PRE, c -> integrate(lookup, polynom, a, c, indeterminant));
        if(indeterminant instanceof Expression.Function f)
            return f.derive("integral", "int($x)d("+f+")", c -> integral(a.toRenderable(), b.toRenderable(), polynom.toRenderable(), c), PRE, c -> integrate(lookup, polynom, a, b, c));
        double dInd = indeterminant.toDouble(lookup);
        if(dInd != (int) dInd) throw new ArithmeticException("Non-integer indeterminant");
        return polynom.integrate((int) dInd, lookup, a, b);
    }



    @Contract(pure = true)
    private static Number value(Number x) {
        while(x instanceof Expression.Constant c)
            x = c.value();
        return x;
    }
}
