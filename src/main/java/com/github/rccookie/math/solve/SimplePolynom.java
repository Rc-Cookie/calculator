package com.github.rccookie.math.solve;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.Arguments;

record SimplePolynom(String indeterminant, Expression... coefficients) implements Polynom {

    SimplePolynom(String indeterminant, Expression... coefficients) {
        this.indeterminant = Arguments.checkNull(indeterminant, "indeterminant");
        int d = coefficients.length-1;
        while(d >= 0 && coefficients[d].equals(Number.ZERO())) d--;
        if(d <= 0)
            this.coefficients = new Expression[] { Expression.ZERO() };
        else this.coefficients = Arrays.copyOf(coefficients, d+1);
    }

    SimplePolynom(Expression... coefficients) {
        this("x", coefficients);
    }

    @Override
    public Number evaluate(SymbolLookup lookup, Number params) {
        Number res = coefficients[0];
        if(coefficients.length == 1)
            return res;
        Number x = lookup.get(indeterminant);
        for(int i=1; i<coefficients.length; i++)
            res = res.add(x.raise(i));
        return res;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(indeterminant).append(" -> ");
        for(int i=coefficients.length-1; i>0; i--)
            if(!coefficients[i].equals(Number.ZERO()))
                str.append('(').append(coefficients[i]).append(')').append(indeterminant).append('^').append(i).append(" + ");
        if(str.isEmpty())
            str.append(coefficients[0]);
        else if(coefficients[0].equals(Number.ZERO()))
            str.delete(str.length() - 3, str.length());
        else str.append(coefficients[0]);

        return str.toString();
    }

    @Override
    public Function derive(String name, String format, Expression b, BinaryOperator<Number> operator) {
        return null;
    }

    @Override
    public Function derive(String name, String format, UnaryOperator<Number> operator) {
        return null;
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
    public Polynom derivative() {
        if(coefficients.length == 1)
            return new SimplePolynom();
        Expression[] derivative = new Expression[coefficients.length - 1];
        for(int i=0; i<derivative.length; i++) {

        }
        return null;
    }

    @Override
    public Polynom antiderivative() {
        return null;
    }
}
