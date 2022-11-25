package com.github.rccookie.math.solve;

import com.github.rccookie.math.expr.Expression;

public interface Polynom extends Expression.Function {

    int degree(int indeterminant);

    default int degree() {
        int d = 0;
        for(int i=0; i<indeterminantCount(); i++)
            d += degree(i);
        return d;
    }

    default int maxDegree() {
        int d = 0;
        for(int i=0; i<indeterminantCount(); i++)
            d = Math.max(d, degree(i));
        return d;
    }

    Expression getCoefficient(int n);

    Expression[] coefficients();

    String[] indeterminants();

    int indeterminantCount();

    Polynom derivative();

    Polynom antiderivative();

    default Polynom derivative(int n) {
        Polynom d = this;
        for(; n>0; n--)
            d = d.derivative();
        for(; n<0; n++)
            d = d.antiderivative();
        return d;
    }

    default Polynom antiderivative(int n) {
        return derivative(-n);
    }

    @Override
    default String[] paramNames() {
        return indeterminants();
    }

    @Override
    default int paramCount() {
        return indeterminantCount();
    }

    @Override
    default String name() {
        return "polynom";
    }

    @Override
    default int operandCount() {
        return indeterminantCount() + maxDegree();
    }

    @Override
    default Expression[] operands() {
        String[] indeterminants = indeterminants();
        Expression[] coefficients = coefficients();
        Expression[] operands = new Expression[indeterminants.length + coefficients.length];
        int indCount = indeterminantCount();
        for(int i=0; i<indCount; i++)
            operands[i] = Symbol.of(indeterminants[i]);
        for(int i=0; i<coefficients.length; i++) {
            Expression operand = coefficients[i];
            for(int j=0; j<indCount; j++)
                operand = operand.multiply(operands[j].raise(i));
            operands[i + indCount] = operand;
        }
        return operands;
    }

    @Override
    default Expression expr() {
        Expression[] operands = operands();
        int indCount = indeterminantCount();
        if(operands.length == indCount)
            return Expression.ZERO();
        Expression expr = operands[operands.length-1];
        for(int i=operands.length - 2; i>=indCount; i--)
            expr = expr.add(operands[i]);
        return expr;
    }
}
