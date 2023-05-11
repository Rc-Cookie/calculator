package com.github.rccookie.math.solve;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.Precedence;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.math.rendering.RenderableExpression;

record LinearFunctionExpr(LinearFunction equation) implements Expression {

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public String toString() {
        Number offset = equation.offset();
        if(operandCount() == 1)
            return offset.toString();
        StringBuilder str = new StringBuilder();
        Expression[] coeffs = equation.coefficients();
        String[] indeterminants = equation.indeterminants();
        for(int i=0; i<coeffs.length; i++) {
            if(!coeffs[i].equals(Number.ZERO())) {
                if(!str.isEmpty()) str.append(" + ");
                str.append(coeffs[i].multiply(Symbol.of(indeterminants[i])));
            }
        }
        if(str.isEmpty())
            return offset.toString();
        if(!offset.equals(Number.ZERO()))
            str.append(" + ").append(offset);
        return str.toString();
    }

    @Override
    public RenderableExpression toRenderable() {
        return null;
    }

    @Override
    public Number evaluate(SymbolLookup lookup) {
        Number result = equation.offset().evaluate(lookup);
        Expression[] coeffs = equation.coefficients();
        String[] indeterminants = equation.indeterminants();
        for(int i=0; i<coeffs.length; i++)
            result = result.add(coeffs[i].evaluate(lookup).multiply(lookup.get(indeterminants[i])));
        return result;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public int operandCount() {
        return equation.indeterminantCount() + 1;
    }

    @Override
    public Expression[] operands() {

        Expression[] operands = new Expression[operandCount()];
        Expression[] coeffs = equation.coefficients();
        String[] indeterminants = equation.indeterminants();

        for(int i=0; i<indeterminants.length; i++)
            operands[i] = coeffs[i].multiply(Symbol.of(indeterminants[i]));
        operands[operands.length-1] = equation.offset();

        return operands;
    }

    @Override
    public String name() {
        return equation.name();
    }

    @Override
    public int precedence() {
        return operandCount() == 1 ? equation.offset().precedence() : Precedence.PLUS;
    }
}
