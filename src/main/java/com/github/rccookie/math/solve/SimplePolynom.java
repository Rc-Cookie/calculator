package com.github.rccookie.math.solve;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.Arguments;

record SimplePolynom(String indeterminant, Expression... coefficients) implements Polynom {

    SimplePolynom(String indeterminant, Expression... coefficients) {
        this.indeterminant = Arguments.checkNull(indeterminant, "indeterminants");
        Arguments.deepCheckNull(coefficients);
        int d = coefficients.length-1;
        while(d >= 0 && coefficients[d].equals(Number.ZERO())) d--;
        if(d < 0)
            this.coefficients = new Expression[] { Expression.ZERO() };
        else this.coefficients = Arrays.copyOf(coefficients, d+1);
    }

    SimplePolynom(Expression... coefficients) {
        this("x", coefficients);
    }

    @Override
    public Number evaluate(SymbolLookup lookup, Number params) {
        Number x;
        if(params instanceof Numbers n) {
            if(n.size() > 1) {
                Expression[] results = new Expression[n.size()];
                for(int i=0; i<results.length; i++)
                    results[i] = Expression.of(evaluate(lookup, n.get(i)));
                return Numbers.of(results);
            }
            x = n.get(0);
        }
        else x = Expression.evaluate(params, lookup);

        Number res = coefficients[0].evaluate(lookup);
        for(int i=1; i<coefficients.length; i++)
            res = res.add(coefficients[i].evaluate(lookup).multiply(x.raise(i)));
        return res;
    }

    @Override
    public Function simplify() {
        return new SimplePolynom(indeterminant, Arrays.stream(coefficients).map(Expression::simplify).toArray(Expression[]::new));
    }

    @Override
    public String toString() {
        if(coefficients.length == 1 && coefficients[0].equals(Number.ZERO()))
            return indeterminant + " -> 0";
        return indeterminant + " -> " + Stream.iterate(coefficients.length-1, i->i-1)
                .limit(coefficients.length)
                .map(this::coeffToString)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" + "));
    }

    private String coeffToString(int coeff) {
        Expression c = coefficients[coeff];
        if(c.equals(Number.ZERO())) return null;
        String str = "";
        if(coeff == 0 || !c.equals(Number.ONE())) {
            if(c instanceof Numeric)
                str = c.toString();
            else str = "(" + c + ")";
        }
        if(coeff != 0) {
            str += indeterminant;
            if(coeff == 2) str += '\u00B2';
            else if(coeff == 3) str += '\u00B3';
            else if(coeff != 1) str += "^" + coeff;
        }
        return str;
    }

    @Override
    public int degree(int indeterminant) {
        Arguments.checkRange(indeterminant, 0, 1);
        return (coefficients.length == 1 && coefficients[0].equals(Number.ZERO())) ? 0 : coefficients.length + 1;
    }

    @Override
    public Expression getCoefficient(int n) {
        Arguments.checkRange(n, 0, null);
        if(n >= coefficients.length)
            return Expression.ZERO();
        return coefficients[n];
    }

    @Override
    public String[] indeterminants() {
        return new String[] { indeterminant };
    }

    @Override
    public int indeterminantCount() {
        return 1;
    }

    @Override
    public Polynom derivative(int indeterminant) {
        Arguments.checkRange(indeterminant, 0, 1);
        if(coefficients.length == 1)
            return new SimplePolynom(this.indeterminant);
        Expression[] derivative = new Expression[coefficients.length - 1];
        for(int i=0; i<derivative.length; i++)
            derivative[i] = (Expression) coefficients[i+1].multiply(i+1);
        return new SimplePolynom(this.indeterminant, derivative);
    }

    @Override
    public Polynom antiderivative(int indeterminant) {
        Arguments.checkRange(indeterminant, 0, 1);
        Expression[] antiderivative = new Expression[coefficients.length + 1];
        antiderivative[0] = Expression.ZERO();
        for(int i=1; i<antiderivative.length; i++)
            antiderivative[i] = (Expression) coefficients[i-1].divide(i);
        return new SimplePolynom(this.indeterminant, antiderivative);
    }


//    @Override
//    public @NotNull BinaryFunctionOperation add(Number x) {
//        if(x instanceof Polynom p && p.indeterminantCount() == 1)
//            return deriveLinear(p, Expression::add);
//        if(x instanceof Expression e && Polynom.containsIndeterminant(e, indeterminant))
//            return Polynom.super.add(x);
//        Expression[] c = coefficients.clone();
//        c[0] = c[0].add(x);
//        return new SimplePolynom(c);
//    }
//
//    @Override
//    public @NotNull Expression subtract(Number x) {
//        if(x instanceof Polynom p && p.indeterminantCount() == 1)
//            return deriveLinear(p, Expression::subtract);
//        if(x instanceof Expression e && Polynom.containsIndeterminant(e, indeterminant))
//            return Polynom.super.subtract(x);
//        Expression[] c = coefficients.clone();
//        c[0] = c[0].subtract(x);
//        return new SimplePolynom(c);
//    }
//
//    @Override
//    public @NotNull Expression subtractFrom(Number x) {
//        if(x instanceof Polynom p && p.indeterminantCount() == 1)
//            return deriveLinear(p, Expression::subtractFrom);
//        if(x instanceof Expression e && Polynom.containsIndeterminant(e, indeterminant))
//            return Polynom.super.subtractFrom(x);
//        Expression[] c = coefficients.clone();
//        c[0] = c[0].subtractFrom(x);
//        return new SimplePolynom(c);
//    }
//
//    @Override
//    public @NotNull Expression multiply(Number x) {
//        return Polynom.super.multiply(x);
//    }
//
//    @Override
//    public @NotNull Expression divide(Number x) {
//        return Polynom.super.divide(x);
//    }
//
//    @Override
//    public @NotNull Expression divideOther(Number x) {
//        return Polynom.super.divideOther(x);
//    }



    private Polynom deriveLinear(UnaryOperator<Expression> operator) {
        Expression[] coeffs = coefficients.clone();
        for(int i=0; i<coeffs.length; i++)
            coeffs[i] = operator.apply(coeffs[i]);
        return new SimplePolynom(indeterminant, coeffs);
    }

    private Polynom deriveLinear(Polynom p, BinaryOperator<Expression> operator) {
        Expression[] coeffs = new Expression[Math.max(coefficients.length, p.maxDegree() + 1)];
        for(int i=0; i<coeffs.length; i++)
            coeffs[i] = operator.apply(getCoefficient(i), p.getCoefficient(i));
        return new SimplePolynom(indeterminant, coeffs);
    }
}
