//package com.github.rccookie.math.expr;
//
//import java.util.function.BiFunction;
//import java.util.function.BinaryOperator;
//import java.util.function.UnaryOperator;
//
//import com.github.rccookie.math.Number;
//
//import org.jetbrains.annotations.NotNull;
//
//public record FunctionImpl1(Number expr, String... paramNames) implements Expression {
//
//    public static final FunctionImpl1 ABS = new FunctionImpl1("abs", Number::abs);
//    public static final FunctionImpl1 SQRT = new FunctionImpl1("sqrt", Number::sqrt);
//    public static final FunctionImpl1 HYPOT = new FunctionImpl1("hypot", Functions::hypot);
//    public static final FunctionImpl1 EXP = new FunctionImpl1("exp", Functions::exp);
//    public static final FunctionImpl1 LN = new FunctionImpl1("ln", Functions::ln);
//    public static final FunctionImpl1 LD = new FunctionImpl1("ld", Functions::ld);
//    public static final FunctionImpl1 LOG = new FunctionImpl1("log", Functions::log);
//    public static final FunctionImpl1 FACTORIAL = new FunctionImpl1("factorial", Functions::factorial);
//    public static final FunctionImpl1 MIN = new FunctionImpl1("min", (BinaryOperator<Number>) Functions::min);
//    public static final FunctionImpl1 MAX = new FunctionImpl1("max", (BinaryOperator<Number>) Functions::max);
//    public static final FunctionImpl1 FLOOR = new FunctionImpl1("floor", Functions::floor);
//    public static final FunctionImpl1 CEIL = new FunctionImpl1("ceil", Functions::ceil);
//    public static final FunctionImpl1 ROUND = new FunctionImpl1("round", Functions::round);
//    public static final FunctionImpl1 SIN = new FunctionImpl1("sin", Functions::sin);
//    public static final FunctionImpl1 COS = new FunctionImpl1("cos", Functions::cos);
//    public static final FunctionImpl1 TAN = new FunctionImpl1("tan", Functions::tan);
//    public static final FunctionImpl1 ASIN = new FunctionImpl1("asin", Functions::asin);
//    public static final FunctionImpl1 ACOS = new FunctionImpl1("acos", Functions::acos);
//    public static final FunctionImpl1 ATAN = new FunctionImpl1("atan", Functions::atan);
//    public static final FunctionImpl1 ATAN2 = new FunctionImpl1("atan2", Functions::atan2);
//    public static final FunctionImpl1 GET = new FunctionImpl1("get", Functions::get);
//    public static final FunctionImpl1 SIZE = new FunctionImpl1("size", Functions::size);
//    public static final FunctionImpl1 CROSS = new FunctionImpl1("cross", (Number a, Number b) -> Functions.cross(a,b));
//    public static final FunctionImpl1 RAD_TO_DEG = new FunctionImpl1("deg", Functions::radToDeg);
//    public static final FunctionImpl1 DEG_TO_RAD = new FunctionImpl1("rad", Functions::degToRad);
//    public static final FunctionImpl1 SUM = new FunctionImpl1(Expression.named("\u03A3", Functions::sum), "$low", "$high", "$f");
//    public static final FunctionImpl1 PRODUCT = new FunctionImpl1(Expression.named("\u03A0", Functions::product), "$low", "$high", "$f");
//
//
//
//    public FunctionImpl1(String name, BinaryOperator<Number> function) {
//        this(Expression.named(name, c -> function.apply(
//                c.get("a"),
//                c.get("b")
//        )), "a", "b");
//    }
//
//    public FunctionImpl1(String name, BiFunction<SymbolLookup, Number, Number> function) {
//        this(Expression.named(name, c -> function.apply(c, c.get("x"))), "x");
//    }
//
//    public FunctionImpl1(String name, UnaryOperator<Number> function) {
//        this(Expression.named(name, c -> function.apply(c.get("x"))), "x");
//    }
//
//    public FunctionImpl1(String name, FunctionImpl1 inner, UnaryOperator<Number> outer) {
//        this(Expression.named(name, c -> outer.apply(Expression.evaluate(inner.expr, c))), inner.paramNames);
//    }
//
//    @Override
//    public String toString() {
//        return expr.toString();/*"Function[" +
//                "expr=" + expr +
//                ", paramNames=" + Arrays.toString(paramNames) +
//                ']';*/
//    }
//
//    @Override
//    public Number evaluate(SymbolLookup lookup) {
//        return this;
//    }
//
//    public Number evaluate(SymbolLookup lookup, Number params) {
//        if(!(params instanceof Numbers l))
//            return evaluateFunction(lookup, params);
//        if(l.size() <= paramNames.length)
//            return evaluateFunction(lookup, l.evaluateToArray(lookup));
//        if(paramNames.length == 1) {
//            Number[] results = new Number[l.size()];
//            for (int i = 0; i < results.length; i++)
//                results[i] = evaluateFunction(lookup, l.evaluate(lookup, i));
//            return new Numbers(results);
//        }
//        throw new IllegalArgumentException("Too many arguments (" + l.size() + ") applied to function, expected " + paramNames.length);
//    }
//
//    private Number evaluateFunction(SymbolLookup c, Number... params) {
//        assert params.length <= paramNames.length;
//
//        for (int i = 0; i < paramNames.length; i++)
//            c.pushLocal(paramNames[i], i < params.length ? params[i] : SymbolLookup.UNSPECIFIED);
//
//        Number result = Expression.evaluate(expr, c);
//
//        for (String paramName : paramNames)
//            c.popLocal(paramName);
//        return result;
//    }
//
//
//    @Override
//    public @NotNull FunctionImpl1 negate() {
//        return new FunctionImpl1(expr.negate(), paramNames);
//    }
//
//    @Override
//    public Expression apply(String format, UnaryOperator<Number> function) {
//        return new FunctionImpl1(new SimpleUnaryOperation(format, Expression.of(expr), function));
//    }
//
//    @Override
//    public Expression apply(String format, Number x, BinaryOperator<Number> function) {
//        if(!(x instanceof FunctionImpl1 xf) || xf.paramNames.length != paramNames.length)
//            return new FunctionImpl1(function.apply(expr, x), paramNames);
//        return new FunctionImpl1(new BinaryOperation(format, this, xf, function, paramNames), paramNames);
//    }
//
//    private record BinaryOperation(String format, FunctionImpl1 a, FunctionImpl1 b, BinaryOperator<Number> function, String[] paramNames) implements Expression {
//        @Override
//        public Number evaluate(SymbolLookup c) {
//            Number[] params = new Number[paramNames.length];
//            for (int i = 0; i < params.length; i++)
//                params[i] = c.get(paramNames[i]);
//            return function.apply(a.evaluateFunction(c, params), b.evaluateFunction(c, params));
//        }
//
//        @Override
//        public String toString() {
//            return format.replace("$1", a.toString()).replace("$2", b.toString());
//        }
//    }
//}
