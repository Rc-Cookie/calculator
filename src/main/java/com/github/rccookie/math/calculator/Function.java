package com.github.rccookie.math.calculator;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

import org.jetbrains.annotations.NotNull;

public record Function(Number expr, String... paramNames) implements Expression {

    public static final Function ABS = new Function("abs", Number::abs);
    public static final Function SQRT = new Function("sqrt", Number::sqrt);
    public static final Function HYPOT = new Function("hypot", Functions::hypot);
    public static final Function EXP = new Function("exp", Functions::exp);
    public static final Function LN = new Function("ln", Functions::ln);
    public static final Function LD = new Function("ld", Functions::ld);
    public static final Function LOG = new Function("log", Functions::log);
    public static final Function FACTORIAL = new Function("factorial", Functions::factorial);
    public static final Function MIN = new Function("min", (BinaryOperator<Number>) Functions::min);
    public static final Function MAX = new Function("max", (BinaryOperator<Number>) Functions::max);
    public static final Function FLOOR = new Function("floor", Functions::floor);
    public static final Function CEIL = new Function("ceil", Functions::ceil);
    public static final Function ROUND = new Function("round", Functions::round);
    public static final Function SIN = new Function("sin", Functions::sin);
    public static final Function COS = new Function("cos", Functions::cos);
    public static final Function TAN = new Function("tan", Functions::tan);
    public static final Function ASIN = new Function("asin", Functions::asin);
    public static final Function ACOS = new Function("acos", Functions::acos);
    public static final Function ATAN = new Function("atan", Functions::atan);
    public static final Function ATAN2 = new Function("atan2", Functions::atan2);
    public static final Function GET = new Function("get", Functions::get);
    public static final Function SIZE = new Function("size", Functions::size);
    public static final Function CROSS = new Function("cross", Functions::cross);
    public static final Function RAD_TO_DEG = new Function("deg", Functions::radToDeg);
    public static final Function DEG_TO_RAD = new Function("rad", Functions::degToRad);
    public static final Function SUM = new Function(Expression.named("\u03A3", Functions::sum), "$low", "$high", "$f");
    public static final Function PRODUCT = new Function(Expression.named("\u03A0", Functions::product), "$low", "$high", "$f");



    public Function(String name, BinaryOperator<Number> function) {
        this(Expression.named(name, e -> function.apply(
                e.getVar("a"),
                e.getVar("b")
        )), "a", "b");
    }

    public Function(String name, UnaryOperator<Number> function) {
        this(Expression.named(name, e -> function.apply(e.getVar("x"))), "x");
    }

    public Function(String name, Function inner, UnaryOperator<Number> outer) {
        this(Expression.named(name, e -> outer.apply(Expression.evaluate(inner.expr, e))), inner.paramNames);
    }

    @Override
    public String toString() {
        return expr.toString();/*"Function[" +
                "expr=" + expr +
                ", paramNames=" + Arrays.toString(paramNames) +
                ']';*/
    }

    @Override
    public Number evaluate(Calculator calculator) {
        return this;
    }

    public Number evaluate(Calculator calculator, Number params) {
        if(!(params instanceof List l))
            return evaluateFunction(calculator, params);
        if(l.size() <= paramNames.length)
            return evaluateFunction(calculator, l.evaluateToArray(calculator));
        if(paramNames.length == 1) {
            Number[] results = new Number[l.size()];
            for (int i = 0; i < results.length; i++)
                results[i] = evaluateFunction(calculator, l.evaluate(calculator, i));
            return new List(results);
        }
        throw new IllegalArgumentException("Too many arguments (" + l.size() + ") applied to function, expected " + paramNames.length);
    }

    private Number evaluateFunction(Calculator c, Number... params) {
        assert params.length <= paramNames.length;

        for (int i = 0; i < paramNames.length; i++)
            c.addLocalVar(paramNames[i], i < params.length ? params[i] : Calculator.UNSPECIFIED);

        Number result = Expression.evaluate(expr, c);

        for (String paramName : paramNames)
            c.removeLocalVar(paramName);
        return result;
    }


    @Override
    public @NotNull Function negate() {
        return new Function(expr.negate(), paramNames);
    }

    @Override
    public Expression apply(String format, UnaryOperator<Number> function) {
        return new Function(new ExpressionUnaryOperation(format, Expression.of(expr), function));
    }

    @Override
    public Expression apply(String format, Number x, BinaryOperator<Number> function) {
        if(!(x instanceof Function xf) || xf.paramNames.length != paramNames.length)
            return new Function(function.apply(expr, x), paramNames);
        return new Function(new BinaryOperation(format, this, xf, function, paramNames), paramNames);
    }

    private record BinaryOperation(String format, Function a, Function b, BinaryOperator<Number> function, String[] paramNames) implements Expression {
        @Override
        public Number evaluate(Calculator c) {
            Number[] params = new Number[paramNames.length];
            for (int i = 0; i < params.length; i++)
                params[i] = c.getVar(paramNames[i]);
            return function.apply(a.evaluateFunction(c, params), b.evaluateFunction(c, params));
        }

        @Override
        public String toString() {
            return format.replace("$1", a.toString()).replace("$2", b.toString());
        }
    }
}
