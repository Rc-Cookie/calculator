package com.github.rccookie.math.interpreter;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;

import org.jetbrains.annotations.NotNull;

public record Function(Number expr, String... paramNames) implements Expression {

    public static final Function ABS = new Function(Number::abs);
    public static final Function SQRT = new Function(Number::sqrt);
    public static final Function EXP = new Function(Functions::exp);
    public static final Function LN = new Function(Functions::ln);
    public static final Function LOG = new Function(Functions::log);
    public static final Function FACTORIAL = new Function(Functions::factorial);
    public static final Function MIN = new Function((BinaryOperator<Number>) Functions::min);
    public static final Function MAX = new Function((BinaryOperator<Number>) Functions::max);
    public static final Function FLOOR = new Function(Functions::floor);
    public static final Function CEIL = new Function(Functions::ceil);
    public static final Function ROUND = new Function(Functions::round);
    public static final Function SIN = new Function(Functions::sin);
    public static final Function COS = new Function(Functions::cos);
    public static final Function TAN = new Function(Functions::tan);
    public static final Function ASIN = new Function(Functions::asin);
    public static final Function ACOS = new Function(Functions::acos);
    public static final Function ATAN = new Function(Functions::atan);
    public static final Function ATAN2 = new Function(Functions::atan2);
    public static final Function GET = new Function(Functions::get);
    public static final Function SIZE = new Function(Functions::size);
    public static final Function CROSS = new Function(Functions::cross);
    public static final Function RAD_TO_DEG = new Function(Functions::radToDeg);
    public static final Function DEG_TO_RAD = new Function(Functions::degToRad);
    public static final Function SUM = new Function((Expression) Functions::sum, "$low", "$high", "$f");
    public static final Function PRODUCT = new Function((Expression) Functions::product, "$low", "$high", "$f");



    public Function(BinaryOperator<Number> function) {
        this((Expression) e -> function.apply(
                e.getVar("$a"),
                e.getVar("$b")
        ), "$a", "$b");
    }

    public Function(UnaryOperator<Number> function) {
        this((Expression) e -> function.apply(e.getVar("$x")), "$x");
    }

    public Function(Function inner, UnaryOperator<Number> outer) {
        this((Expression) e -> outer.apply(Expression.evaluate(inner.expr, e)), inner.paramNames);
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
            for(int i=0; i<params.length; i++)
                params[i] = c.getVar(paramNames[i]);
            return function.apply(a.evaluateFunction(c, params), b.evaluateFunction(c, params));
        }

        @Override
        public String toString() {
            return format.replace("$1", a.toString()).replace("$2", b.toString());
        }
    }
}
